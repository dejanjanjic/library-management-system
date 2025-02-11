package net.etfbl.logger;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import net.etfbl.config.ConfigLoader;

public class ServerLogger {
	public static Logger logger = Logger.getLogger(ServerLogger.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler(ConfigLoader.getInstance().getProperty("log.file"), true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            logger.severe("Failed to initialize logger handler.");
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
