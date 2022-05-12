package server.response;

import server.annotations.CommandServerOnly;
import server.commands.CommandsArg;
import server.commands.CommandsCompositeArg;
import server.commands.JustCommand;
import server.commands.JustCommands;
import server.exceptions.ResponseException;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ResponseFactory {
    public static Response createResponse(TypeResponse typeResponse) {
        Response response;
        switch (typeResponse) {
            case GREETING:
                response = new Response(TypeResponse.GREETING, getGreeting());
                break;
            case ANSWER:
            case QUESTION:
            case DATA:
            case COMMAND:
            case COMPOSITE_DATA:
            case END_COMPOSITE_DATA:
            case BYE:
            case NONE:
                response = new Response(typeResponse, new String[]{});
                break;
            default:
                throw new ResponseException("Unexpected value: " + typeResponse);
        }
        return response;
    }

    public static Response createResponse(TypeResponse typeResponse, String[] data) {
        Response response;
        switch (typeResponse) {
            case GREETING:
                response = new Response(TypeResponse.GREETING, getGreeting());
                break;
            case ANSWER:
            case QUESTION:
            case DATA:
            case COMMAND:
            case COMPOSITE_DATA:
            case END_COMPOSITE_DATA:
            case BYE:
            case NONE:
                response = new Response(typeResponse, data);
                break;
            default:
                throw new ResponseException("Unexpected value: " + typeResponse);
        }
        return response;
    }

    private static String[] getGreeting() {
        ArrayList<String> arr = new ArrayList<>();
        arr.addAll(getCommands(JustCommands.class));
        arr.addAll(getCommands(CommandsArg.class));
        arr.addAll(getCommands(CommandsCompositeArg.class));
        return arr.toArray(new String[0]);
    }

    private static ArrayList<String> getCommands(Class<? extends JustCommand> commands) {
        ArrayList<String> result = new ArrayList<>();
        for (Field command: commands.getDeclaredFields()) {
            command.setAccessible(true);
            if ((command.getAnnotation(CommandServerOnly.class) != null) || (!command.isEnumConstant()))
                continue;
            result.add(command.getName());
        }
        return result;
    }
}
