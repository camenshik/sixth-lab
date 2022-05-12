package client.commands;


import client.CommandHandler;
import client.exceptions.CommandException;

public interface JustCommand {
    void execution(CommandHandler commandHandler, String command) throws CommandException;
    String getNameCommand();
    String getDescription();
}
