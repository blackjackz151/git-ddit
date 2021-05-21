package commands;

import git.IssueRepo;
import issueData.Comment;

import java.io.IOException;

public final class CommandComment extends AbstractCommandUpdate {

    private final IssueRepo issueRepo;
    private final String id;
    private final String comment;

    CommandComment(IssueRepo issueRepo, String id, String comment) {
        this.issueRepo = issueRepo;
        this.id = id;
        this.comment = comment;
    }

    @Override
    public void execute() {

        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        issueRepo.getIssue(id).ifPresentOrElse(
                issue -> {
                    issue.addComment(new Comment(issueRepo.getUser(), comment));
                    issue.edited();
                    try {
                        String commitMessage = "Commented on issue " + issue.getHash();
                        updateRepo(issueRepo, issue, commitMessage);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                },
                () -> System.out.println("Issue with given ID not found"));
    }
}
