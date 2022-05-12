package server.connect;

import server.CommandHandler;
import server.response.TypeResponse;

import java.io.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractServer {
    protected int limitByteBuffer;
    protected byte[] inputBuffer;
    protected int port;
    protected ObjectInputStream ois;
    protected StatusServer statusServer;
    protected String clientSocket;
    protected CommandHandler commandHandler;
    protected ConnectionManager connectionManager;
    protected final int timeOut;

    public AbstractServer(int port, CommandHandler commandHandler) {
        this.limitByteBuffer = 4096;
        this.inputBuffer = new byte[this.limitByteBuffer];
        this.port = port;
        this.commandHandler = commandHandler;
        this.connectionManager = new ConnectionManager();
        this.commandHandler.setConnectionManager(this.connectionManager);
        this.statusServer = StatusServer.STARTING;
        this.timeOut = 100;
    }

    public AbstractServer(int limitByteBuffer, int port, CommandHandler commandHandler) {
        this.limitByteBuffer = limitByteBuffer;
        this.inputBuffer = new byte[this.limitByteBuffer];
        this.port = port;
        this.commandHandler = commandHandler;
        this.connectionManager = new ConnectionManager();
        this.commandHandler.setConnectionManager(this.connectionManager);
        this.statusServer = StatusServer.STARTING;
        this.timeOut = 100;
    }

    public void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(this.timeOut);
        } catch (InterruptedException e) {
            System.out.printf("Ошибка - %s\n", e.getMessage());
        }
    }

    public abstract void start() throws IOException;
    public abstract void send(TypeResponse typeResponse, String[] data) throws  IOException;
    public abstract void waitingRequest() throws IOException;
    public abstract String processingRequest() throws IOException;
    public abstract void stopping() throws IOException;
    public abstract Object parseRequest() throws IOException, ClassNotFoundException;
}
