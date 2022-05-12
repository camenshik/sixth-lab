package server.connect;

import server.CommandHandler;
import server.exceptions.CommandException;
import server.response.ResponseFactory;
import server.response.Response;
import server.response.ResponseReader;
import server.response.TypeResponse;

import java.io.*;
import java.net.*;

public class ServerUDP extends AbstractServer {
    private final DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    public ServerUDP(int port, CommandHandler commandHandler) throws IOException {
        super(port, commandHandler);
        this.datagramSocket = new DatagramSocket(new InetSocketAddress("localhost", this.port));
    }

    public ServerUDP(int inputBufferLength, int port, CommandHandler commandHandler) throws IOException {
        super(inputBufferLength, port, commandHandler);
        this.datagramSocket = new DatagramSocket(new InetSocketAddress("localhost", this.port));
    }

    @Override
    public void start() throws SocketException {
        this.datagramSocket.setSoTimeout(this.timeOut);
        this.statusServer = StatusServer.WORKING;
        System.out.println("Сервер запущен");
    }

    @Override
    public void send(TypeResponse typeResponse, String[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
        oos.writeObject(ResponseFactory.createResponse(typeResponse, data));
        oos.flush();
        this.datagramSocket.send(new DatagramPacket(
                byteArrayOutputStream.toByteArray(),
                byteArrayOutputStream.toByteArray().length,
                this.datagramPacket.getAddress(),
                this.datagramPacket.getPort()
        ));
    }

    @Override
    public void stopping() throws IOException {
        this.statusServer = StatusServer.STOPPING;
        this.datagramSocket.close();
        this.statusServer = StatusServer.STOPPED;
    }

    @Override
    public void waitingRequest() throws IOException {
        if (this.commandHandler.getIsExit()) {
            this.stopping();
            return;
        }

        this.inputBuffer = new byte[this.limitByteBuffer];
        this.datagramPacket = new DatagramPacket(
                this.inputBuffer,
                this.inputBuffer.length
        );
        try {
            this.datagramSocket.receive(datagramPacket);
        } catch (SocketTimeoutException e) {
        }
        if (this.datagramPacket.getAddress() == null || this.datagramPacket.getPort() == 0) {
            this.clientSocket = null;
            return;
        }
        this.clientSocket = String.format(
                "%s:%s", this.datagramPacket.getAddress(), this.datagramPacket.getPort()
        );
        this.connectionManager.setProcessingClient(clientSocket);
        this.processingRequest();
    }

    @Override
    public String processingRequest() {
        Response request;
        try {
            request = ResponseReader.getResponse(this.parseRequest());
            if (request != null) {
                TypeResponse typeResponse = request.getTypeResponse();
                switch (typeResponse) {
                    case GREETING:
                        this.connectionManager.createNewConnection(this.clientSocket);
                        this.send(TypeResponse.GREETING, new String[]{});
                        if (!this.connectionManager.getProcessingClient().getCommands().isEmpty()) {
                            this.send(TypeResponse.QUESTION, new String[]{
                                    "Обнаружен незаконченный ввод составной комманды, вы хотите его продолжить? (YES/NO)"
                            });
                        } else {
                            this.send(TypeResponse.NONE, new String[]{});
                        }
                        break;
                    case ANSWER:
                        if (this.connectionManager.getProcessingClient().getCommands().isEmpty())
                            break;
                        if (request.getBody()[0].trim().equalsIgnoreCase("true")) {
                            this.commandHandler.getManagerIO().getIO().outputComposite("");
                            this.commandHandler.interactiveMod(
                                    this.connectionManager.getProcessingClient().getExecuteCommand().getName()
                            );
                        } else {
                            this.connectionManager.getProcessingClient().clearCommands();
                        }
                        break;
                    case COMMAND:
                        this.commandHandler.interactiveMod(request.getBody()[0]);
                        break;
                    case DATA:
                        if (this.connectionManager.getProcessingClient().getCommands().isEmpty())
                            return request.getBody()[0];
                        try {
                            Command command = this.connectionManager.getProcessingClient().getExecuteCommand();
                            command.addArg(request.getBody()[0]);
                            this.commandHandler.executeCommand(command.getName());
                        } catch (CommandException e) {
                            break;
                        }
                        break;
                    case BYE:
                        this.connectionManager.closeConnection(this.clientSocket);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.out.printf("Ошибка - %s\n", e.getMessage());
        }
        return "";
    }

    @Override
    public Object parseRequest() throws IOException, ClassNotFoundException {
        try {
            this.ois = new ObjectInputStream(new ByteArrayInputStream(this.inputBuffer));
        } catch (EOFException e) {
            this.commandHandler.getManagerIO().getSystemIO().output("Ошибка: получен пустой объект");
        }
        Object response = null;
        if (this.ois != null)
            response = this.ois.readObject();
        return response;
    }
}
