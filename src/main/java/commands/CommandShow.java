package commands;

import git.IssueRepo;
import issueData.Comment;
import util.Util;

import java.util.Comparator;
import java.util.List;

public final class CommandShow implements Command {

    private final IssueRepo issueRepo;
    private final String id;

    CommandShow(IssueRepo issueRepo, String id) {
        this.issueRepo = issueRepo;
        this.id = id;
    }

    @Override
    public void execute() {

        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        issueRepo.getIssue(id).ifPresentOrElse(
                issue -> {

                    Util.printIssue(issue);

                    if (issue.getComments().isEmpty()) {
                        System.out.println("\nNo comments on this issue");
                    } else {
                        List<Comment> sortedComments = issue.getComments();
                        sortedComments.sort(Comparator.comparing(Comment::getCreationTime));
                        System.out.println("\nComments:\n");
                        sortedComments.forEach(comment -> System.out.println("Author: " + "User Name: " + comment.getAuthor().name + " Email: " + comment.getAuthor().email + "\n" + comment.getMessage() + "\n"));
                    }

                }, () -> System.out.println("Issue with given ID not found"));
    }
}
