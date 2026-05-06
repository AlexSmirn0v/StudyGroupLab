package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Класс для сериализации ответов в массив байтов.
 */
public final class RespSender {
    /**
     * Сериализует объект в массив байтов.
     * @param content объект для сериализации
     * @return массив байтов, представляющий объект
     * @throws IOException при ошибке ввода-вывода
     * @throws IllegalArgumentException если content равен null
     */
    public static byte[] serialize(Serializable content) throws IOException, IllegalArgumentException {
        if (content == null) {
            throw new IllegalArgumentException("Ответ не может быть null");
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(content);
            output.flush();
        }
        return bytes.toByteArray();
    }
}
