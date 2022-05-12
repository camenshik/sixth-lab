package client.connect;

import client.exceptions.RecursionException;

import java.io.IOException;

public class ClientManager {
    private AbstractClient client;

    public ClientManager() {}

    public ClientManager(AbstractClient client) {
        this.client = client;
    }

    public AbstractClient getClient() {
        return client;
    }

    public void starting() throws IOException {
        this.client.start();
    }

    public void starting(AbstractClient client) throws IOException {
        if (this.client != null)
            this.client.stopping();
        this.client = client;
        this.client.start();
    }

    public void working() throws IOException {
        while (this.client.statusClient.equals(StatusClient.WORKING)) {
            try {
                this.client.generationRequest();
            } catch (RecursionException e) {
                System.out.println("Ошибка - превышение лимита рекурсии");
            }
        }
    }

    public void stopping() throws IOException {
        //this.client.stopping();
    }
}
