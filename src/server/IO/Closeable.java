package server.IO;

import java.io.IOException;

public interface Closeable {
    void close() throws IOException;
}
