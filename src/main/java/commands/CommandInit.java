package commands;

import git.IssueRepo;

import java.io.IOException;

public final class CommandInit implements Command {
    private final IssueRepo issueRepo;

    CommandInit(IssueRepo issueRepo) {
        this.issueRepo = issueRepo;
    }

    public void execute() {

        try {
            boolean success = false;

            if (issueRepo.issueBranchExists()) {
                System.out.println("Issue branch already exists");
            } else {
                success = issueRepo.init();
            }

            if (success) {
                System.out.println("Issue repository created");
            } else {
                System.out.println("Failed to create issue repository");
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}