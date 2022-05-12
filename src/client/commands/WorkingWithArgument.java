package client.commands;

import client.CommandHandler;
import client.exceptions.CommandException;
import client.exceptions.ArgumentException;

public interface WorkingWithArgument extends JustCommand {
    void execution(CommandHandler commandHandler, String command) throws CommandException;
    String parseArg(String command) throws ArgumentException;
    String getNameArg();
}
