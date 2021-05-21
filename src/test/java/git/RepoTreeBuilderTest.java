package git;

import issueData.Comment;
import issueData.Issue;
import issueData.IssueNicknames;
import issueData.User;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
class RepoTreeBuilderTest {
    private Repository repository;

    private List<Issue> issues;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException, IOException {
        Git git = Git.init().setDirectory(new File(tempDir.toString())).call();
        this.repository = git.getRepository();
        this.issues = buildFakeObjects();
    }

    @AfterEach
    void tearDown() {
        repository.close();
    }


    @Test
    void buildValidRepoTree() throws IOException {
        issues.sort(Comparator.comparing(Issue::getHash));
        IssueNicknames nicknames = new IssueNicknames(new HashMap<>());
        nicknames.addNickname("hash", "nickname");
        RepoTreeBuilder treeBuilder = new RepoTreeBuilder(repository);

        Optional<ObjectId> objectId = treeBuilder.getRepoTree(issues, nicknames);
        objectId.ifPresent(ob -> assertDoesNotThrow(() -> {
            byte[] rawBytes = repository.open(ob).getBytes(); //verifies tree for repo is added to repo
            new ObjectChecker().checkTree(ob, rawBytes); //incorrectly formatted trees cause exception to be thrown
        }));
    }

    @Test
    void buildInvalidRepoTree() throws IOException {
        issues.sort(Comparator.comparing(Issue::getHash).reversed()); //incorrectly formats tree to cause error

        IssueNicknames nicknames = new IssueNicknames(new HashMap<>());
        nicknames.addNickname("hash", "nickname");

        RepoTreeBuilder treeBuilder = new RepoTreeBuilder(repository);

        Optional<ObjectId> objectId = treeBuilder.getRepoTree(issues, nicknames);
        objectId.ifPresent(ob -> assertThrows(CorruptObjectException.class, () -> {
            byte[] rawBytes = repository.open(ob).getBytes();
            new ObjectChecker().checkTree(ob, rawBytes);
        }));
    }

    @Test
    void validateNicknames() throws IOException {
        IssueNicknames nicknames = new IssueNicknames(new HashMap<>());
        nicknames.addNickname("hash", "nickname");

        RepoTreeBuilder treeBuilder = new RepoTreeBuilder(repository);
        ObjectId blob = treeBuilder.writeNicknames(nicknames.getIssueNicknames());

        assertEquals("{\"hash\":\"nickname\"}", getContent(blob));
    }

    private List<Issue> buildFakeObjects() throws IOException {
        List<Issue> issues = new ArrayList<>();

        User user1 = new User("user1", "user1@email.com");
        User user2 = new User("user2", "user2@email.com");
        User user3 = new User("user3", "user3@email.com");

        Issue issue = new Issue("Basic1", "issue1");
        issue.setCreator(user1);
        issue.addAttachment("testFile1.txt", Files.readAllBytes(Paths.get("src/test/resources/testResource.txt")));
        issue.addComment(new Comment(user1, "This is a test comment on issue 1"));
        issue.setHash("a");
        issues.add(issue);

        Issue issue2 = new Issue("Basic2", "issue2");
        issue2.setCreator(user2);
        issue2.addAttachment("testFile2.txt", Files.readAllBytes(Paths.get("src/test/resources/testResource.txt")));
        issue2.addComment(new Comment(user2, "This is a test comment on issue 2"));
        issue2.setHash("b");
        issues.add(issue2);

        Issue issue3 = new Issue("Basic3", "issue3");
        issue3.setCreator(user3);
        issue3.addAttachment("testFile3.txt", Files.readAllBytes(Paths.get("src/test/resources/testResource.txt")));
        issue3.addComment(new Comment(user3, "This is a test comment on issue 3"));
        issue3.setHash("c");
        issue3.addAssignee(user2);
        issue3.addWatcher(user1);
        issues.add(issue3);

        return issues;

    }


    private String getContent(ObjectId blob) throws IOException {
        try (ObjectReader objectReader = repository.newObjectReader()) {
            ObjectLoader objectLoader = objectReader.open(blob);
            byte[] bytes = objectLoader.getBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

}
