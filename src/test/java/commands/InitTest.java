package commands;

import git.IssueRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitTest {

    @Test
    void testIssueBranchDoesntExist() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        when(issueRepo.init()).thenReturn(true);
        Command command = new CommandInit(issueRepo);
        command.execute();
        assertEquals("Issue repository created" + System.lineSeparator(), out.toString());
    }

    @Test
    void testFailToInit() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        when(issueRepo.init()).thenReturn(false);
        Command command = new CommandInit(issueRepo);
        command.execute();
        assertEquals("Failed to create issue repository" + System.lineSeparator(), out.toString());

    }

    @Test
    void testIssueBranchExists() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        Command command = new CommandInit(issueRepo);
        command.execute();
        assertEquals("Issue branch already exists" + System.lineSeparator() + "Failed to create issue repository" + System.lineSeparator(), out.toString());
    }

    @Test
    void testException() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        when(issueRepo.init()).thenThrow(IOException.class);
        Command command = new CommandInit(issueRepo);
        command.execute();
        assertFalse(out.toString().isEmpty());

    }

}
