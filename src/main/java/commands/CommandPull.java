package commands;

import git.IssueRepo;

public final class CommandPull implements Command {
    private final IssueRepo issueRepo;
    private final String remote;

    CommandPull(IssueRepo issueRepo, String remote) {
        this.issueRepo = issueRepo;
        this.remote = remote;
    }

    public void execute() {
        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        issueRepo.pull(remote);
    }
}
