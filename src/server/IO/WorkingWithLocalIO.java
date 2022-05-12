package server.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public interface WorkingWithLocalIO extends WorkingWithIO, CompositeOutput {
    void setIn(BufferedReader in);
    void setOut(PrintStream out);
    BufferedReader getIn();
    PrintStream getOut();
    void close() throws IOException;
}
