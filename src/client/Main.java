package client;

import client.connect.ClientManager;
import client.connect.ClientUDP;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static int port;
    private static String receiveToAddress;
    private static int receiveToPort;
    private static SocketAddress receiveTo;

    public static void input(String[] args) {
        if (args.length == 3) {
            port = Integer.parseInt(args[0]);
            receiveTo = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
        } else {
            Scanner in = new Scanner(System.in);
            if (port == 0) {
                System.out.println("Введите порт клиента:");
                port = Integer.parseInt(in.nextLine());
            }
            if (receiveToAddress == null) {
                System.out.println("Введите адрес сервера:");
                receiveToAddress = in.nextLine();
            }
            if (receiveToPort == 0) {
                System.out.println("Введите порт сервера:");
                receiveToPort = Integer.parseInt(in.nextLine());
            }
            receiveTo = new InetSocketAddress(receiveToAddress, receiveToPort);
        }
    }

    public static void main(String[] args) {
        while (true) {
            try {
                input(args);
                ClientManager connectionManager = new ClientManager(
                        new ClientUDP(receiveTo, port, 15)
                );
                connectionManager.starting();
                connectionManager.working();
                connectionManager.stopping();
                break;
            } catch (BindException e) {
                port = 0;
                System.out.println("Порт для клиента занят, повторите ввод");
            } catch (SocketException e) {
                System.out.printf("Ошибка сокета - %s\n", e.getMessage());
            } catch (IOException | IllegalArgumentException e) {
                System.out.printf("Ошибка ввода: %s\n", e.getMessage());
                break;
            } catch (NullPointerException | NoSuchElementException e) {
                System.out.println("Ввод прерван");
                break;
            }
        }
    }
}
