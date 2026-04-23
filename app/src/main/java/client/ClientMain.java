package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

import model.CommandMessage;
import model.CommandFormat;

final public class ClientMain {
    static Deque<String> scriptHistory = new ArrayDeque<>();
    static boolean keepRunning = true;
    static boolean insideFile = false;

    private static void runLoop(Scanner scan, PayloadMaker maker, TCPConnector connector, Charset consoleCharset) {
       while (keepRunning) {
            System.out.print("> ");

            if (!scan.hasNextLine())
                break;

            String line = scan.nextLine().trim();
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\\s+", 2);
            CommandFormat command;
            try {
                command = CommandFormat.getByName(parts[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Команда не распознана. Введите 'help' для получения списка доступных команд.");
                continue;
            }
            String argument = (parts.length > 1) ? parts[1].trim() : "";
            CommandMessage message = null;

            switch (command) {
                case EXIT:
                    if (insideFile) {
                        scan = new Scanner(System.in, consoleCharset);
                        insideFile = false;
                        scriptHistory.clear();
                        System.out.println("Завершение выполнения скрипта. Возвращение к консольному режиму.");
                    } else {
                        keepRunning = false;
                        System.out.println("Завершение выполнения программы");
                    }
                    break;
                case EXECUTE:
                    try {
                        java.io.File f = new java.io.File(argument);
                        String scriptPath = f.getCanonicalPath();
                        if (scriptHistory.contains(scriptPath)) {
                            System.out.println("Обнаружена рекурсия при выполнении скрипта: " + argument);
                            break;
                        }
                        scriptHistory.add(scriptPath);
                        scan = new Scanner(new BufferedInputStream(new FileInputStream(scriptPath)), consoleCharset);
                        insideFile = true;
                    } catch (FileNotFoundException e) {
                        System.out.println("Файл не найден или не может быть открыт: " + argument);
                    } catch (java.io.IOException e) {
                        System.out.println("Ошибка при открытии скрипта: " + e.getMessage());
                    }
                    break;
                case ADD:
                case ADD_MIN:
                case REMOVE_LOW:
                    message = new CommandMessage(command, maker.askGroup());
                    break;
                case UPDATE:
                    message = new CommandMessage(command, maker.askUpdate(argument));
                    break;
                case REMOVE:
                    message = new CommandMessage(command, Long.valueOf(argument));
                    break;
                case FILTER:
                    message = new CommandMessage(command, argument);
                    break;
                default:
                    message = new CommandMessage(command);
            }

            if (message == null)
                continue;
            try {
                connector.sendMessage(message);
            } catch (IOException e) {
                System.out.println(e.getClass().getSimpleName() + e.getMessage());
                System.out.println("Отсутствует подключение к серверу");
            }
        }
    }

    public static void main(String[] args) {
        Charset consoleCharset = (System.console() != null)
                ? System.console().charset()
                : Charset.defaultCharset();

        Scanner scan = new Scanner(System.in, consoleCharset);
        PayloadMaker maker = new PayloadMaker(scan);

        try (TCPConnector connector = new TCPConnector(5000)) {
            System.out.println("Добро пожаловать! Введите 'help' для получения списка доступных команд.");
            runLoop(scan, maker, connector, consoleCharset);
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу");
        }

    }
}
