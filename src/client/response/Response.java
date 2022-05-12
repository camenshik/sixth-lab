package client.response;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = -4507489610617393544L;
    protected final TypeResponse typeResponse;
    protected String[] body;
    public Response(TypeResponse typeResponse, String[] body) {
        this.typeResponse = typeResponse;
        this.body = body;
    }

    public TypeResponse getTypeResponse() {
        return this.typeResponse;
    }

    public String[] getBody() {
        return this.body;
    }
}
