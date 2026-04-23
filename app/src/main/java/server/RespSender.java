package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class RespSender {
    public static byte[] serialize(Serializable content) throws IOException {
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
