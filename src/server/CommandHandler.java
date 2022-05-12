package server;

import com.opencsv.exceptions.CsvException;
import server.IO.*;
import server.collections.Organization;
import server.commands.CommandsArg;
import server.commands.CommandsCompositeArg;
import server.commands.JustCommand;
import server.commands.JustCommands;
import server.connect.ConnectionManager;
import server.exceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class CommandHandler {
    private boolean isExit;
    private final LinkedList<String> history;
    private final WrapperHashSet<Organization> organizations;
    private final int limitRecursion;
    private int depthRecursion;
    private final ManagerIO managerIO;
    private final CsvIO csvIO;
    private ConnectionManager connectionManager;

    public CommandHandler(String nameCsvFile) throws IOException {
        this.isExit = false;
        this.history = new LinkedList<>();
        this.organizations = new WrapperHashSet<>();
        this.managerIO = new ManagerIO();
        this.limitRecursion = 10;
        this.depthRecursion = 0;
        this.csvIO = new CsvIO(nameCsvFile);
    }

    public void setExit(boolean isExit) {
        this.isExit = isExit;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setDepthRecursion(int depthRecursion) {
        this.depthRecursion = depthRecursion;
    }

    public boolean getIsExit() {
        return this.isExit;
    }

    public LinkedList<String> getHistory() {
        return this.history;
    }

    public WrapperHashSet<Organization> getOrganizations() {
        return this.organizations;
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

    public CsvIO getCsvIO() {
        return this.csvIO;
    }

    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    public void executeCommand(String command) throws CommandException {
        String[] parts = command.split(" ");
        if (parts.length < 1) {
            return;
        }

        String nameCommand = parts[0].toUpperCase();
        JustCommand commandClass;
        try {
            if (parts.length == 1) {
                commandClass = JustCommands.valueOf(nameCommand);
            } else {
                commandClass = CommandsArg.valueOf(nameCommand);
            }
        } catch(IllegalArgumentException e) {
            if (this.connectionManager.getProcessingClient() != null)
                this.connectionManager.getProcessingClient().addCommand(command);
            commandClass = CommandsCompositeArg.valueOf(nameCommand);
        }
        boolean isExecuteFull = false;
        try {
            isExecuteFull = commandClass.execution(this, command);
        } catch (CollectionsException e) {
            try {
                this.getManagerIO().getIO().output(e.getMessage());
            } catch (IOException eIO) {
                System.out.println(e.getMessage());
                eIO.printStackTrace();
            }
        } catch (IOException e) {
            System.out.printf("Ошибка ввода: %s\n", e.getMessage());
        }
        if (!isExecuteFull)
            return;
        if (this.getManagerIO().getIO() instanceof WorkingWithLocalIO)
            this.history.addFirst(commandClass.getNameCommand());
        if (this.getManagerIO().getIO() instanceof ServerIO)
            this.connectionManager.getProcessingClient().getHistory().addFirst(commandClass.getNameCommand());
    }

    public void interactiveMod(String command) {
        if (command == null)
            throw new EotException();
        try {
            command = command.trim();
            try {
                this.executeCommand(command);
            } catch (CommandException e) {
                this.getManagerIO().getIO().output(String.format("Ошибка ввода, %s", e.getMessage()));
            } catch (IllegalArgumentException e) {
                this.getManagerIO().getIO().output("Команда не найдена, повторите ввод");
            } catch (CollectionsException e) {
                this.getManagerIO().getIO().output(e.getMessage());
            } catch (RecursionException e) {
                if (this.getManagerIO().getIO() instanceof FileIO) {
                    this.getManagerIO().closeIO();
                    this.getManagerIO().changeIO(TypeIO.SYSTEM);
                }
                throw e;
            }
        } catch (IOException | ManagerIOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadDataOrganisations()
            throws IOException,
            CsvException,
            ReflectiveOperationException,
            AnnotationsException,
            WrapperException {
        for (String[] row : this.csvIO.readAll()) {
            Organization org = new Organization();
            org.setFieldsByListString(new ArrayList<>(Arrays.asList(row)));
            this.getOrganizations().addElement(org);
        }
    }

    public boolean askAnswer(String answer) throws IOException {
        this.getManagerIO().getIO().output(answer);
        String reply;
        while (true) {
            reply = this.getManagerIO().getIO().input().trim().toUpperCase();
            if (reply.equals("YES")) {
                return true;
            } else if (reply.equals("NO")) {
                return false;
            }
            this.getManagerIO().getIO().output("Ответ нераспознан, повторите ввод");
        }
    }
}