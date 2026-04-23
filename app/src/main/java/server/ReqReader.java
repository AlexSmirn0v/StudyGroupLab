package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import model.CommandMessage;

public class ReqReader {
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return objectInput.readObject();
        }
    }

    public static CommandMessage parse(byte[] payload) {
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
