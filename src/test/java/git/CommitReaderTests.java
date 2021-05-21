package git;

import issueData.Comment;
import issueData.Issue;
import issueData.IssueNicknames;
import issueData.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TempDirectory.class)
class CommitReaderTests {
    private Repository repository;
    private List<Issue> issues;
    private Map<String, String> nicknames;

    @BeforeAll
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException, IOException {
        Git git = Git.init().setDirectory(new File(tempDir.toString())).call();

        FileUtils.copyDirectory(new File("src/test/resources/fake-issues"), new File(tempDir.toString()));
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();

        CommitReader commitReader = new CommitReader(git.getRepository(), commit.getTree());

        this.issues = commitReader.getIssues();
        this.nicknames = commitReader.getNicknames();
        this.repository = git.getRepository();

    }


    @AfterAll
    void tearDown() {
        repository.close();
    }


    @Test
    void validateIssuesSize() {
        assertEquals(2, issues.size());
    }

    @Test
    void validateNicknamesSize() {
        assertEquals(1, nicknames.size());
    }

    @Test
    void validateHash() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        Issue issue2 = new Issue();
        issue2.setHash("03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c");
        Issue issue3 = new Issue();
        issue3.setHash("03548c2a70ca9e232487234ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c"); //made up

        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        assertTrue(issues.contains(issue2), "Issue that should be there, is not.");
        assertFalse(issues.contains(issue3), "Issue that shouldn't be, is there.");
    }

    @Test
    void validateComment() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");

        List<Comment> comments = issues.get(issues.indexOf(issue)).getComments();
        String name = "Jack Maclauchlan";
        String email = "jack.maclauchlan.2016@uni.strath.ac.uk";

        assertEquals(new User(name, email), comments.get(0).getAuthor());
        assertEquals(1, comments.size());
        assertEquals(1551656106311L, comments.get(0).getCreationTime());
        assertEquals("hello world", comments.get(0).getMessage());
        assertEquals("555f2e662659c91797cb54b5eb66d438502a47aad32bc697aa011a5bd0a2ccb7", comments.get(0).getHash());
    }

    @Test
    void validateAttachment() throws IOException {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");

        Map<String, byte[]> attachments = issues.get(issues.indexOf(issue)).getAttachments();

        assertEquals(1, attachments.size());
        assertTrue(attachments.containsKey("molly.jpg"));
        File file = new File("src/test/resources/fake-issues/7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/attachments/molly.jpg");
        assertArrayEquals(IOUtils.toByteArray(file.toURI()), attachments.get("molly.jpg"));

    }

    @Test
    void validateNicknames() {
        IssueNicknames names = new IssueNicknames(nicknames);
        assertTrue(names.getIssueNicknames().containsValue("issue"));
    }

    @Test
    void validateNicknames2() {
        IssueNicknames names = new IssueNicknames(nicknames);
        assertTrue(names.getIssueNicknames().containsKey("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db"));
    }

    @Test
    void validateDescriptions1() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals("world", issue.getDescription());
    }

    @Test
    void validateDescriptions2() {
        Issue issue = new Issue();
        issue.setHash("03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals("teest", issue.getDescription());
    }

    @Test
    void validateTitle1() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals("hello", issue.getTitle());
    }

    @Test
    void validateTitle2() {
        Issue issue = new Issue();
        issue.setHash("03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals("test", issue.getTitle());
    }


    @Test
    void validateCreationTime1() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals(1551653634761L, issue.getCreationTime());
    }

    @Test
    void validateCreationTime2() {
        Issue issue = new Issue();
        issue.setHash("03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals(1551655982088L, issue.getCreationTime());
    }

    @Test
    void validateEditTime1() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals(1551656257856L, issue.getEdited());
    }

    @Test
    void validateEditTime2() {
        Issue issue = new Issue();
        issue.setHash("03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertEquals(1551655982088L, issue.getEdited());
    }

    @Test
    void validateTags1() {
        Issue issue = new Issue();
        issue.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertTrue(issue.getTags().contains("test tag") && issue.getTags().contains("another"));
    }

    @Test
    void validateTags2() {
        Issue issue = new Issue();
        issue.setHash("03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c");
        assertTrue(issues.contains(issue), "Issue that should be there, is not.");
        issue = issues.get(issues.indexOf(issue));
        assertTrue(issue.getTags().isEmpty());
    }

    @Test
    void validateAssignees() {
        String name = "Jack Maclauchlan";
        String email = "jack.maclauchlan.2016@uni.strath.ac.uk";
        User user = new User(name, email);
        issues.forEach(issue -> assertTrue(issue.getAssignees().contains(user)));
    }


    @Test
    void validateWatchers() {
        String name = "Jack Maclauchlan";
        String email = "jack.maclauchlan.2016@uni.strath.ac.uk";
        User user = new User(name, email);
        issues.forEach(issue -> assertTrue(issue.getWatchers().contains(user)));
    }


    @Test
    void validateAuthor() {
        String name = "Jack Maclauchlan";
        String email = "jack.maclauchlan.2016@uni.strath.ac.uk";
        issues.forEach(issue -> assertEquals(new User(name, email), issue.getCreator()));
    }

    @Test
    void validateStatus() {
        issues.forEach(issue -> assertTrue(issue.getStatus()));
    }

}
