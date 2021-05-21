package commands;


import git.IssueRepo;
import issueData.Issue;

import java.io.IOException;

public final class CommandCreate implements Command {

    private final IssueRepo issueRepo;
    private final String title;
    private final String description;

    CommandCreate(IssueRepo issueRepo, String title, String description) {
        this.issueRepo = issueRepo;
        this.title = title;
        this.description = description;
    }

    public void execute() {
        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        Issue issue = new Issue(title, description);
        issue.setCreator(issueRepo.getUser());

        try {
            boolean success;
            issueRepo.getIssues().add(issue);
            success = issueRepo.writeRepo("Created Issue " + issue.getTitle());
            if (success) {
                System.out.println("Issue created with hash: " + issue.getHash());
            } else {
                System.out.println("Failed to create issue");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
