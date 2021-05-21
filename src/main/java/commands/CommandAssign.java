package commands;

import git.IssueRepo;
import issueData.User;

import java.io.IOException;

public final class CommandAssign extends AbstractCommandUpdate {

    private final IssueRepo issueRepo;
    private final String hash;
    private final String option;
    private final String name;
    private final String email;

    CommandAssign(IssueRepo issueRepo, String hash, String option, String name, String email) {
        this.issueRepo = issueRepo;
        this.hash = hash;
        this.option = option;
        this.name = name;
        this.email = email;
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
                            issue.addAssignee(new User(name, email));
                            issue.edited();
                            commitMessage = "Assignee " + name + " " + email + " added to issue " + hash;
                            break;
                        case "rm":
                            issue.removeAssignee(new User(name, email));
                            issue.edited();
                            commitMessage = "Assignee " + name + " " + email + " removed from issue " + hash;
                            break;
                        case "ls":
                            issue.getAssignees().forEach(user -> System.out.println("Name: " + user.name + "Email: " + user.email));
                            return;
                        default:
                            System.out.println("Invalid option - add, ls or rm only");
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

