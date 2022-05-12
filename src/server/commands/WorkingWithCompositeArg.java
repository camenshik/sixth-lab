package server.commands;

import server.CommandHandler;
import server.collections.Entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public interface WorkingWithCompositeArg extends WorkingWithArgument {
    boolean inputValueFieldByClient(
            CommandHandler commandHandler,
            Field field,
            Entity entity,
            ArrayList<String> args,
            int indexProcessingArg) throws IOException;
    void inputValueFieldServer(CommandHandler commandHandler, Field field, Entity entity) throws IOException;
    Entity setEntityFields(CommandHandler commandHandler, Entity entity, String nameCommand) throws IOException;
}
