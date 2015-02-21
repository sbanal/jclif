package org.jclif.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggerUtil {

  private LoggerUtil() {
    // util class
  }

  public static void initializeLogger(Logger logger) {

    try {
      if (System.getProperty("java.util.logging.config.file") != null) {
        logger.info("Using logging property file "
            + System.getProperty("java.util.logging.config.file"));
        return;
      }
      InputStream loggingStream =
          LoggerUtil.class.getClassLoader().getResourceAsStream("logging.properties");
      if (loggingStream == null) {
        return;
      }
      LogManager.getLogManager().readConfiguration(loggingStream);
    } catch (SecurityException e) {
      logger.log(Level.SEVERE, "Error loading logging.prroperties from class loader.", e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error loading logging.prroperties from class loader.", e);
    }

  }

}
