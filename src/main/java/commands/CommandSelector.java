package commands;

import cli.CommandLineInterface;
import git.GitRepoNotFoundException;
import git.IssueRepo;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Arrays;

class CommandSelector {

    private final String[] enteredCommand;

    CommandSelector(String[] enteredCommand) {
        this.enteredCommand = enteredCommand;
    }

    private IssueRepo repoFactory() {
        try {
            return new IssueRepo(System.getProperty("user.dir") + "/.git");
        } catch (IOException e) {
            throw new GitRepoNotFoundException("Not a valid repo: " + System.getProperty("user.dir"));
        }
    }

    Command getCommand() {

        if (enteredCommand != null && enteredCommand.length > 0) {
            String userCommand = enteredCommand[0];
            String[] commandArgs = Arrays.copyOfRange(enteredCommand, 1, enteredCommand.length);

            if (userCommand.contains("-h") || userCommand.contains("--help")) {
                return new CommandHelp("help");
            }

            for (String arg : commandArgs) {
                if (arg.contains("-h") || arg.contains("--help")) {
                    return new CommandHelp(userCommand);
                }
            }

            CommandLineInterface commandLineInterface = new CommandLineInterface(userCommand);

            try {
                CommandLine userInput = commandLineInterface.getInput(commandArgs);

                switch (userCommand) {
                    case "init":
                        return init();
                    case "new":
                        return createIssue(userInput);
                    case "delete":
                        return delete(userInput);
                    case "ls":
                        return ls(userInput);
                    case "show":
                        return show(userInput);
                    case "pull":
                        return pull(userInput);
                    case "push":
                        return push(userInput);
                    case "assign":
                        return assign(userInput);
                    case "attach":
                        return attachment(userInput);
                    case "comment":
                        return comment(userInput);
                    case "tag":
                        return tag(userInput);
                    case "edit":
                        return edit(userInput);
                    case "watch":
                        return watcher(userInput);
                    case "help":
                        return new CommandHelp("help");
                    case "nickname":
                        return nickname(userInput);
                    default:
                        return nullCommand();
                }


            } catch (ParseException e) {
                System.err.println("Command \"" + userCommand + "\" failed - " + e.getMessage());
                return nullCommand();
            }

        } else {
            return new CommandHelp("help");
        }

    }

    private Command nickname(CommandLine commandLine) {
        if (commandLine.hasOption("add")) {
            return new CommandNickname(repoFactory(), "add", commandLine.getOptionValues("add"));
        } else if (commandLine.hasOption("update")) {
            return new CommandNickname(repoFactory(), "update", commandLine.getOptionValues("edit"));
        } else if (commandLine.hasOption("remove")) {
            return new CommandNickname(repoFactory(), "remove", commandLine.getOptionValues("remove"));
        } else {
            return new CommandNickname(repoFactory(), "list", new String[]{});
        }
    }

    private Command comment(CommandLine commandLine) {
        String hash = commandLine.getOptionValue("id");
        String comment = commandLine.getOptionValue("message");
        return new CommandComment(repoFactory(), hash, comment);
    }

    private Command pull(CommandLine commandLine) {
        return new CommandPull(repoFactory(), commandLine.getOptionValue("remote"));
    }

    private Command push(CommandLine commandLine) {
        return new CommandPush(repoFactory(), commandLine.getOptionValue("remote"));
    }

    private Command init() {
        return new CommandInit(repoFactory());
    }

    private Command nullCommand() {
        return new CommandNull();
    }

    private Command createIssue(CommandLine commandLine) {
        String title = commandLine.getOptionValue("title");
        String description = commandLine.getOptionValue("desc");
        return new CommandCreate(repoFactory(), title, description);
    }

    private Command ls(CommandLine commandLine) {
        if (commandLine.hasOption("csv-comments")) {
            return new CommandLs(repoFactory(), commandLine.hasOption("verbose"), commandLine.getOptionValue("csv"), commandLine.getOptionValues("query"), commandLine.getOptionValue("csv-comments"));
        } else if (commandLine.hasOption("csv")) {
            return new CommandLs(repoFactory(), commandLine.hasOption("verbose"), commandLine.getOptionValues("query"), commandLine.getOptionValue("csv"));
        } else {
            return new CommandLs(repoFactory(), commandLine.getOptionValues("query"), commandLine.hasOption("verbose"));
        }
    }

    private Command delete(CommandLine commandLine) {
        return new CommandDelete(repoFactory(), commandLine.getOptionValue("id"));
    }

    private Command show(CommandLine commandLine) {
        return new CommandShow(repoFactory(), commandLine.getOptionValue("id"));
    }

    private Command edit(CommandLine commandLine) {
        String hash = commandLine.getOptionValue("id");
        String optionValue;
        String data;
        if (commandLine.hasOption("title")) {
            optionValue = "title";
            data = commandLine.getOptionValue("title");
        } else if (commandLine.hasOption("desc")) {
            optionValue = "desc";
            data = commandLine.getOptionValue("desc");
        } else {
            optionValue = "status";
            data = commandLine.getOptionValue("status");
        }

        return new CommandEdit(repoFactory(), hash, optionValue, data);
    }

    @SuppressWarnings("Duplicates")
    private Command tag(CommandLine commandLine) {
        String hash = commandLine.getOptionValue("id");
        String optionValue;
        String tag;
        if (commandLine.hasOption("add")) {
            optionValue = "add";
            tag = commandLine.getOptionValue("add");
        } else {
            optionValue = "rm";
            tag = commandLine.getOptionValue("rm");
        }

        return new CommandTag(repoFactory(), hash, optionValue, tag);
    }

    @SuppressWarnings("Duplicates")
    private Command attachment(CommandLine commandLine) {
        String hash = commandLine.getOptionValue("id");
        String optionValue;
        String path;
        if (commandLine.hasOption("add")) {
            optionValue = "add";
            path = commandLine.getOptionValue("add");
        } else {
            optionValue = "rm";
            path = commandLine.getOptionValue("rm");
        }

        return new CommandAttachment(repoFactory(), hash, optionValue, path);
    }

    private Command assign(CommandLine commandLine) {
        String hash = commandLine.getOptionValue("id");
        String name = commandLine.getOptionValue("name");
        String email = commandLine.getOptionValue("email");

        String optionValue = getOption(commandLine);

        if (optionValue.equals("add") || optionValue.equals("rm")) {
            if (name == null || email == null) {
                System.err.println("Missing name or email");
                return nullCommand();
            }
        }

        return new CommandAssign(repoFactory(), hash, optionValue, name, email);
    }

    private Command watcher(CommandLine commandLine) {
        String hash = commandLine.getOptionValue("id");
        String name = commandLine.getOptionValue("name");
        String email = commandLine.getOptionValue("email");

        String optionValue = getOption(commandLine);

        if (optionValue.equals("add") || optionValue.equals("rm")) {
            if (name == null || email == null) {
                System.err.println("Missing name or email");
                return nullCommand();
            }
        }

        return new CommandWatcher(repoFactory(), hash, optionValue, name, email);
    }

    private String getOption(CommandLine commandLine) {
        String optionValue;
        if (commandLine.hasOption("add")) {
            optionValue = "add";
        } else if (commandLine.hasOption("rm")) {
            optionValue = "rm";
        } else {
            optionValue = "ls";
        }
        return optionValue;
    }

}


