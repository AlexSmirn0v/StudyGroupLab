package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import model.CommandMessage;

/**
 * Класс для десериализации и парсинга входящих запросов.
 */
public class ReqReader {
    /**
     * Десериализует массив байтов в объект.
     * @param data массив байтов
     * @return десериализованный объект
     * @throws IOException при ошибке ввода-вывода
     * @throws ClassNotFoundException если класс объекта не найден
     */
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return objectInput.readObject();
        }
    }

    /**
     * Парсит массив байтов запроса от клиента в объект CommandMessage.
     * @param payload массив байтов
     * @return объект CommandMessage или null если ошибка десериализации
     * @throws IllegalStateException если полученный объект не является CommandMessage
     */
    public static CommandMessage parse(byte[] payload) throws IllegalStateException {
        Object request;
        try {
            request = deserialize(payload);
        } catch (ClassNotFoundException | IOException e) {
            ServerLogger.log("Объект не получилось распарсить");
            return null;
        }
        if (!(request instanceof CommandMessage commandMessage)) {
            throw new IllegalStateException("Ожидался CommandMessage, получено: " + request.getClass().getName());
        }
        return commandMessage;
    }
}
