package client.IO;

import client.exceptions.ManagerIOException;

import java.io.IOException;

public class ManagerIO {
    private WorkingWithIO io;
    private SystemIO systemIO;

    public ManagerIO() {
        this.systemIO = new SystemIO();
        this.io = this.systemIO;
    }

    public ManagerIO(WorkingWithIO io) {
        this.io = io;
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
            default:
                throw new ManagerIOException();
        }
    }

    public void closeIO() throws IOException {
        this.io.close();
    }

    public void closeAll() throws IOException {
        this.closeIO();
        this.systemIO.close();
    }
}
