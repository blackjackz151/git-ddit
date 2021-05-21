package commands;

import git.IssueRepo;
import issueData.Comment;
import issueData.Issue;
import issueData.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import util.Util;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowTest {


    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandShow(issueRepo, "hash");
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }

    @Test
    void testShowNoComments() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setCreator(new User("test", "testUser"));
        issue.setHash("hash");
        Util.printIssue(issue);

        String issueString = out.toString();

        out.reset();

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));

        Command command = new CommandShow(issueRepo, "hash");
        command.execute();
        assertEquals(issueString + "\nNo comments on this issue" + System.lineSeparator(), out.toString());
    }

    @Test
    void testShowComments() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Issue issue = new Issue("test", "issue");
        issue.setCreator(new User("test", "testUser"));
        issue.setHash("hash");
        Comment comment = new Comment(new User("test", "testUser"), "message");
        issue.addComment(comment);
        Util.printIssue(issue);

        String issueString = out.toString();

        out.reset();

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.of(issue));

        Command command = new CommandShow(issueRepo, "hash");
        command.execute();
        assertEquals(issueString + "\nComments:\n" + System.lineSeparator() +
                "Author: " + "User Name: " + comment.getAuthor().name + " Email: " + comment.getAuthor().email + "\n" + comment.getMessage() + "\n"
                + System.lineSeparator(), out.toString());
    }

    @Test
    void testNoIssue() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.getIssue(anyString())).thenReturn(Optional.empty());

        Command command = new CommandShow(issueRepo, "hash");
        command.execute();
        assertEquals("Issue with given ID not found" + System.lineSeparator(), out.toString());
    }

}
