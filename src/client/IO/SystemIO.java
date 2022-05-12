package client.IO;

import java.io.*;

public class SystemIO implements WorkingWithIO {
    BufferedReader in;
    PrintStream out;
    public SystemIO() {
        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.out = new PrintStream(System.out);
    }

    @Override
    public void setIn(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void setOut(PrintStream out) {
        this.out = out;
    }

    @Override
    public BufferedReader getIn() {
        return in;
    }

    @Override
    public PrintStream getOut() {
        return out;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
    }
}