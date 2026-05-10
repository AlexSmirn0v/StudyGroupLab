package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.security.UnrecoverableKeyException;

import model.CommandMessage;
import model.CommandFormat;

/**
 * Главный класс клиента.
 */
final public class ClientMain {
    static Deque<String> scriptHistory = new ArrayDeque<>();
    static boolean keepRunning = true;
    static boolean insideFile = false;
    static LoginManager loginManager = new LoginManager();
    static IOHandler ioHandler = new IOHandler();

    /**
     * Основной цикл обработки команд.
     * @param ioHandler сканер для чтения ввода
     * @param maker составитель запросов
     * @param connector соединение с сервером
     * @param consoleCharset кодировка консоли
     */
    private static void runLoop(PayloadMaker maker, TCPConnector connector) {
       while (keepRunning) {
            ioHandler.print("> ");

            if (!ioHandler.hasNextLine()) {
                if (insideFile) {
                    ioHandler.changeSource(System.in);
                    insideFile = false;
                    scriptHistory.clear();
                    ioHandler.println("Достигнут конец файла скрипта. Возвращение к консольному режиму.");
                    continue;
                }
                break;
            }
            
            String line = ioHandler.readLine().trim();
            if (line.isEmpty())
                continue;
            String[] parts = line.split("\\s+", 2);
            CommandFormat command;
            try {
                command = CommandFormat.getByName(parts[0]);
            } catch (IllegalArgumentException e) {
                ioHandler.println("Команда не распознана. Введите 'help' для получения списка доступных команд.");
                continue;
            }
            String username = loginManager.getUsername();
            String password = loginManager.getPassword();
            String argument = (parts.length > 1) ? parts[1].trim() : "";
            CommandMessage message = null;

            switch (command) {
                case EXIT:
                    if (insideFile) {
                        ioHandler.changeSource(System.in);
                        insideFile = false;
                        scriptHistory.clear();
                        ioHandler.println("Завершение выполнения скрипта. Возвращение к консольному режиму.");
                        break;
                    } else {
                        keepRunning = false;
                        ioHandler.println("Завершение выполнения программы");
                    }
                    break;
                case EXECUTE:
                    try {
                        java.io.File f = new java.io.File(argument);
                        String scriptPath = f.getCanonicalPath();
                        if (scriptHistory.contains(scriptPath)) {
                            ioHandler.println("Обнаружена рекурсия при выполнении скрипта: " + argument);
                            break;
                        }
                        scriptHistory.add(scriptPath);
                        ioHandler.changeSource(new BufferedInputStream(new FileInputStream(scriptPath)));
                        insideFile = true;
                    } catch (FileNotFoundException e) {
                        ioHandler.println("Файл не найден или не может быть открыт: " + argument);
                    } catch (java.io.IOException e) {
                        ioHandler.println("Ошибка при открытии скрипта: " + e.getMessage());
                    }
                    break;
                case ADD:
                case ADD_MIN:
                case REMOVE_LOW:
                    message = new CommandMessage(command, maker.askGroup(username), username, password);
                    break;
                case UPDATE:
                    message = new CommandMessage(command, maker.askUpdate(argument), username, password);
                    break;
                case REMOVE:
                    message = new CommandMessage(command, Long.valueOf(argument), username, password);
                    break;
                case FILTER:
                    message = new CommandMessage(command, argument, username, password);
                    break;
                default:
                    message = new CommandMessage(command, username, password);
            }

            if (message == null)
                continue;
            try {
                connector.sendMessage(message);
                Object response = connector.readResponse();
                ioHandler.printResponse(response);
            } catch (UnrecoverableKeyException e) {
                ioHandler.println("Ошибка aутентификации: " + e.getMessage());
                loginManager.askCredentials(ioHandler);
            } catch (IOException e) {
                ioHandler.println(e.getClass().getSimpleName() + e.getMessage());
                ioHandler.println("Отсутствует подключение к серверу. Повторите попытку ввода команды.");
            } catch (ClassNotFoundException e) {
                ioHandler.println("Не удалось прочитать ответ сервера: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        PayloadMaker maker = new PayloadMaker(ioHandler);

        try (TCPConnector connector = new TCPConnector(4000)) {
            ioHandler.println("Добро пожаловать! Введите 'help' для получения списка доступных команд.");
            loginManager.askCredentials(ioHandler);
            runLoop(maker, connector);
        } catch (IOException e) {
            ioHandler.println("Не удалось подключиться к серверу");
        }

    }
}
