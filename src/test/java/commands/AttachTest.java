package commands;

import git.IssueRepo;
import issueData.Issue;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
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
class AttachTest {

    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandAttachment(issueRepo, "hash", "add", "path");
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulAddAttachment() throws IOException {
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

        Command command = new CommandAttachment(issueRepo, "hash", "add", "src/test/resources/testResource.txt");
        command.execute();
        assertEquals("Attachment " + "testResource.txt" + " added to issue " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testInvalidFileAddAttach() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));

        Command command = new CommandAttachment(issueRepo, "hash", "add", "invalid");
        command.execute();
        assertEquals("File not found" + System.lineSeparator(), out.toString());
    }

    @Test
    void testInvalidFileAddAttach2() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));

        Command command = new CommandAttachment(issueRepo, "hash", "add", "src/test/resources/");
        command.execute();
        assertEquals("File not found" + System.lineSeparator(), out.toString());
    }

    @Test
    void testInvalidFileAddAttach3() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));

        Command command = new CommandAttachment(issueRepo, "hash", "add", "src/test/resources/oversized.zip");
        command.execute();
        assertEquals("File too large, make sure under 5MB" + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulRmAttachment() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.addAttachment("testResource.txt", IOUtils.toByteArray(new File("src/test/resources/testResource.txt").toURI()));
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));
        when(issueRepo.getIssues()).thenReturn(issueList);
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandAttachment(issueRepo, "hash", "rm", "testResource.txt");
        command.execute();
        assertEquals("Attachment " + "testResource.txt" + " removed from issue " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testUnsuccessfulRmAttachment() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));


        Command command = new CommandAttachment(issueRepo, "hash", "rm", "testResource.txt");
        command.execute();
        assertEquals("File not found" + System.lineSeparator(), out.toString());
    }

    @Test
    void testInvalidOption() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));


        Command command = new CommandAttachment(issueRepo, "hash", "invalid", "path");
        command.execute();
        assertEquals("Invalid option - add or rm only" + System.lineSeparator(), out.toString());
    }

    @Test
    void testNoIssue() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.empty());

        Command command = new CommandAttachment(issueRepo, "hash", "invalid", "path");
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

        Command command = new CommandAttachment(issueRepo, "hash", "add", "src/test/resources/testResource.txt");
        command.execute();
        assertFalse(out.toString().isEmpty());
    }


}
