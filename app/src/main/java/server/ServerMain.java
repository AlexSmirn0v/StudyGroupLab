package server;

import model.CommandMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public class ServerMain {
    private static final int PORT = 5000;
    private static final long SELECT_TIMEOUT_MS = 200;
    private static ServerHandler handler = new ServerHandler();

    public static void main(String[] args) {
        String stdoutEncoding = System.getProperty("sun.stdout.encoding");
        if (stdoutEncoding == null || stdoutEncoding.isBlank()) {
            stdoutEncoding = System.getProperty("file.encoding", "UTF-8");
        }
        System.setProperty("LOG_CHARSET", stdoutEncoding);

        try {
            ServerConnector connect = new ServerConnector(PORT);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            ServerLogger.log("Сервер запущен на порте " + PORT);

            while (true) {
                if (consoleReader.ready()) {
                    String consoleInput = consoleReader.readLine();
                    if (consoleInput != null) {
                        ServerLogger.log(handler.runConsole(consoleInput));
                    }
                }

                connect.pump(SELECT_TIMEOUT_MS);

                ServerConnector.IncomingRequest incoming;
                CommandMessage request;
                while ((incoming = connect.pollRequest()) != null &&
                        (request = ReqReader.parse(incoming.payload())) != null) {
                    ServerLogger.log("Обрабатывается команда " + request.command().getName());
                    Object result = handler.run(request);
                    Class<?> expected = request.command().getRespClass();
                    if (result == null) {
                        continue;
                    }
                    if (!expected.isAssignableFrom(result.getClass())) {
                        ServerLogger.log("Пропуск ответа: ожидался " + expected.getSimpleName()
                                + ", получено " + result.getClass().getSimpleName());
                        continue;
                    }
                    if (!(result instanceof Serializable serializableResult)) {
                        ServerLogger.log("Пропуск ответа: результат не оцифруем");
                        continue;
                    }

                    ServerLogger.log(serializableResult.toString());
                    byte[] rawResponse = RespSender.serialize(serializableResult);
                    connect.queueResponse(incoming.client(), rawResponse);
                }
            }
        } catch (IOException e) {
            ServerLogger.log("Сервер не запустился из-за ошибки: " + e.getMessage());
        }
    }
}
