package commands;

import git.IssueRepo;
import org.eclipse.jgit.transport.PushResult;

import java.util.Optional;

public final class CommandPush implements Command {

    private final IssueRepo issueRepo;
    private final String remote;

    CommandPush(IssueRepo issueRepo, String remote) {
        this.issueRepo = issueRepo;
        this.remote = remote;
    }

    public void execute() {

        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        Optional<Iterable<PushResult>> results = issueRepo.push(remote);

        results.ifPresentOrElse(pushResults -> pushResults.forEach(pr -> {
                    pr.getRemoteUpdates().forEach(refUpdate ->
                            System.out.println("Pushing to remote ref: " + refUpdate.getRemoteName() +
                                    "\n" + "Status: " + refUpdate.getStatus()));
                }),
                () -> System.out.println("Nothing was pushed to remote"));

    }
}

