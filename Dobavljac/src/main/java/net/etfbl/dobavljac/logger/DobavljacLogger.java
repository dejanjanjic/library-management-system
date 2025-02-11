package net.etfbl.dobavljac.logger;

import net.etfbl.dobavljac.config.ConfigLoader;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class DobavljacLogger {
    public static Logger logger = Logger.getLogger(DobavljacLogger.class.getName());

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
