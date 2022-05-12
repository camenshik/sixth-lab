package server.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;

public class SystemIO implements WorkingWithIO, WorkingWithLocalIO, Closeable {
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

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
    }
}