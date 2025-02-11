package net.etfbl.biblioteka.logger;

import net.etfbl.biblioteka.config.ConfigLoader;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class BibliotekaLogger {
    public static Logger logger = Logger.getLogger(BibliotekaLogger.class.getName());

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
