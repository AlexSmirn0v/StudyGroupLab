package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLogger {
    private static final Logger LOGGER = LogManager.getLogger(ServerLogger.class);

    static void log(String line) {
        LOGGER.info(line);
    }
}
