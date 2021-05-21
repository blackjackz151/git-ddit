package commands;

import util.Util;

public final class CommandHelp implements Command {

    private final String command;

    CommandHelp(String command) {
        this.command = command;
    }

    @Override
    public void execute() {
        switch (command) {

            case "help":
                Util.readFile("help/MainMenuHelp");
                break;
            case "init":
                Util.readFile("help/InitHelp");
                break;
            case "new":
                Util.readFile("help/NewHelp");
                break;
            case "delete":
                Util.readFile("help/DeleteHelp");
                break;
            case "ls":
                Util.readFile("help/LsHelp");
                break;
            case "show":
                Util.readFile("help/ShowHelp");
                break;
            case "pull":
                Util.readFile("help/PullHelp");
                break;
            case "push":
                Util.readFile("help/PushHelp");
                break;
            case "assign":
                Util.readFile("help/AssignHelp");
                break;
            case "attach":
                Util.readFile("help/AttachHelp");
                break;
            case "comment":
                Util.readFile("help/CommentHelp");
                break;
            case "tag":
                Util.readFile("help/TagHelp");
                break;
            case "edit":
                Util.readFile("help/EditHelp");
                break;
            case "watch":
                Util.readFile("help/WatchHelp");
                break;
            case "nickname":
                Util.readFile("help/NicknameHelp");
                break;
            default:
                break;
        }
    }
}
