package commands;

import git.IssueRepo;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;

public final class CommandAttachment extends AbstractCommandUpdate {

    private final static long MAX_FILE_SIZE = 5000000L; // 5MB
    private final IssueRepo issueRepo;
    private final String hash;
    private final String option;
    private final String path;

    CommandAttachment(IssueRepo issueRepo, String hash, String option, String path) {
        this.issueRepo = issueRepo;
        this.hash = hash;
        this.option = option;
        this.path = path;
    }

    @Override
    public void execute() {

        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        issueRepo.getIssue(hash).ifPresentOrElse(issue -> {
            try {
                String commitMessage;
                switch (option) {
                    case "add":
                        File file = new File(path);
                        if (!file.isFile() || !file.exists()) {
                            System.out.println("File not found");
                            return;
                        }
                        if (file.length() > MAX_FILE_SIZE) {
                            System.out.println("File too large, make sure under 5MB");
                            return;
                        }
                        issue.addAttachment(file.getName(), IOUtils.toByteArray(file.toURI()));
                        issue.edited();
                        commitMessage = "Attachment " + file.getName() + " added to issue " + hash;
                        break;
                    case "rm":
                        byte[] removedFile = issue.getAttachments().remove(path);
                        if (removedFile != null) {
                            issue.edited();
                            commitMessage = "Attachment " + path + " removed from issue " + hash;
                            break;
                        } else {
                            System.out.println("File not found");
                            return;
                        }
                    default:
                        System.out.println("Invalid option - add or rm only");
                        return;

                }

                updateRepo(issueRepo, issue, commitMessage);

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

        }, () -> System.out.println("Issue with given ID not found"));
    }
}
