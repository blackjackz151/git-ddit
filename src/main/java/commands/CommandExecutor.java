package commands;

public class CommandExecutor {
    private final Command command;

    public CommandExecutor(String[] args) {
        CommandSelector commandSelector = new CommandSelector(args);
        this.command = commandSelector.getCommand();
    }

    public void start() {
        command.execute();
    }


}
