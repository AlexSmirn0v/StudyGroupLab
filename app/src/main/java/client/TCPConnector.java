package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import model.CommandMessage;

public class TCPConnector implements AutoCloseable{
    private SocketChannel channel;
    private static final int MAX_RESPONSE_BYTES = 16 * 1024 * 1024;
    private static final int RECONNECT_ATTEMPTS = 5;
    private static final int RECONNECT_DELAY_MS = 2000;
    private final InetSocketAddress serverAddress;

    TCPConnector(int port) throws IOException{
        this.serverAddress = new InetSocketAddress("localhost", port);
        reconnect();
    }

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

    public Object readResponse() throws IOException, ClassNotFoundException {
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
            return input.readObject();
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
