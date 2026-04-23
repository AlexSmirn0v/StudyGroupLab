package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class ServerConnector {
    private static final int LENGTH_BYTES = Integer.BYTES;
    private static final int MAX_MESSAGE_BYTES = 16 * 1024 * 1024;

    private final int port;
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final Queue<IncomingRequest> payloadQueue = new ArrayDeque<>();

    ServerConnector(int port) throws IOException {
        this.port = port;
        this.selector = Selector.open();
        this.serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(this.port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    static class ClientState {
        final ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_BYTES);
        ByteBuffer dataBuffer;
        final Queue<ByteBuffer> pendingWrites = new ArrayDeque<>();
    }

    public record IncomingRequest(SocketChannel client, byte[] payload) {
    }

    public void pump(long timeoutMs) throws IOException {
        if (timeoutMs <= 0) {
            selector.selectNow();
        } else {
            selector.select(timeoutMs);
        }

        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();

            if (!key.isValid()) {
                continue;
            }

            try {
                if (key.isAcceptable()) {
                    accept();
                }
                if (key.isReadable()) {
                    read(key);
                }
                if (key.isWritable()) {
                    write(key);
                }
            } catch (IOException e) {
                closeClient(key, "I/O ошибка: " + e.getMessage());
            } catch (ClassNotFoundException | IllegalStateException e) {
                closeClient(key, "Недопустимый формат содержания запроса: " + e.getMessage());
            }
        }
    }

    public IncomingRequest pollRequest() {
        return payloadQueue.poll();
    }

    public void queueResponse(SocketChannel client, byte[] payload) throws IOException {
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }
        if (payload.length > MAX_MESSAGE_BYTES) {
            throw new IllegalArgumentException("Размер ответа превышает лимит: " + payload.length);
        }

        SelectionKey key = client.keyFor(selector);
        if (key == null || !key.isValid()) {
            throw new IOException("Клиент не зарегистрирован в селекторе");
        }

        ClientState state = (ClientState) key.attachment();
        if (state == null) {
            throw new IllegalStateException("Отсутствует состояние клиента");
        }

        ByteBuffer framed = ByteBuffer.allocate(LENGTH_BYTES + payload.length);
        framed.putInt(payload.length);
        framed.put(payload);
        framed.flip();

        state.pendingWrites.offer(framed);
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    }

    private void accept() throws IOException {
        SocketChannel client = serverChannel.accept();
        if (client == null) {
            return;
        }

        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, new ClientState());
        ServerLogger.log("Подключился клиент: " + client.getRemoteAddress());
    }

    private void read(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        if (state == null) {
            throw new IllegalStateException("Отсутствует состояние клиента");
        }

        if (state.dataBuffer == null) {
            int bytesRead = channel.read(state.lengthBuffer);
            if (bytesRead == -1) {
                closeClient(key, "Клиент отключился");
                return;
            }
            if (state.lengthBuffer.hasRemaining()) {
                return;
            }

            state.lengthBuffer.flip();
            int payloadSize = state.lengthBuffer.getInt();
            state.lengthBuffer.clear();

            if (payloadSize <= 0 || payloadSize > MAX_MESSAGE_BYTES) {
                throw new IllegalStateException("Недопустимый размер приложенных данных: " + payloadSize);
            }

            state.dataBuffer = ByteBuffer.allocate(payloadSize);
        }

        int bytesRead = channel.read(state.dataBuffer);
        if (bytesRead == -1) {
            closeClient(key, "Клиент отключился");
            return;
        }

        if (state.dataBuffer.hasRemaining()) {
            return;
        }

        state.dataBuffer.flip();
        byte[] payload = new byte[state.dataBuffer.remaining()];
        state.dataBuffer.get(payload);
        state.dataBuffer = null;
        payloadQueue.offer(new IncomingRequest(channel, payload));
        ServerLogger.log("Получен набор байтов: " + payload);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        if (state == null) {
            throw new IllegalStateException("Отсутствует состояние клиента");
        }

        while (!state.pendingWrites.isEmpty()) {
            ByteBuffer current = state.pendingWrites.peek();
            channel.write(current);
            if (current.hasRemaining()) {
                return;
            }
            state.pendingWrites.poll();
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void closeClient(SelectionKey key, String reason) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ServerLogger.log("Отключение клиента: " + reason + " (" + channel.getRemoteAddress() + ")");
            key.cancel();
            channel.close();
        } catch (IOException ignored) {
            key.cancel();
        }
    }
}
