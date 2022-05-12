package client.connect;

import client.CommandHandler;
import client.response.TypeResponse;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClient {
    protected ByteBuffer inputBuffer;
    protected int port;
    protected ObjectInputStream ois;
    protected SocketAddress serverSocket;
    protected CommandHandler commandHandler;
    protected final int maxLimitWaitingResponse;
    protected int waitingIterations;
    protected StatusClient statusClient;

    public AbstractClient(SocketAddress serverSocket, int port, int maxLimitWaitingResponse) throws IOException {
        this.inputBuffer = ByteBuffer.allocate(4096);
        this.port = port;
        this.serverSocket = serverSocket;
        this.commandHandler = new CommandHandler(this);
        this.maxLimitWaitingResponse = maxLimitWaitingResponse;
        this.waitingIterations = 0;
        this.statusClient = StatusClient.STARTING;
    }

    public AbstractClient(SocketAddress serverSocket,
                          int inputBufferLength,
                          int port,
                          int maxLimitWaitingResponse) throws IOException {
        this.inputBuffer = ByteBuffer.allocate(inputBufferLength);
        this.port = port;
        this.serverSocket = serverSocket;
        this.commandHandler = new CommandHandler(this);
        this.maxLimitWaitingResponse = maxLimitWaitingResponse;
        this.waitingIterations = 0;
        this.statusClient = StatusClient.STARTING;
    }

    public void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            System.out.printf("Ошибка - %s\n", e.getMessage());
        }
    }

    public abstract void start() throws IOException;
    public abstract void stopping() throws IOException;
    public abstract void send(TypeResponse typeResponse, String[] data) throws  IOException;
    public abstract boolean generationRequest() throws IOException;
    public abstract boolean waitingResponse() throws IOException;
    public abstract void processingResponse() throws IOException;
    public abstract Object parseResponse() throws IOException, ClassNotFoundException;
}
