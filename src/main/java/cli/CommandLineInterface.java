package cli;

import org.apache.commons.cli.*;

public class CommandLineInterface {

    private final Options options;

    public CommandLineInterface(String command) {
        this.options = new Options();
        addOptions(command);
    }

    private void addOptions(String command) {
        switch (command) {
            case "new":
                addNewIssueOptions();
                break;
            case "delete":
                addGetIdOption();
                break;
            case "ls":
                addLsOptions();
                break;
            case "show":
                addGetIdOption();
                break;
            case "pull":
                addRemoteOption();
                break;
            case "push":
                addRemoteOption();
                break;
            case "assign":
                addGetIdOption();
                addUserOptions();
                break;
            case "attach":
                addRemoveOptions();
                addGetIdOption();
                break;
            case "comment":
                addGetIdOption();
                addNewCommentOptions();
                break;
            case "tag":
                addRemoveOptions();
                addGetIdOption();
                break;
            case "edit":
                addEditOptions();
                addGetIdOption();
                break;
            case "watch":
                addGetIdOption();
                addUserOptions();
                break;
            case "nickname":
                addNickNameOptions();
                break;
            case "init":
            case "help":
                break;
            default:
                System.out.println("Unrecognized command: " + command);
                break;
        }
    }

    private void addNewIssueOptions() {
        options.addRequiredOption("t", "title", true, "Title for new issue");
        options.addRequiredOption("d", "desc", true, "Description for new issue");
    }

    private void addNickNameOptions() {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(Option
                .builder("a")
                .longOpt("add")
                .hasArgs()
                .numberOfArgs(2)
                .optionalArg(false)
                .build());
        optionGroup.addOption(Option
                .builder("u")
                .longOpt("update")
                .hasArgs()
                .numberOfArgs(2)
                .optionalArg(false)
                .build());
        optionGroup.addOption(Option
                .builder("rm")
                .longOpt("remove")
                .hasArg()
                .optionalArg(false)
                .build());
        optionGroup.addOption(Option
                .builder("ls")
                .longOpt("list")
                .build());
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);
    }

    private void addNewCommentOptions() {
        options.addRequiredOption("m", "message", true, "Message for new comment");
    }

    private void addUserOptions() {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(Option
                .builder("a")
                .longOpt("add")
                .build());
        optionGroup.addOption(Option
                .builder("rm")
                .longOpt("remove")
                .build());
        optionGroup.addOption(Option
                .builder("ls")
                .longOpt("list")
                .build());
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);
        options.addOption("n", "name", true, "Name of user being added");
        options.addOption("e", "email", true, "Email of user being added");
    }

    private void addEditOptions() {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(Option
                .builder("t")
                .hasArg()
                .argName("title")
                .longOpt("title")
                .build());
        optionGroup.addOption(Option
                .builder("d")
                .hasArg()
                .argName("desc")
                .longOpt("desc")
                .build());
        optionGroup.addOption(Option
                .builder("s")
                .hasArg()
                .argName("status")
                .longOpt("status")
                .build());
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);
    }

    private void addGetIdOption() {
        Option id = Option.builder("id")
                .hasArg()
                .argName("id")
                .required()
                .build();
        options.addOption(id);
    }

    private void addRemoteOption() {
        Option remote = Option.builder("r")
                .longOpt("remote")
                .hasArg()
                .argName("remote")
                .required()
                .build();
        options.addOption(remote);
    }

    private void addRemoveOptions() {
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.addOption(Option
                .builder("a")
                .hasArg()
                .longOpt("add")
                .build());
        optionGroup.addOption(Option
                .builder("rm")
                .hasArg()
                .longOpt("remove")
                .build());
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);
    }

    private void addLsOptions() {
        Option ls = Option.builder("q")
                .longOpt("query")
                .hasArgs()
                .optionalArg(true)
                .argName("query")
                .build();
        options.addOption(ls);
        Option verbose = Option.builder("v")
                .longOpt("verbose")
                .build();
        options.addOption(verbose);
        Option toCSV = Option.builder("csv")
                .longOpt("csv")
                .hasArg()
                .build();
        options.addOption(toCSV);
        Option commentCSV = Option.builder("csvc")
                .longOpt("csv-comments")
                .hasArg()
                .build();
        options.addOption(commentCSV);
    }

    public CommandLine getInput(String[] userInput) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, userInput);
    }

}
