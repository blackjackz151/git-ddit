package commands;

import git.IssueRepo;
import issueData.Issue;

import java.io.IOException;
import java.util.List;

abstract class AbstractCommandUpdate implements Command {

    void updateRepo(IssueRepo issueRepo, Issue issue, String commitMessage) throws IOException {
        List<Issue> issues = issueRepo.getIssues();

        int i = issues.indexOf(issue);
        issues.remove(i);
        issues.add(i, issue);

        boolean success = issueRepo.writeRepo(commitMessage);

        if (success) {
            System.out.println(commitMessage);
        } else {
            System.out.println("Issue not updated.");
        }
    }
}
