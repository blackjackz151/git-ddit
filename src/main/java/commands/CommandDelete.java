package commands;

import git.IssueRepo;

import java.io.IOException;

public final class CommandDelete implements Command {

    private final IssueRepo issueRepo;
    private final String hash;

    CommandDelete(IssueRepo issueRepo, String id) {
        this.issueRepo = issueRepo;
        this.hash = id;
    }

    @Override
    public void execute() {


        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        try {
            boolean success = false;

            if (issueRepo.getIssues().removeIf(issue -> issue.getHash().equals(hash))) {
                success = issueRepo.writeRepo("Deleted issue " + hash);
            }

            if (success) {
                System.out.println("Issue deleted");
            } else {
                System.out.println("Issue doesn't exist");
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }
}
