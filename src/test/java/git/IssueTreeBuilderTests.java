package git;

import issueData.Comment;
import issueData.Issue;
import issueData.User;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(TempDirectory.class)
class IssueTreeBuilderTests {

    private Repository repository;
    private Git git;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException {
        this.git = Git.init().setDirectory(new File(tempDir.toString())).call();
        this.repository = git.getRepository();
    }

    @AfterEach
    void tearDown() {
        repository.close();
    }

    @Test
    void verifyIssueTitle() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals("\"Basic\"", walkTree("title", tree));
    }

    @Test
    void verifyIssueDesc() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals("\"issue\"", walkTree("description", tree));
    }

    @Test
    void verifyCreationTime() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals(String.valueOf(issue.getCreationTime()), walkTree("creationTime", tree));
    }

    @Test
    void verifyEditTime() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals(String.valueOf(issue.getEdited()), walkTree("editTime", tree));
    }

    @Test
    void verifyStatus() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals(String.valueOf(issue.getStatus()), walkTree("status", tree));
    }

    @Test
    void verifyCreator() throws IOException {
        String name = Util.getUserName(repository);
        String email = Util.getUserEmail(repository);

        Issue issue = new Issue("Basic", "issue");
        issue.setCreator(new User(name, email));
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);

        assertEquals("{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}", walkTree("creator", tree));
    }

    @Test
    void verifyWatcher() throws IOException {
        String name = Util.getUserName(repository);
        String email = Util.getUserEmail(repository);

        Issue issue = new Issue("Basic", "issue");
        issue.addWatcher(new User(name, email));
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);

        assertEquals("[{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}]", walkTree("watchers", tree));
    }

    @Test
    void verifyAssignees() throws IOException {
        String name = Util.getUserName(repository);
        String email = Util.getUserEmail(repository);

        Issue issue = new Issue("Basic", "issue");
        issue.addAssignee(new User(name, email));
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);

        assertEquals("[{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}]", walkTree("assignees", tree));
    }

    @Test
    void verifyTags() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        issue.addTags("test tag, another");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);

        assertEquals("[\"test tag\",\"another\"]", walkTree("tags", tree));
    }

    @Test
    void verifyCommentAuthor() throws IOException {
        String name = Util.getUserName(repository);
        String email = Util.getUserEmail(repository);

        Issue issue = new Issue("Basic", "issue");
        issue.addComment(new Comment(new User(name, email), "message"));
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);

        assertEquals("{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}", walkTree("author", tree));
    }

    @Test
    void verifyCommentMessage() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        issue.addComment(new Comment(new User("test", "test"), "message"));
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals("\"message\"", walkTree("message", tree));
    }

    @Test
    void verifyCommentTime() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        issue.addComment(new Comment(new User("test", "test"), "message"));
        long creationTime = issue.getComments().get(0).getCreationTime();
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertEquals(String.valueOf(creationTime), walkTree("commentTime", tree));
    }

    @Test
    void verifyAttachment() throws IOException {
        Issue issue = new Issue("Basic", "issue");
        File file = new File("src/test/resources/testResource.txt");
        issue.addAttachment("testResource.txt", IOUtils.toByteArray(file.toURI()));
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        ObjectId tree = treeBuilder.buildIssueTree(issue);
        assertArrayEquals((IOUtils.toByteArray(file.toURI())), walkTreeAttachments(tree));
    }


    private String walkTree(String attrib, ObjectId tree) throws IOException {
        String content = "";
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                if (treeWalk.getNameString().contains(attrib)) {
                    ObjectId blob = treeWalk.getObjectId(0);
                    content = getContent(blob);
                }
            }
        }
        return content;
    }

    private byte[] walkTreeAttachments(ObjectId tree) throws IOException {
        byte[] content = new byte[]{};
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                if (treeWalk.getNameString().contains("testResource.txt")) {
                    ObjectId blob = treeWalk.getObjectId(0);
                    content = getByteContent(blob);
                }
            }
        }
        return content;
    }

    private byte[] getByteContent(ObjectId blob) throws IOException {
        try (ObjectReader objectReader = repository.newObjectReader()) {
            ObjectLoader objectLoader = objectReader.open(blob);
            return objectLoader.getBytes();
        }
    }

    private String getContent(ObjectId blob) throws IOException {
        try (ObjectReader objectReader = repository.newObjectReader()) {
            ObjectLoader objectLoader = objectReader.open(blob);
            byte[] bytes = objectLoader.getBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    @Test
    void buildIssueTreeDefault() {
        Issue issue = new Issue("Basic", "issue");
        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        assertDoesNotThrow(() -> {
            ObjectId objectId = treeBuilder.buildIssueTree(issue);
            byte[] rawBytes = repository.open(objectId).getBytes();
            new ObjectChecker().checkTree(objectId, rawBytes); //incorrectly formatted trees cause exception to be thrown
        });
    }

    @Test
    void buildIssueTreeWithComments() {
        User user1 = new User("user1", "user1@email.com");
        Issue issue = new Issue("Basic", "issue");
        issue.setCreator(user1);
        issue.addComment(new Comment(user1, "This is a test comment"));

        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        assertDoesNotThrow(() -> {
            ObjectId objectId = treeBuilder.buildIssueTree(issue);
            byte[] rawBytes = repository.open(objectId).getBytes(); //verifies that tree added to repo
            new ObjectChecker().checkTree(objectId, rawBytes); //incorrectly formatted trees cause exception to be thrown
        });
    }


    @Test
    void buildIssueTreeWithAttachments() throws IOException {
        User user1 = new User("user1", "user1@email.com");
        Issue issue = new Issue("Basic", "issue");
        issue.setCreator(user1);
        issue.addAttachment("testFile.txt", Files.readAllBytes(Paths.get("src/test/resources/testResource.txt")));

        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        assertDoesNotThrow(() -> {
            ObjectId objectId = treeBuilder.buildIssueTree(issue);
            byte[] rawBytes = repository.open(objectId).getBytes(); //verifies that tree added to repo
            new ObjectChecker().checkTree(objectId, rawBytes); //incorrectly formatted trees cause exception to be thrown
        });
    }

    @Test
    void buildIssueTreeWithCommentsAttachments() throws IOException {
        User user1 = new User("user1", "user1@email.com");
        Issue issue = new Issue("Basic", "issue");
        issue.setCreator(user1);
        issue.addAttachment("testFile.txt", Files.readAllBytes(Paths.get("src/test/resources/testResource.txt")));
        issue.addComment(new Comment(user1, "This is a test comment"));

        IssueTreeBuilder treeBuilder = new IssueTreeBuilder(repository);
        assertDoesNotThrow(() -> {
            ObjectId objectId = treeBuilder.buildIssueTree(issue);
            byte[] rawBytes = repository.open(objectId).getBytes(); //verifies that tree added to repo
            new ObjectChecker().checkTree(objectId, rawBytes); //incorrectly formatted trees cause exception to be thrown
        });
    }


}
