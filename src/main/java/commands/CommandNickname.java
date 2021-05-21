package commands;

import git.IssueRepo;
import issueData.IssueNicknames;

import java.io.IOException;

public final class CommandNickname implements Command {

    private final IssueRepo issueRepo;
    private final String option;
    private final String[] args;

    CommandNickname(IssueRepo issueRepo, String option, String[] args) {
        this.issueRepo = issueRepo;
        this.option = option;
        this.args = args;
    }

    @Override
    public void execute() {
        if (!validatedArgs()) {
            System.out.println("invalid args");
            return;
        }


        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }

        if (option.equals("list")) {
            issueRepo.getNicknames().getIssueNicknames().forEach((k, v) -> System.out.println("Hash: " + k + " has nickname \"" + v + "\""));
            return;
        }

        String id = args[0];

        issueRepo.getIssue(id).ifPresentOrElse(issue -> {
                    IssueNicknames nicknames = issueRepo.getNicknames();
                    boolean success;
                    String commitMessage;

                    if (args.length > 1 && args[1] != null) {
                        if (issueRepo.getIssue(args[1]).isPresent()) {
                            System.out.println("Nickname already linked to an issue");
                            return;
                        }
                    }

                    switch (option) {
                        case "add":
                            success = nicknames.addNickname(issue.getHash(), args[1]);
                            commitMessage = "Nickname added to " + issue.getHash();
                            break;
                        case "update":
                            success = nicknames.updateNickname(issue.getHash(), args[1]);
                            commitMessage = "Nickname updated for " + issue.getHash();
                            break;
                        case "remove":
                            success = nicknames.removeNickname(issue.getHash());
                            commitMessage = "Nickname remove from " + issue.getHash();
                            break;
                        default:
                            System.out.println("Invalid option - add, update or remove only");
                            return;
                    }

                    if (success) {
                        try {
                            boolean successCommit = issueRepo.writeRepo(commitMessage);
                            if (successCommit) {
                                System.out.println(commitMessage);
                            } else {
                                System.out.println("Failed to " + option + " nickname");
                            }

                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Failed to " + option + " nickname");
                    }

                },
                () -> System.out.println("Issue with given ID not found"));


    }

    private boolean validatedArgs() {
        switch (option) {
            case "add":
            case "update":
                return args.length == 2;
            case "remove":
                return args.length == 1;
            case "list":
                return args.length == 0;
            default:
                return true;
        }
    }


}
