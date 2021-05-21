package commands;

import git.IssueRepo;
import issueData.Issue;
import issueData.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.mockito.junit.jupiter.MockitoExtension;
import util.Util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(TempDirectory.class)
class LsTest {

    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandLs(issueRepo, new String[]{}, false);
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }


    @Test
    void testSuccessfulLs() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);

        Command command = new CommandLs(issueRepo, new String[]{"title:test"}, false);
        command.execute();
        assertEquals(issue.toString() + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulLsVerbose() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setCreator(new User("test", "testUser"));
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);
        Util.printIssue(issue);

        String issueString = out.toString();

        out.reset();

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);

        Command command = new CommandLs(issueRepo, new String[]{"title:test"}, true);
        command.execute();
        assertEquals(issueString, out.toString());
    }

    @Test
    void testNullArgs() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setCreator(new User("test", "testUser"));
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);

        Command command = new CommandLs(issueRepo, null, false);
        command.execute();
        assertEquals(issue.toString() + System.lineSeparator(), out.toString());
    }


    @Test
    void testSuccessfulLsCSVIssueAndComment(@TempDirectory.TempDir Path tempDir) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);

        Command command = new CommandLs(issueRepo, false, tempDir.toString(), new String[]{"title:test"}, tempDir.toString());
        command.execute();
        assertTrue(out.toString().contains(issue.toString() + System.lineSeparator()));
        assertTrue(out.toString().contains("Created CSV at: " + tempDir.toString() + "/issues-"));
        assertTrue(out.toString().contains("Created CSV at: " + tempDir.toString() + "/comments-"));
    }

    @Test
    void testSuccessfulLsCSVInvalidPath() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        java.io.ByteArrayOutputStream errOut = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(errOut));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);

        Command command = new CommandLs(issueRepo, false, "asd", new String[]{"title:test"}, "asd");
        command.execute();
        assertTrue(out.toString().contains(issue.toString() + System.lineSeparator()));
        assertTrue(errOut.toString().contains("File path not found, make sure path exists - CSV not created"));

    }


}
