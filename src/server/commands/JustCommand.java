package server.commands;

import server.CommandHandler;
import server.exceptions.CommandException;

import java.io.IOException;

public interface JustCommand {
    boolean execution(CommandHandler commandHandler, String command) throws CommandException, IOException;
    String getNameCommand();
    String getDescription();
}
