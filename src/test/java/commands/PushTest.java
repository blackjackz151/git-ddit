package commands;

import git.IssueRepo;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushTest {

    @Test
    void testIssueBranchDoesntExist() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(false);
        Command command = new CommandPush(issueRepo, "remote");
        command.execute();
        assertEquals("Issue branch does not exist - need to run git ddit init" + System.lineSeparator(), out.toString());
    }

    @Test
    void testPush() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);
        when(issueRepo.push(anyString())).thenReturn(Optional.empty());
        Command command = new CommandPush(issueRepo, "remote");
        command.execute();
        assertEquals("Nothing was pushed to remote" + System.lineSeparator(), out.toString());
    }

    @Test
    void testPush2() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        List<PushResult> pushResults = new ArrayList<>();
        List<RemoteRefUpdate> remoteRefUpdates = new ArrayList<>();

        PushResult pr = mock(PushResult.class);
        RemoteRefUpdate refUpdate = mock(RemoteRefUpdate.class);

        remoteRefUpdates.add(refUpdate);
        pushResults.add(pr);


        when(refUpdate.getRemoteName()).thenReturn("remote");
        when(refUpdate.getStatus()).thenReturn(RemoteRefUpdate.Status.OK);
        when(pr.getRemoteUpdates()).thenReturn(remoteRefUpdates);

        IssueRepo issueRepo = mock(IssueRepo.class);
        when(issueRepo.issueBranchExists()).thenReturn(true);

        doReturn(Optional.of((Iterable<PushResult>) pushResults)).when(issueRepo).push(anyString());

        Command command = new CommandPush(issueRepo, "remote");
        command.execute();
        assertEquals("Pushing to remote ref: " + "remote" +
                "\n" + "Status: " + RemoteRefUpdate.Status.OK.toString() + System.lineSeparator(), out.toString());
    }

}
