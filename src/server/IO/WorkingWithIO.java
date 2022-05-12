package server.IO;

import java.io.IOException;

public interface WorkingWithIO extends CompositeOutput {
    void output(String data) throws IOException;
    void output(String[] data) throws IOException;
    String input() throws IOException;
}
