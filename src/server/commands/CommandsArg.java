package server.commands;

import server.IO.SystemIO;
import server.IO.TypeIO;
import server.CommandHandler;
import server.annotations.CommandServerOnly;
import server.exceptions.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum CommandsArg implements WorkingWithArgument {
    REMOVE_BY_ID(
            "remove_by_id",
            "удалить элемент из коллекции по его id",
            "id"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws CommandException, IOException {
            long id = Long.parseLong(this.parseArg(command));
            if (!commandHandler.getOrganizations().contains(id))
                throw new CollectionsException("Элемент не найден");
            try {
                commandHandler.getOrganizations().removeElement(id);
            } catch(WrapperException e) {
                throw new CommandException(e.getMessage());
            }
            commandHandler.getManagerIO().getIO().output("Элемент удален");
            return true;
        }
    },
    @CommandServerOnly
    EXECUTE_SCRIPT(
            "execute_script",
            "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.",
            "file_name"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws CommandException, IOException {
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
                                "output-server-%s.txt",
                                new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date())
                        )
                );
                String commandFromFile = commandHandler.getManagerIO().getIO().input();
                while (commandFromFile != null) {
                    commandHandler.interactiveMod(commandFromFile);
                    commandFromFile = commandHandler.getManagerIO().getIO().input();
                }
                commandHandler.getManagerIO().changeIO(TypeIO.SYSTEM);
            } catch (ManagerIOException | IOException e) {
                throw new CommandException(String.format("неполучилось выполнить скрипт - %s", e.getMessage()));
            }
            commandHandler.getManagerIO().getIO().output(
                    String.format("Скрипт %s выполнен, вывод сохранен в файл", arg)
            );
            return true;
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
