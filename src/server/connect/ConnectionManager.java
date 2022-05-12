package server.connect;

import java.util.HashMap;

public class ConnectionManager {
    private final HashMap<String, Connection> connections;
    private Connection processingClient;

    public ConnectionManager() {
        this.connections = new HashMap<>();
    }
    public ConnectionManager(HashMap<String, Connection> connections) {
        this.connections = connections;
    }

    public boolean isEmpty() {
        if (this.connections == null)
            return false;
        return this.connections.isEmpty();
    }

    public void setProcessingClient(String processingClient) {
        if (!this.connections.containsKey(processingClient))
            this.createNewConnection(processingClient);
        this.processingClient = this.connections.get(processingClient);
    }

    public Connection getProcessingClient() {
        return this.processingClient;
    }

    public void createNewConnection(String clientSocket) {
        if (!this.connections.containsKey(clientSocket))
            this.connections.put(clientSocket, new Connection(clientSocket));
    }

    public void getConnection(String nameConnection) {
        this.connections.get(nameConnection);
    }

    public void closeConnection(String clientSocket) {
        this.connections.remove(clientSocket);
    }
}
