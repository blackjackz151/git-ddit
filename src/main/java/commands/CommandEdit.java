package commands;

import git.IssueRepo;

import java.io.IOException;

public final class CommandEdit extends AbstractCommandUpdate {

    private final IssueRepo issueRepo;
    private final String hash;
    private final String option;
    private final String data;

    CommandEdit(IssueRepo issueRepo, String hash, String option, String data) {
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
                        case "title":
                            issue.setTitle(data);
                            issue.edited();
                            commitMessage = "Title of " + hash + " updated";
                            break;
                        case "desc":
                            issue.setDescription(data);
                            issue.edited();
                            commitMessage = "Description of " + hash + " updated";
                            break;
                        case "status":
                            if (data.equals("open")) {
                                issue.setStatus(true);
                                commitMessage = "Opened issue " + hash;
                            } else if (data.equals("close")) {
                                issue.setStatus(false);
                                commitMessage = "Closed issue " + hash;
                            } else {
                                System.out.println("Invalid option given: " + data + " - should be open or close");
                                return;
                            }
                            issue.edited();
                            break;
                        default:
                            System.out.println("Invalid option given must be: title, desc or status");
                            return;

                    }

                    try {
                        updateRepo(issueRepo, issue, commitMessage);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }

                },
                () -> System.out.println("Issue with given ID not found"));

    }
}
