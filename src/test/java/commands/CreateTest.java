package commands;

import git.IssueRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTest {

    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command createCommand = new CommandCreate(issueRepo, "Hello", "world");
        createCommand.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulWrite() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(new ArrayList<>());
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command createCommand = new CommandCreate(issueRepo, "Hello", "world");
        createCommand.execute();
        assertTrue(out.toString().contains("Issue created with hash: "));
    }


    @Test
    void testUnsuccessfulWrite() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(new ArrayList<>());
        when(issueRepo.writeRepo(anyString())).thenReturn(false);

        Command createCommand = new CommandCreate(issueRepo, "Hello", "world");
        createCommand.execute();
        assertEquals("Failed to create issue" + System.lineSeparator(), out.toString());
    }

    @Test
    void testException() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssues()).thenReturn(new ArrayList<>());
        when(issueRepo.writeRepo(anyString())).thenThrow(IOException.class);
        Command createCommand = new CommandCreate(issueRepo, "Hello", "world");
        createCommand.execute();
        assertFalse(out.toString().isEmpty());
    }


}
