package application;

import commands.CommandExecutor;

public class DDITApplication {

    public static void main(String[] args) {
        CommandExecutor executor = new CommandExecutor(args);
        executor.start();
    }
}

