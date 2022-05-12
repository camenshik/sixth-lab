package server.response;

import java.io.Serializable;

public enum TypeResponse implements Serializable {
    GREETING,
    ANSWER,
    QUESTION,
    COMMAND,
    DATA,
    COMPOSITE_DATA,
    END_COMPOSITE_DATA,
    BYE,
    NONE;
    private static final long serialVersionUID = -4507489610617395344L;
}
