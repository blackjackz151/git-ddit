package commands;

import git.IssueRepo;
import issueData.Issue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTest {


    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandDelete(issueRepo, "hash");
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulDelete() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandDelete(issueRepo, "hash");
        command.execute();
        assertEquals("Issue deleted" + System.lineSeparator(), out.toString());
    }

    @Test
    void testUnsuccessfulDelete() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);

        Command command = new CommandDelete(issueRepo, "hash");
        command.execute();
        assertEquals("Issue doesn't exist" + System.lineSeparator(), out.toString());
    }

    @Test
    void testException() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenThrow(IOException.class);

        Command command = new CommandDelete(issueRepo, "hash");
        command.execute();
        assertFalse(out.toString().isEmpty());
    }


}
