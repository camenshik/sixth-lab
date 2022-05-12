package server.response;

import java.lang.reflect.Field;

public class ResponseReader {
    public static TypeResponse getTypeResponse(Object response) throws ReflectiveOperationException {
        Field fieldTypeResponse = response.getClass().getDeclaredField("typeResponse");
        fieldTypeResponse.setAccessible(true);
        return TypeResponse.valueOf(fieldTypeResponse.get(response).toString());
    }

    public static String[] getBodyResponse(Object response) throws ReflectiveOperationException {
        Field bodyResponse = response.getClass().getDeclaredField("body");
        bodyResponse.setAccessible(true);
        return (String[]) bodyResponse.get(response);
    }

    public static Response getResponse(Object response) throws ReflectiveOperationException {
        return ResponseFactory.createResponse(
                ResponseReader.getTypeResponse(response),
                ResponseReader.getBodyResponse(response)
        );
    }
}
