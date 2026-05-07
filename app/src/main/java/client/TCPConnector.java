package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.UnrecoverableKeyException;

import model.CommandMessage;

/**
 * Клиентское соединение по TCP для коммуникации с сервером.
 */
public class TCPConnector implements AutoCloseable{
    private SocketChannel channel;
    private static final int MAX_RESPONSE_BYTES = 16 * 1024 * 1024;
    private static final int RECONNECT_ATTEMPTS = 5;
    private static final int RECONNECT_DELAY_MS = 2000;
    private final InetSocketAddress serverAddress;

    /**
     * Конструктор для подключения к серверу.
     * @param port порт сервера
     * @throws IOException при ошибке подключения
     */
    TCPConnector(int port) throws IOException{
        this.serverAddress = new InetSocketAddress("localhost", port);
        reconnect();
    }

    /**
     * Отправляет сообщение на сервер.
     * @param mess сообщение для отправки в виде объекта CommandMessage
     * @throws IOException при ошибке отправки
     */
    public void sendMessage(CommandMessage mess) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(mess);
        oos.flush();

        byte[] data = bos.toByteArray();
        int length = data.length;
        ByteBuffer buff = ByteBuffer.allocate(4 + length);
        buff.putInt(length);
        buff.put(data);
        buff.flip();

        try {
            writeFully(buff);
        } catch (IOException firstError) {
            reconnect();
            try {
                writeFully(buff);
            } catch (IOException secondError) {
                secondError.addSuppressed(firstError);
                throw secondError;
            }
        }
    }

    /**
     * Обрабатывает и десериализует ответ от сервера.
     * @return ответ сервера
     * @throws IOException при ошибке чтения
     * @throws ClassNotFoundException если класс ответа не найден
     * @throws UnrecoverableKeyException если пользователь отправил существующий логин с неверным паролем
     */
    public Object readResponse() throws IOException, ClassNotFoundException, UnrecoverableKeyException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(Integer.BYTES);
        readFully(lengthBuffer);
        lengthBuffer.flip();

        int responseLength = lengthBuffer.getInt();
        if (responseLength <= 0 || responseLength > MAX_RESPONSE_BYTES) {
            throw new IOException("Недопустимый размер ответа: " + responseLength);
        }

        ByteBuffer responseBuffer = ByteBuffer.allocate(responseLength);
        readFully(responseBuffer);

        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(responseBuffer.array()))) {
            Object result = input.readObject();
            if (result instanceof String && ((String) result).startsWith("Неверный пароль")) {
                throw new UnrecoverableKeyException((String) result);
            }
            return result;
        }
    }

    private void readFully(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int read = channel.read(buffer);
            if (read == -1) {
                throw new IOException("Соединение закрыто сервером");
            }
        }
    }

    private void writeFully(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int written = channel.write(buffer);
            if (written == -1) {
                throw new IOException("Соединение закрыто сервером");
            }
        }
    }

    /**
     * Пытается переподключиться к серверу с 5 попытками.
     * @throws IOException если все попытки к переподключению неудачны
     */
    private void reconnect() throws IOException {
        try {
            close();
        } catch (IOException e) {
            
        }

        for (int i = 1; i <= RECONNECT_ATTEMPTS; i++) {
            try {
                channel = SocketChannel.open();
                channel.connect(serverAddress);
                return;
            } catch (IOException e) {
                System.out.println("Сервер недоступен, переподключаюсь, попытка " + i);
                if (i == RECONNECT_ATTEMPTS) {
                    throw new IOException("Попытка переподключения не удалась", e);
                }
                sleepBeforeRetry();
            }
        }
    }

    private static void sleepBeforeRetry() throws IOException {
        try {
            Thread.sleep(RECONNECT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Подключение прервано", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }
}
