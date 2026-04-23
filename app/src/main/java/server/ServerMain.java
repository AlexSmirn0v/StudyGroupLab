package server;

import model.CommandMessage;

import java.io.IOException;

public class ServerMain {
    private static final int PORT = 5000;
    private static final long SELECT_TIMEOUT_MS = 200;

    public static void main(String[] args) {
        String stdoutEncoding = System.getProperty("sun.stdout.encoding");
        if (stdoutEncoding == null || stdoutEncoding.isBlank()) {
            stdoutEncoding = System.getProperty("file.encoding", "UTF-8");
        }
        System.setProperty("LOG_CHARSET", stdoutEncoding);

        try {
            ServerConnector connect = new ServerConnector(PORT);
            ServerLogger.log("Сервер запущен на порте " + PORT);

            while (true) {
                connect.pump(SELECT_TIMEOUT_MS);

                CommandMessage request;
                while ((request = connect.pollRequest()) != null) {
                    ServerLogger.log("Обрабатывается команда" + request.command().getName());
                }
            }
        } catch (IOException e) {
            ServerLogger.log("Сервер не запустился из-за ошибки: " + e.getMessage());
        }
    }
}
