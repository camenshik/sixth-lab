package server;

import com.opencsv.exceptions.CsvException;
import server.connect.ServerManager;
import server.connect.ServerUDP;
import server.exceptions.AnnotationsException;
import server.exceptions.EotException;
import server.exceptions.PreloadException;
import server.exceptions.WrapperException;

import java.io.IOException;
import java.net.BindException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            CommandHandler commandHandler = preload(args);
            startServer(commandHandler);
        } catch (PreloadException e) {
            System.out.println("Неполучилось загрузить необходимые ресурсы");
        } catch (IllegalArgumentException e) {
            System.out.printf("Ошибка ввода: %s\n", e.getMessage());
        } catch (NullPointerException | NoSuchElementException e) {
            System.out.println("Ввод прерван");
        }
    }

    public static CommandHandler preload(String[] args) throws PreloadException {
        String nameFileCsv;
        CommandHandler commandHandler;
        if (args.length == 1) {
            nameFileCsv = args[0];
        } else {
            Scanner in = new Scanner(System.in);
            System.out.println("Введите имя файла сохранения коллекции:");
            nameFileCsv = in.nextLine();
        }
        try {
            commandHandler = new CommandHandler(nameFileCsv);
            commandHandler.loadDataOrganisations();
        } catch (CsvException e) {
            System.out.printf("Ошибка чтения файла коллекции - %s\n", e.getMessage());
            throw new PreloadException();
        } catch (IOException e) {
            System.out.printf("Ошибка чтения/записи данных - %s\n", e.getMessage());
            throw new PreloadException();
        } catch (ReflectiveOperationException |
                WrapperException |
                AnnotationsException e) {
            System.out.println(e.getMessage());
            throw new PreloadException();
        }
        return commandHandler;
    }

    public static void startServer(CommandHandler commandHandler) {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("Введите порт:");
            int port = Integer.parseInt(in.nextLine());
            try {
                ServerManager connectionManager = new ServerManager(
                        new ServerUDP(port, commandHandler)
                );
                connectionManager.starting();
                connectionManager.working();
                connectionManager.stopping();
                break;
            } catch (BindException e) {
                System.out.println("Введенный порт сервера занят");
            } catch (IOException | IllegalArgumentException e) {
                System.out.printf("Ошибка ввода: %s\n", e.getMessage());
                break;
            } catch (NullPointerException | NoSuchElementException | EotException e) {
                System.out.println("Ввод прерван");
                break;
            }
        }
    }
}
