package server.IO;

import server.exceptions.ManagerIOException;

import java.io.IOException;

public class ManagerIO {
    private WorkingWithIO io;
    private SystemIO systemIO;
    private ServerIO serverIO;

    public ManagerIO() {
        this.io = new SystemIO();
    }

    public ManagerIO(ServerIO serverIO) {
        this.io = new SystemIO();
        this.serverIO = serverIO;
    }

    public ManagerIO(WorkingWithLocalIO io, ServerIO serverIO) {
        this.io = io;
        this.serverIO = serverIO;
    }

    public void setServerIO(ServerIO serverIO) {
        this.serverIO = serverIO;
    }

    public WorkingWithIO getIO() {
        return this.io;
    }

    public SystemIO getSystemIO() {
        return this.systemIO;
    }

    public void changeIO(TypeIO typeIO) throws IOException, ManagerIOException {
        switch (typeIO) {
            case SYSTEM:
                if (this.systemIO == null) {
                    this.systemIO = new SystemIO();
                }
                this.io = this.systemIO;
                break;
            case FILE:
                this.io = new FileIO("in.txt", "out.txt");
                break;
            case SERVER:
                this.io = this.serverIO;
                break;
            default:
                throw new ManagerIOException();
        }
    }

    public void changeIO(TypeIO typeIO, String nameIn, String nameOut) throws IOException, ManagerIOException {
        switch (typeIO) {
            case SYSTEM:
                if (this.systemIO == null) {
                    this.systemIO = new SystemIO();
                }
                this.io = this.systemIO;
                break;
            case FILE:
                this.io = new FileIO(nameIn, nameOut);
                break;
            case SERVER:
                this.io = this.serverIO;
                break;
            default:
                throw new ManagerIOException();
        }
    }

    public void closeIO() throws IOException {
        if (this.io instanceof Closeable) {
            ((Closeable) this.io).close();
        }
    }

    public void closeAll() throws IOException {
        this.closeIO();
        this.systemIO.close();
    }
}
