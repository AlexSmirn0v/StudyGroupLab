package client;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Scanner;

public class IOHandler implements AutoCloseable {
    private Scanner scanner;
    private Charset consoleCharset;

    public IOHandler() {
        consoleCharset = (System.console() != null)
                ? System.console().charset()
                : Charset.defaultCharset();
        scanner = new Scanner(System.in, consoleCharset);
    }

    public void print(Object line) {
        System.out.print(line.toString());
    }

    public void println(Object line) {
        print(line);
        print("\n");
    }

    public void changeSource(InputStream newSource) {
        if (newSource instanceof BufferedInputStream) {
            close();
        }
        scanner = new Scanner(newSource, consoleCharset);
    }

    /**
     * Выводит в консоль клиента ответ в оптимальном форматировании.
     * 
     * @param response ответ для вывода
     */
    public void printResponse(Object response) {
        if (response == null) {
            println("Сервер вернул пустой ответ.");
            return;
        }

        if (response instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                println("Коллекция пуста.");
                return;
            }
            for (Object item : collection) {
                println(item);
                println("");
            }
            return;
        }

        println(response);
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    @Override
    public void close() {
        if (scanner != null)
            scanner.close();
    }
}
