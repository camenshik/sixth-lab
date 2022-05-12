package server.connect;

import java.util.ArrayList;

public class Command {
    private final String name;
    private final ArrayList<String> args;
    public Command(String name) {
        this.name = name;
        this.args = new ArrayList<>();
    }

    public Command(String name, ArrayList<String> args) {
        this.name = name;
        this.args = args;
    }

    public boolean isEmpty() {
        return this.args.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getArgs() {
        return this.args;
    }

    public void addArg(String arg) {
        this.args.add(arg);
    }

    public void addArgs(ArrayList<String> args) {
        this.args.addAll(args);
    }

    public void removeArg() {
        this.args.remove(0);
    }

    public void removeArgs() {
        this.args.clear();
    }
}
