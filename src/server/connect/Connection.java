package server.connect;

import java.util.ArrayList;
import java.util.LinkedList;

public class Connection {
    private final String address;
    private final ArrayList<Command> commands;
    private final LinkedList<String> history;

    public Connection(String address) {
        this.address = address;
        this.commands = new ArrayList<>();
        this.history = new LinkedList<>();
    }

    public Connection(String address, ArrayList<Command> commands) {
        this.address = address;
        this.commands = commands;
        this.history = new LinkedList<>();
    }

    public LinkedList<String> getHistory() {
        return this.history;
    }

    public boolean isEmpty() {
        return this.commands.isEmpty();
    }

    public String getAddress() {
        return this.address;
    }

    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    public Command getExecuteCommand() {
        if (!this.commands.isEmpty())
            return this.commands.get(0);
        return null;
    }

    public void removeExecuteCommand() {
        if (!this.commands.isEmpty())
            this.commands.remove(0);
    }

    public void addCommand(String nameCommand) {
        this.commands.add(new Command(nameCommand));
    }

    public void addCommand(String nameCommand, ArrayList<String> args) {
        this.commands.add(new Command(nameCommand, args));
    }

    public void clearCommands() {
        this.commands.clear();
    }
}
