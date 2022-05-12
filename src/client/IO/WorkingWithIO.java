package client.IO;

import java.io.*;

public interface WorkingWithIO {
    void setIn(BufferedReader in);
    void setOut(PrintStream out);
    BufferedReader getIn();
    PrintStream getOut();
    void close() throws IOException;
}
