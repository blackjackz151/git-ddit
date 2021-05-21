package commands;

import git.IssueRepo;
import issueData.Issue;
import issueData.IssueNicknames;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NicknameTest {

    @Test
    void testInvalidArgs() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        Command command = new CommandNickname(issueRepo, "add", new String[]{});
        command.execute();
        assertEquals("invalid args" + System.lineSeparator(), out.toString());
    }

    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandNickname(issueRepo, "add", new String[]{"one", "two"});
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }

    @Test
    void testNicknameListEmpty() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueNicknames issueNicknames = new IssueNicknames(new HashMap<>());

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(issueNicknames);
        Command command = new CommandNickname(issueRepo, "list", new String[]{});
        command.execute();
        assertTrue(out.toString().isEmpty());
    }

    @Test
    void testNicknameList() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueNicknames issueNicknames = new IssueNicknames(new HashMap<>());
        issueNicknames.addNickname("hash", "nickname");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(issueNicknames);
        Command command = new CommandNickname(issueRepo, "list", new String[]{});
        command.execute();
        assertEquals("Hash: " + "hash" + " has nickname \"" + "nickname" + "\"" + System.lineSeparator(), out.toString());
    }

    @Test
    void nullCommand() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        Command command = new CommandNull();
        command.execute();
        assertTrue(out.toString().isEmpty());
    }

    @Test
    void testNoIssue() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.empty());

        Command command = new CommandNickname(issueRepo, "add", new String[]{"1", "2"});
        command.execute();
        assertEquals("Issue with given ID not found" + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulAddNickname() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(new IssueNicknames(new HashMap<>()));
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.empty());
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandNickname(issueRepo, "add", new String[]{"hash", "nickname"});
        command.execute();
        assertEquals("Nickname added to " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testUnsuccessfulAddNickname() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(new IssueNicknames(new HashMap<>()));
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.empty());
        when(issueRepo.writeRepo(anyString())).thenReturn(false);

        Command command = new CommandNickname(issueRepo, "add", new String[]{"hash", "nickname"});
        command.execute();
        assertEquals("Failed to " + "add" + " nickname" + System.lineSeparator(), out.toString());
    }


    @Test
    void testNicknameInUse() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(new IssueNicknames(new HashMap<>()));
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.of(new Issue()));


        Command command = new CommandNickname(issueRepo, "add", new String[]{"hash", "nickname"});
        command.execute();
        assertEquals("Nickname already linked to an issue" + System.lineSeparator(), out.toString());
    }

    @Test
    void testSuccessfulUpdateNickname() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueNicknames nicknames = mock(IssueNicknames.class);
        when(nicknames.updateNickname(anyString(), anyString())).thenReturn(true);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(nicknames);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.empty());
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandNickname(issueRepo, "update", new String[]{"hash", "newnickname"});
        command.execute();
        assertEquals("Nickname updated for " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testUnsuccessfulUpdateNickname() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueNicknames nicknames = mock(IssueNicknames.class);
        when(nicknames.updateNickname(anyString(), anyString())).thenReturn(false);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(nicknames);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.empty());

        Command command = new CommandNickname(issueRepo, "update", new String[]{"hash", "newnickname"});
        command.execute();
        assertEquals("Failed to " + "update" + " nickname" + System.lineSeparator(), out.toString());
    }


    @Test
    void testException() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueNicknames nicknames = mock(IssueNicknames.class);
        when(nicknames.updateNickname(anyString(), anyString())).thenReturn(true);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(nicknames);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.empty());
        when(issueRepo.writeRepo(anyString())).thenThrow(IOException.class);

        Command command = new CommandNickname(issueRepo, "update", new String[]{"hash", "newnickname"});
        command.execute();

        assertFalse(out.toString().isEmpty());
    }

    @Test
    void testSuccessfulRmNickname() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setHash("hash");

        IssueNicknames nicknames = mock(IssueNicknames.class);
        when(nicknames.removeNickname(anyString())).thenReturn(true);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getNicknames()).thenReturn(nicknames);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue)).thenReturn(Optional.empty());
        when(issueRepo.writeRepo(anyString())).thenReturn(true);

        Command command = new CommandNickname(issueRepo, "remove", new String[]{"hash"});
        command.execute();
        assertEquals("Nickname remove from " + "hash" + System.lineSeparator(), out.toString());
    }

    @Test
    void testInvalidOption() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));


        Command command = new CommandNickname(issueRepo, "invalid", new String[]{"ok"});
        command.execute();
        assertEquals("Invalid option - add, update or remove only" + System.lineSeparator(), out.toString());
    }


}
