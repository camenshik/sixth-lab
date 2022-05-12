package server.commands;

import server.CommandHandler;
import server.IO.WorkingWithLocalIO;
import server.annotations.CommandServerOnly;
import server.collections.Organization;
import server.exceptions.CommandException;
import server.exceptions.WrapperException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public enum JustCommands implements JustCommand {
    HELP(
            "help",
            "вывести справку по доступным командам"
    ) {
        @Override
        public boolean execution(
                CommandHandler commandHandler,
                String command
        ) throws IOException {
            ArrayList<String> response = new ArrayList<>();
            response.add("===================================");
            response.addAll(
                    Arrays.stream(JustCommands.values())
                            .map((value) -> String.format("%s - %s", value.getNameCommand(), value.getDescription()))
                            .collect(Collectors.toList())
            );
            response.addAll(
                    Arrays.stream(CommandsArg.values())
                            .map((value) -> String.format("%s - %s", value.getNameCommand(), value.getDescription()))
                            .collect(Collectors.toList()));
            response.addAll(
                    Arrays.stream(CommandsCompositeArg.values())
                            .map((value) -> String.format("%s - %s", value.getNameCommand(), value.getDescription()))
                            .collect(Collectors.toList())
            );
            response.add("==================================");
            commandHandler.getManagerIO().getIO().output(response.toArray(new String[0]));
            return true;
        }
    },
    INFO(
            "info",
            "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            ArrayList<String> response = new ArrayList<>();
            response.add("===============info================");
            response.add(commandHandler.getOrganizations().getInfo());
            response.add("===============info================");
            commandHandler.getManagerIO().getIO().output(response.toArray(new String[0]));
            return true;
        }
    },
    SHOW(
            "show",
            "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            for (Organization org : commandHandler.getOrganizations().getSet()) {
                commandHandler.getManagerIO().getIO().outputComposite(org.getInfo());
            }
            commandHandler.getManagerIO().getIO().endOutputComposite(false);
            return true;
        }
    },
    CLEAR(
            "clear",
            "очистить коллекцию"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            commandHandler.getOrganizations().clear();
            commandHandler.getManagerIO().getIO().output("Коллекция очищена");
            return true;
        }
    },
    @CommandServerOnly
    SAVE(
            "save",
            "сохранить коллекцию в файл"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            try {
                List<String[]> data = new ArrayList<>();
                for (Organization org : commandHandler.getOrganizations().getSet()) {
                    ArrayList<String> list = org.getListStringFields();
                    data.add(list.toArray(new String[list.size()]));
                }
                commandHandler.getCsvIO().writeAll(data);
            } catch (ReflectiveOperationException | IOException e) {
                System.out.println(e.getMessage());
                return false;
            }
            commandHandler.getManagerIO().getIO().output("Коллекция сохранена");
            return true;
        }
    },
    @CommandServerOnly
    EXIT(
            "exit",
            "завершить программу (без сохранения в файл)"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            commandHandler.getManagerIO().getIO().output("Завершение программы...");
            commandHandler.setExit(true);
            return true;
        }
    },
    HISTORY(
            "history",
            "вывести последние 15 команд (без их аргументов)"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            int maxLength = 15;
            LinkedList<String> history;
            if (commandHandler.getManagerIO().getIO() instanceof WorkingWithLocalIO) {
                history = commandHandler.getHistory();
            } else {
                history = commandHandler.getConnectionManager().getProcessingClient().getHistory();
            }
            if (history.size() < maxLength)
                maxLength = history.size();
            commandHandler.getManagerIO().getIO().outputComposite("=============history===============");
            for (int i = 0; i < maxLength; i++) {
                commandHandler.getManagerIO().getIO().outputComposite(String.format(
                        "%d. %s", i + 1, history.get(i)
                ));
            }
            commandHandler.getManagerIO().getIO().outputComposite("=============history===============");
            commandHandler.getManagerIO().getIO().endOutputComposite(false);
            return true;
        }
    },
    GROUP_COUNTING_BY_ID(
            "group_counting_by_id",
            "сгруппировать элементы коллекции по значению поля id, вывести количество элементов в каждой группе"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws IOException {
            int even = 0;
            int odd = 0;
            for (Organization org : commandHandler.getOrganizations().getSet()) {
                    if (org.getId() % 2 == 0)
                        even++;
                    else
                        odd++;
            }
            ArrayList<String> response = new ArrayList<>();
            response.add(String.format(
                    "%s - количество элементов с четным значением id", even
            ));
            response.add(String.format(
                    "%s - количество элементов с нечетным значением id", odd
            ));
            commandHandler.getManagerIO().getIO().output(response.toArray(new String[0]));
            return true;
        }
    },
    PRINT_ASCENDING(
            "print_ascending",
            "вывести элементы коллекции в порядке возрастания"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws CommandException, IOException {
            try {
                for (Organization org : commandHandler.getOrganizations().sort()) {
                    commandHandler.getManagerIO().getIO().outputComposite(org.getInfo());
                }
            } catch(WrapperException | IOException e) {
                throw new CommandException(e.getMessage());
            }
            commandHandler.getManagerIO().getIO().outputComposite(
                    "Выведены элементы коллекции в порядке возрастания"
            );
            commandHandler.getManagerIO().getIO().endOutputComposite(false);
            return true;
        }
    },
    PRINT_DESCENDING(
            "print_descending",
            "вывести элементы коллекции в порядке убывания"
    ) {
        @Override
        public boolean execution(CommandHandler commandHandler, String command) throws CommandException, IOException {
            try {
                for (Organization org : commandHandler.getOrganizations().reversedSort()) {
                    commandHandler.getManagerIO().getIO().outputComposite(org.getInfo());
                }
            } catch(WrapperException e) {
                throw new CommandException(e.getMessage());
            }
            commandHandler.getManagerIO().getIO().outputComposite(
                    "Выведены элементы коллекции в порядке убывания"
            );
            commandHandler.getManagerIO().getIO().endOutputComposite(false);
            return true;
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
