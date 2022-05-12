package client.commands;

import client.CommandHandler;

public enum JustCommands implements JustCommand {
    EXIT(
            "exit",
            "завершить программу (без сохранения в файл)"
    ) {
        @Override
        public void execution(CommandHandler commandHandler, String command) {
            commandHandler.getManagerIO().getIO().getOut().println("Завершение программы...");
            commandHandler.setExit(true);
        }
    };

    private final String nameCommand;
    private final String description;
    JustCommands(String nameCommand, String description) {
        this.nameCommand = nameCommand;
        this.description = description;
    }

    @Override
    public String getNameCommand() {
        return this.nameCommand;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
