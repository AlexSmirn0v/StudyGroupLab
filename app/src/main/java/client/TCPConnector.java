package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import model.CommandMessage;

public class TCPConnector implements AutoCloseable{
    private SocketChannel channel;

    TCPConnector(int port) throws IOException{
        InetSocketAddress addr = new InetSocketAddress("localhost", port);

        for (int i = 1; i <= 5; i++) {
            try {
                channel = SocketChannel.open();
                channel.connect(addr);
                return;
            } catch (IOException e) {
                System.out.println("Сервер недоступен, переподключаюсь, попытка " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Подключение прервано", e2);
                }
            }
        }
        throw new IOException("Попытка не удалась");
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

        channel.write(buff);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
