package client;

import client.IO.FileIO;
import client.IO.ManagerIO;
import client.IO.SystemIO;
import client.IO.TypeIO;
import client.commands.CommandsArg;
import client.commands.JustCommand;
import client.commands.JustCommands;
import client.connect.AbstractClient;
import client.exceptions.CommandException;
import client.exceptions.EotException;
import client.exceptions.ManagerIOException;
import client.exceptions.RecursionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class CommandHandler {
    private final LinkedList<String> history;
    private final ManagerIO managerIO;
    private final int limitRecursion;
    private int depthRecursion;
    private boolean isExit;
    private ArrayList<String> serverCommands;
    private final AbstractClient client;

    public CommandHandler(AbstractClient client) throws IOException {
        this.history = new LinkedList<>();
        this.managerIO = new ManagerIO();
        this.limitRecursion = 10;
        this.depthRecursion = 0;
        this.isExit = false;
        this.client = client;
    }

    public void setDepthRecursion(int depthRecursion) {
        this.depthRecursion = depthRecursion;
    }

    public void setExit(boolean isExit) {
        this.isExit = isExit;
    }

    public void setServerCommands(ArrayList<String> serverCommands) {
        this.serverCommands = serverCommands;
    }

    public LinkedList<String> getHistory() {
        return this.history;
    }

    public ManagerIO getManagerIO() {
        return this.managerIO;
    }

    public int getLimitRecursion() {
        return this.limitRecursion;
    }

    public int getDepthRecursion() {
        return this.depthRecursion;
    }

    public boolean getIsExit() {
        return this.isExit;
    }

    public AbstractClient getClient() {
        return this.client;
    }

    public ArrayList<String> getServerCommands() {
        return this.serverCommands;
    }

    public String filterCommand(String rawCommand) throws CommandException {
        String command = rawCommand.trim();
        if (command.split(" ").length > 2)
            throw new CommandException("слишком большое количество аргументов");
        return command;
    }

    public boolean isLocalCommand(String nameCommand) {
        for (JustCommand command: JustCommands.values()) {
            if (command.getNameCommand().equals(nameCommand))
                return true;
        }
        for (JustCommand command: CommandsArg.values()) {
            if (command.getNameCommand().equals(nameCommand))
                return true;
        }
        return false;
    }

    public void executeLocalCommand(String command) throws CommandException {
        String[] parts = command.split(" ");
        if (parts.length < 1) {
            return;
        }

        String nameCommand = parts[0].toUpperCase();
        JustCommand commandClass;
        if (parts.length == 1) {
            commandClass = JustCommands.valueOf(nameCommand);
        } else if (parts.length == 2) {
            commandClass = CommandsArg.valueOf(nameCommand);
        } else {
            throw new CommandException("слишком большое количество аргументов");
        }
        commandClass.execution(this, command);
    }

    public String interactiveMod() {
        try {
            String command;
            if (this.managerIO.getIO() instanceof SystemIO) {
                this.depthRecursion = 0;
            } else if (this.managerIO.getIO() instanceof FileIO) {
                if (this.depthRecursion > this.limitRecursion)
                    throw new RecursionException();
            }
            command = this.managerIO.getIO().getIn().readLine();
            if (command == null)
                throw new EotException();
            command = this.filterCommand(command);
            if (!this.isLocalCommand(command.toUpperCase()) && this.serverCommands.contains(command.split(" ")[0].toUpperCase())) {
                this.history.addFirst(command.split(" ")[0]);
                return command;
            }
            try {
                this.executeLocalCommand(command);
                this.history.addFirst(command);
            } catch (CommandException e) {
                this.getManagerIO().getIO().getOut().printf("Ошибка ввода, %s\n", e.getMessage());
            } catch (IllegalArgumentException e) {
                this.getManagerIO().getIO().getOut().println("Команда не найдена, повторите ввод");
            } catch (RecursionException e) {
                if (!(this.getManagerIO().getIO() instanceof SystemIO)) {
                    this.getManagerIO().closeIO();
                    this.getManagerIO().changeIO(TypeIO.SYSTEM);
                }
                throw e;
            }
        } catch (CommandException e) {
            this.getManagerIO().getIO().getOut().printf("Ошибка ввода, %s\n", e.getMessage());
        } catch (IOException | ManagerIOException e) {
            System.out.println(e.getMessage());
        } catch (EotException e) {
            if (this.getManagerIO().getIO() instanceof SystemIO) {
                this.isExit = true;
                this.getManagerIO().getIO().getOut().println("Ввод прерван");
            }
        }
        return "";
    }

    public boolean askAnswer(String answer) throws IOException {
        this.getManagerIO().getSystemIO().getOut().println(answer);
        String reply;
        while (true) {
            reply = this.getManagerIO().getSystemIO().getIn().readLine().trim().toUpperCase();
            if (reply.equals("YES")) {
                return true;
            } else if (reply.equals("NO")) {
                return false;
            }
            this.getManagerIO().getSystemIO().getOut().println("Ответ нераспознан, повторите ввод");
        }
    }

    public String inputData() throws IOException {
        return this.getManagerIO().getIO().getIn().readLine().trim();
    }
}
