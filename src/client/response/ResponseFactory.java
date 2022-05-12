package client.response;

import client.exceptions.ResponseException;

public class ResponseFactory {
    public static Response createResponse(TypeResponse typeResponse) {
        Response response;
        switch (typeResponse) {
            case GREETING:
                response = new Response(TypeResponse.GREETING, ResponseFactory.getGreeting());
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
                response = new Response(TypeResponse.GREETING, data);
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

    public static String[] getGreeting() {
        return new String[]{""};
    }
}
