package client.IO;

import java.io.*;
import java.nio.file.Paths;

public class FileIO implements WorkingWithIO{
    private BufferedReader in;
    private PrintStream out;

    public FileIO() {}

    public FileIO(String nameFileIn, String nameFileOut) throws IOException {
        File fileOut = Paths.get(System.getProperty("user.dir"), nameFileOut).toFile();
        fileOut.createNewFile();
        this.in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(Paths.get(System.getProperty("user.dir"), nameFileIn).toFile())
                )
        );
        this.out = new PrintStream(new FileOutputStream(fileOut));
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
        this.out.flush();
        this.out.close();
    }
}
