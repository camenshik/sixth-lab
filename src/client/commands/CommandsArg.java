package client.commands;

import client.CommandHandler;
import client.IO.TypeIO;
import client.exceptions.ArgumentException;
import client.exceptions.CommandException;
import client.exceptions.ManagerIOException;
import client.exceptions.RecursionException;
import client.IO.SystemIO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum CommandsArg implements WorkingWithArgument {
    EXECUTE_SCRIPT(
            "execute_script",
            "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.",
            "file_name"
    ) {
        @Override
        public void execution(CommandHandler commandHandler, String command) throws CommandException {
            if (commandHandler.getManagerIO().getIO() instanceof SystemIO)
                commandHandler.setDepthRecursion(0);
            if (commandHandler.getDepthRecursion() >= commandHandler.getLimitRecursion())
                throw new RecursionException();
            String arg = this.parseArg(command);
            commandHandler.setDepthRecursion(commandHandler.getDepthRecursion() + 1);
            try {
                commandHandler.getManagerIO().changeIO(
                        TypeIO.FILE,
                        arg,
                        String.format(
                                "output-client-%s.txt",
                                new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date())
                        )
                );
                boolean isNoteEndInput;
                do {
                    isNoteEndInput = commandHandler.getClient().generationRequest();
                } while(isNoteEndInput);
                commandHandler.getManagerIO().closeIO();
                commandHandler.getManagerIO().changeIO(TypeIO.SYSTEM);
            } catch (ManagerIOException | IOException e) {
                throw new CommandException(String.format("неполучилось выполнить скрипт - %s", e.getMessage()));
            }
            commandHandler.getManagerIO().getIO().getOut().printf("Скрипт %s выполнен, вывод сохранен в файл\n", arg);
        }
    };

    private final String nameCommand;
    private final String description;
    private final String nameArg;
    CommandsArg(String nameCommand, String description, String nameArg) {
        this.nameCommand = nameCommand;
        this.description = description;
        this.nameArg = nameArg;
    }

    @Override
    public String getNameCommand() {
        return this.nameCommand;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getNameArg() {
        return this.nameArg;
    }

    @Override
    public String parseArg(String command) throws ArgumentException {
        String[] parts = command.split(" ");
        if (parts.length <= 1) {
            throw new ArgumentException("не переданы аргументы");
        } else if (parts.length > 2) {
            throw new ArgumentException("передано более 1 аргумента");
        }
        return parts[1];
    }
}
