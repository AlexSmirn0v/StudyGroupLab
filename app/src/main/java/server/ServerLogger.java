package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс для логирования сообщений сервера.
 */
public class ServerLogger {
    static {
        String stdoutEncoding = System.getProperty("sun.stdout.encoding");
        if (stdoutEncoding == null || stdoutEncoding.isBlank()) {
            stdoutEncoding = System.getProperty("file.encoding", "UTF-8");
        }
        System.setProperty("LOG_CHARSET", stdoutEncoding);
    }
    private static final Logger LOGGER = LogManager.getLogger(ServerLogger.class);

    /**
     * Логирует сообщение.
     * @param line сообщение для логирования
     */
    public static void log(String line) {
        LOGGER.info(line);
    }
}
