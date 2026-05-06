package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс для логирования сообщений сервера.
 */
public class ServerLogger {
    private static final Logger LOGGER = LogManager.getLogger(ServerLogger.class);

    /**
     * Логирует сообщение.
     * @param line сообщение для логирования
     */
    public static void log(String line) {
        LOGGER.info(line);
    }
}
