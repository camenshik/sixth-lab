package server.IO;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileIO implements WorkingWithIO, WorkingWithLocalIO, Closeable {
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
    public String input() throws IOException {
        return this.in.readLine();
    }

    @Override
    public void output(String data) throws IOException {
        this.out.println(data);
    }

    @Override
    public void output(String[] data) throws IOException {
        Arrays.stream(data).forEach(this.out::println);
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.flush();
        this.out.close();
    }

    @Override
    public void outputComposite(String data) {
        this.out.println(data);
    }

    @Override
    public void outputComposite(String[] data) {
        Arrays.stream(data).forEach(this.out::println);
    }

    @Override
    public void endOutputComposite(boolean requestMore) {
        this.out.print("");
    }
}
