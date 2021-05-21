package commands;

import git.IssueRepo;

import java.io.IOException;

public final class CommandTag extends AbstractCommandUpdate {

    private final IssueRepo issueRepo;
    private final String hash;
    private final String option;
    private final String data;

    CommandTag(IssueRepo issueRepo, String hash, String option, String data) {
        this.issueRepo = issueRepo;
        this.hash = hash;
        this.option = option;
        this.data = data;
    }

    @Override
    public void execute() {

        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        issueRepo.getIssue(hash).ifPresentOrElse(
                issue -> {
                    String commitMessage;
                    switch (option) {
                        case "add":
                            issue.addTags(data.toLowerCase());
                            issue.edited();
                            commitMessage = "Tag(s): " + data + " added to issue " + hash;
                            break;
                        case "rm":
                            issue.removeTag(data.toLowerCase());
                            issue.edited();
                            commitMessage = "Tag(s): " + data + " removed from issue " + hash;
                            break;
                        default:
                            System.out.println("Invalid option - add or remove only");
                            return;
                    }

                    try {

                        updateRepo(issueRepo, issue, commitMessage);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }

                }, () -> System.out.println("Issue with given ID not found"));

    }

}
