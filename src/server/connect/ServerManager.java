package server.connect;

import server.exceptions.RecursionException;
import server.IO.ServerIO;
import server.IO.TypeIO;
import server.exceptions.ManagerIOException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerManager {
    private AbstractServer server;
    private final Scanner systemScanner;

    public ServerManager() {
        this.systemScanner = new Scanner(System.in);
    }

    public ServerManager(AbstractServer server) {
        this.server = server;
        this.systemScanner = new Scanner(System.in);
    }

    public AbstractServer getServer() {
        return server;
    }

    public void starting() throws IOException {
        this.server.start();
    }

    public void starting(AbstractServer connection) throws IOException {
        if (this.server != null)
            this.server.stopping();
        this.server = connection;
        this.server.start();
    }

    public void working() throws IOException {
        try {
            this.server.commandHandler.getManagerIO().setServerIO(new ServerIO(this.server));
            this.server.commandHandler.getManagerIO().changeIO(TypeIO.SERVER);
        } catch (ManagerIOException e) {
            System.out.println(e.getMessage());
            return;
        }
        while (this.server.statusServer.equals(StatusServer.WORKING)) {
            if (this.hookForSystemMode())
                continue;
            this.server.waitingRequest();
        }
    }

    public void stopping() throws IOException {
        //this.server.stopping();
    }

    public boolean hookForSystemMode() throws IOException {
        if (System.in.available() != 0) {
            try {
                this.server.commandHandler.getManagerIO().changeIO(TypeIO.SYSTEM);
                this.server.commandHandler.interactiveMod(this.systemScanner.nextLine());
                this.server.commandHandler.getManagerIO().changeIO(TypeIO.SERVER);
            } catch (IOException | ManagerIOException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Сервер остановлен");
                this.server.stopping();
            } catch (RecursionException e) {
                System.out.println("Ошибка - превышение лимита рекурсии");
            }
            return true;
        }
        return false;
    }
}
