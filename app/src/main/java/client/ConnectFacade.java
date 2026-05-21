package client;

import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.Collection;

import model.CommandMessage;

public class ConnectFacade implements AutoCloseable {
    private final int PORT = 4000;
    private TCPConnector connector;

    public ConnectFacade() throws IOException {
        this.connector = new TCPConnector(PORT);
    }

    public String askServer(CommandMessage message) {
        try {
            connector.sendMessage(message);
            Object response = connector.readResponse();
            return parseResponse(response);
        } catch (UnrecoverableKeyException e) {
            return "Ошибка aутентификации: " + e.getMessage();
        } catch (IOException e) {
            return e.getClass().getSimpleName() + e.getMessage()
                    + "\nОтсутствует подключение к серверу. Повторите попытку отправки команды.";
        } catch (ClassNotFoundException e) {
            return "Не удалось прочитать ответ сервера: " + e.getMessage();
        }
    }

    private String parseResponse(Object response) {
        if (response == null) {
            return "Сервер вернул пустой ответ.";
        }

        if (response instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return "Коллекция пуста.";
            }
            StringBuilder sb = new StringBuilder();
            for (Object item : collection) {
                sb.append(item).append("\n");
            }
            return sb.toString();
        }

        return response.toString();
    }

    @Override
    public void close() throws IOException {
        if (connector != null) {
            connector.close();
        }
    }
}
