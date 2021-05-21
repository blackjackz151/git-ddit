package commands;

import git.IssueRepo;
import issueData.Issue;
import issueData.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatcherTest {


    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandWatcher(issueRepo, "hash", "add", "name", "email");
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }


    @Test
    void testSuccessfulAddAssign() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandWatcher(issueRepo, "hash", "add", "name", "email");
        command.execute();
        assertEquals("Watcher " + "name" + " " + "email" + " added to issue " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testUnsuccessfulAddAssign() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenReturn(false);

        Command command = new CommandWatcher(issueRepo, "hash", "add", "name", "email");
        command.execute();
        assertEquals("Issue not updated." + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulRemoveAssign() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandWatcher(issueRepo, "hash", "rm", "name", "email");
        command.execute();
        assertEquals("Watcher " + "name" + " " + "email" + " removed from issue " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testUnsuccessfulRmAssign() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenReturn(false);

        Command command = new CommandWatcher(issueRepo, "hash", "rm", "name", "email");
        command.execute();
        assertEquals("Issue not updated." + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulLsAssign() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.addWatcher(new User("test", "user"));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));


        Command command = new CommandWatcher(issueRepo, "hash", "ls", "name", "email");
        command.execute();
        assertEquals("Name: " + "test" + "Email: " + "user" + System.lineSeparator(), out.toString());
    }

    @Test
    void testInvalidOption() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.addAssignee(new User("test", "user"));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));


        Command command = new CommandWatcher(issueRepo, "hash", "invalid", "name", "email");
        command.execute();
        assertEquals("Invalid option - add, ls or rm only" + System.lineSeparator(), out.toString());
    }

    @Test
    void testNoIssue() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.empty());

        Command command = new CommandWatcher(issueRepo, "hash", "invalid", "name", "email");
        command.execute();
        assertEquals("Issue with given ID not found" + System.lineSeparator(), out.toString());
    }

    @Test
    void testException() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenThrow(IOException.class);

        Command command = new CommandWatcher(issueRepo, "hash", "rm", "name", "email");
        command.execute();

        assertFalse(out.toString().isEmpty());
    }


}
