package server.commands;

import server.exceptions.ArgumentException;

public interface WorkingWithArgument extends JustCommand {
    String parseArg(String command) throws ArgumentException;
    String getNameArg();
}
