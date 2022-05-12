package client.connect;

import client.response.Response;
import client.response.ResponseReader;
import client.response.TypeResponse;
import client.response.ResponseFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientUDP extends AbstractClient {
    private DatagramChannel datagramChannel;

    public ClientUDP(SocketAddress serverSocket, int port, int maxLimitWaitingResponse) throws IOException {
        super(serverSocket, port, maxLimitWaitingResponse);
        this.datagramChannel = DatagramChannel.open();
        this.datagramChannel.configureBlocking(false);
    }

    public ClientUDP(
            SocketAddress serverSocket,
            int outBufferLength,
            int port,
            int maxLimitWaitingResponse) throws IOException {
        super(serverSocket, outBufferLength, port, maxLimitWaitingResponse);
        this.datagramChannel = DatagramChannel.open();
        this.datagramChannel.configureBlocking(false);
    }

    @Override
    public void start() throws IOException {
        this.statusClient = StatusClient.WORKING;
        this.datagramChannel.bind(new InetSocketAddress("localhost", this.port));
    }

    @Override
    public void stopping() throws IOException {
        this.send(TypeResponse.BYE, new String[]{});
        this.statusClient = StatusClient.STOPPING;
        this.datagramChannel.close();
        this.statusClient = StatusClient.STOPPED;
    }

    @Override
    public void send(TypeResponse typeResponse, String[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
        oos.writeObject(ResponseFactory.createResponse(typeResponse, data));
        oos.flush();
        this.datagramChannel.send(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()), this.serverSocket);
    }

    @Override
    public boolean generationRequest() throws IOException {
        if (this.commandHandler.getIsExit()) {
            this.stopping();
            return false;
        } else if (this.commandHandler.getServerCommands() == null) {
            this.send(TypeResponse.GREETING, new String[] {});
            this.commandHandler.getManagerIO().getIO().getOut().println("Получение набора серверных комманд...");
            if (this.waitingResponse()) {
                this.commandHandler.getManagerIO().getIO().getOut().println("Серверные комманды получены");
                this.waitingResponse();
            }
            return true;
        }

        String command = this.commandHandler.interactiveMod();

        if (command.equals(""))
            return false;

        this.send(TypeResponse.COMMAND, new String[] {command});
        this.waitingResponse();
        return true;
    }

    @Override
    public boolean waitingResponse() throws IOException {
        SocketAddress serverSocket;
        this.waitingIterations = 0;
        do {
            if (this.waitingIterations == this.maxLimitWaitingResponse) {
                if (this.commandHandler.askAnswer(
                        "Превышен лимит ожидания ответа от сервера. \nВы хотите продолжить ожидание? (YES/NO)"
                )) {
                    this.waitingIterations = 0;
                } else {
                    return false;
                }
            }
            serverSocket = this.datagramChannel.receive(this.inputBuffer);
            if (serverSocket != null) {
                this.processingResponse();
            } else {
                if (this.waitingIterations == 3) {
                    this.commandHandler.getManagerIO().getIO().getOut().println("Ожидание ответа от сервера...");
                }
                this.sleep();
            }
            this.waitingIterations++;
        } while(serverSocket == null);
        return true;
    }

    @Override
    public void processingResponse() {
        try {
            Response response = ResponseReader.getResponse(this.parseResponse());
            switch (response.getTypeResponse()) {
                case GREETING:
                    this.commandHandler.setServerCommands(
                            new ArrayList<>(Arrays.stream(response.getBody()).collect(Collectors.toList()))
                    );
                    break;
                case QUESTION:
                    if (this.commandHandler.askAnswer(response.getBody()[0])) {
                        this.send(TypeResponse.ANSWER, new String[]{"true"});
                        this.waitingResponse();
                    } else {
                        this.send(TypeResponse.ANSWER, new String[]{"false"});
                    }
                    break;
                case DATA:
                    for (String bodyResponse : response.getBody()) {
                        this.commandHandler.getManagerIO().getIO().getOut().printf("%s\n", bodyResponse);
                    }
                    break;
                case COMPOSITE_DATA:
                    for (String bodyResponse : response.getBody()) {
                        this.commandHandler.getManagerIO().getIO().getOut().printf("%s\n", bodyResponse);
                    }
                    this.waitingResponse();
                    break;
                case END_COMPOSITE_DATA:
                    if (response.getBody().length < 1)
                        break;
                    if (response.getBody()[0].equals("request_more")) {
                        this.send(TypeResponse.DATA, new String[]{this.commandHandler.inputData()});
                        this.waitingResponse();
                    }
                    break;
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.out.printf("Ошибка - %s\n", e.getMessage());
        }
    }

    @Override
    public Object parseResponse() throws IOException, ClassNotFoundException {
        this.inputBuffer.flip();
        byte[] bytes = new byte[this.inputBuffer.remaining()];
        this.inputBuffer.get(bytes);
        try {
            this.ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        } catch (EOFException e) {
            this.commandHandler.getManagerIO().getSystemIO().getOut().println("Ошибка: получен пустой объект");
        }
        Object response = null;
        if (this.ois != null)
            response = this.ois.readObject();
        this.inputBuffer.compact();
        return response;
    }
}
