package org.jclif.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.jclif.runtime.Executor;

public class LoggerUtil {

	public static void initializeLogger() {
		
		try {
			if(System.getProperty("java.util.logging.config.file")!=null) {
				System.out.println("Using logging property file " + System.getProperty("java.util.logging.config.file"));
				return;
			}
			InputStream loggingStream = Executor.class.getClassLoader().getResourceAsStream("logging.properties");
			if(loggingStream==null) {
				return;
			}
			LogManager.getLogManager().readConfiguration(loggingStream);
		} catch (SecurityException e) {
			e.printStackTrace();
			System.err.println("Error loading logging.prroperties from class loader. Error:" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error loading logging.prroperties from class loader. Error:" + e.getMessage());
		}
		
	}
	
}
