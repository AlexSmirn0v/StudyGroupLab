package server;

import model.CommandMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Главный класс сервера.
 */
public class ServerMain {
    private static final int PORT = 4000;
    private static final long SELECT_TIMEOUT_MS = 200;
    private static final int READ_POOL_SIZE = 4;
    private static final int PROCESS_POOL_SIZE = 8;
    private static final ServerHandler handler = new ServerHandler();
    private static final AtomicBoolean running = new AtomicBoolean(true);

    // Массив из одного элемента статуса для расширяемости и передачи элемента в
    // качестве ссылки
    private static final boolean[] consoleStatus = new boolean[] { true };
    private static final ExecutorService readPool = Executors.newFixedThreadPool(READ_POOL_SIZE);
    private static final ExecutorService processPool = Executors.newFixedThreadPool(PROCESS_POOL_SIZE);

    public static void main(String[] args) {
        try {
            ServerConnector connect = new ServerConnector(PORT);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            ServerLogger.log("Сервер запущен на порте " + PORT);
            while (running.get()) {
                if (consoleReader.ready()) {
                    String consoleInput = consoleReader.readLine();
                    if (consoleInput != null) {
                        ServerLogger.log(handler.runConsole(consoleInput, consoleStatus));
                        if (!consoleStatus[0]) {
                            running.set(false);
                        }
                    }
                }

                connect.pump(SELECT_TIMEOUT_MS);

                ServerConnector.IncomingRequest incoming;
                while ((incoming = connect.pollRequest()) != null) {
                    final ServerConnector.IncomingRequest queuedIncoming = incoming;
                    readPool.submit(() -> {
                        CommandMessage request = ReqReader.parse(queuedIncoming.payload());
                        if (request == null) {
                            return;
                        }
                        processPool.submit(() -> processRequest(connect, queuedIncoming, request));
                    });
                }
            }
        } catch (IOException e) {
            ServerLogger.log("Сервер не запустился из-за ошибки: " + e.getMessage());
        } finally {
            shutdownPool(readPool, "пул чтения запросов");
            shutdownPool(processPool, "пул обработки запросов");
        }
    }

    private static void processRequest(ServerConnector connect, ServerConnector.IncomingRequest incoming,
            CommandMessage request) {
        ServerLogger.log("Обрабатывается команда " + request.command().getName());
        Object result = handler.run(request);
        Class<?> expected = request.command().getRespClass();
        if (result == null) {
            return;
        }
        if (!expected.isAssignableFrom(result.getClass()) && result.getClass() != String.class) {
            ServerLogger.log("Пропуск ответа: ожидался " + expected.getSimpleName()
                    + ", получено " + result.getClass().getSimpleName());
            return;
        }
        if (!(result instanceof Serializable serializableResult)) {
            ServerLogger.log("Пропуск ответа: результат не оцифруем");
            return;
        }

        try {
            byte[] rawResponse = RespSender.serialize(serializableResult);
            new Thread(connect.getQueueRunnable(incoming.client(), rawResponse)).start();
        } catch (IOException e) {
            ServerLogger.log("Не удалось сериализовать ответ: " + e.getMessage());
        }
    }

    private static void shutdownPool(ExecutorService pool, String poolName) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ServerLogger.log("Остановка прервана: " + poolName);
        }
    }
}
