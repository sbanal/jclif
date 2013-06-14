/** 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclif.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclif.annotation.Command;
import org.jclif.parser.CommandLineParseResult;
import org.jclif.parser.CommandLineParser;
import org.jclif.parser.InvalidInputException;
import org.jclif.text.CommandLineFormat;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandLineProperties;
import org.jclif.type.OptionMetadata;
import org.jclif.type.ParameterMetadata;
import org.jclif.util.LoggerUtil;


/**
 * This class execute command handlers registered to its registry or . This class is registered
 * as the main class in a manifest file of an application to support JCLIF annotations
 * in their code.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public final class Executor {

	private static final Logger LOGGER = Logger.getLogger(Executor.class.getCanonicalName());
	
	private Configuration appConfig = new Configuration();
	private CommandLineConfiguration config = new CommandLineConfiguration();
	private ExecutorHandlerRegistry handlerRegistry = new ExecutorHandlerRegistry();
	private InputStream configurationStream;
	private PrintStream outputStream;
	
	/**
	 * Creates an Executor instance which uses a custom input stream of configuration properties
	 * and output stream of command line usage text.
	 *  
	 * @param configurationStream	input stream where the configurations are read
	 * @param outputStream			output stream where the help text are written
	 */
	public Executor(InputStream configurationStream, PrintStream outputStream) {
		this.configurationStream = configurationStream;
		this.outputStream = outputStream;
	}
	
	/**
	 * Creates an Executor instance which uses a specified output stream where the command line
	 * usage text are written.
	 * 
	 * @param outputStream	output stream where the help text are written
	 * @throws IOException	thrown if loading of default configuration stream fails 
	 */
	public Executor(PrintStream outputStream) throws IOException {
		this.outputStream = outputStream;
		this.configurationStream = Configuration.getDefaultConfigInputStream();
	}
	
	/**
	 * Returns the comman line configuration of this executor.
	 * 
	 * @return	CommandLineConfiguration
	 */
	public CommandLineConfiguration getConfig() {
		return config;
	}
	
	/**
	 * Loads command line handlers from configuration input stream and registers the handlers
	 * to the command line configuration.
	 * 
	 * @throws IOException thrown if an error occurs while loading the configuration properties 
	 */
	public void loadHandlers() throws IOException {
		
		if(configurationStream==null) {
			appConfig.load();
		} else {
			appConfig.load(configurationStream);
		}
		
		Set<Class<?>> handlerClassList = new HashSet<Class<?>>();
		handlerClassList.addAll(appConfig.getPackageHandlerList());
		handlerClassList.addAll(appConfig.getHandlerList());
		processHandlerAnnotations(handlerClassList);
		
		LOGGER.info("Handler class list: " + handlerClassList);
		
	}
	
	void processHandlerAnnotations(Set<Class<?>> classList) {
		for(Class<?> classInstance : classList) {
			try {
				registerHandler(classInstance);
			} catch(IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, "Handler " + classInstance.getCanonicalName() 
						+ " is not a valid handler, skipping.", e);
			}
		}
	}
	
	/**
	 * Registers a class as a command line handler.
	 * 
	 * @param handlerClass command line handler
	 * @throws IllegalArgumentException thrown if handler class is not a valid Command handler
	 */
	public void registerHandler(Class<?> handlerClass) {
		
		ExecutorHandler handler = AnnotationProcessor.createExecutorHandler(handlerClass);
		
		LOGGER.log(Level.INFO, "Adding class handler " + handlerClass.getCanonicalName());
		
		if(handler.getMetadata().getIdentifier().equals(Command.DEFAULT_COMMAND_IDENTIFIER)) {
			for(OptionMetadata optMeta: handler.getMetadata().getOptionConfigurations().values()) {
				config.getOptionConfiguration().addOption(optMeta);
			}
			for(ParameterMetadata paramMeta: handler.getMetadata().getParameterConfigurations().values()) {
				config.getParameterConfiguration().addParameter(paramMeta);
			}
		} else {
			config.getCommandConfiguration().addCommand(handler.getMetadata());
		}
		
		handlerRegistry.add(handler);
	}
	
	/**
	 * Sets operating system name of command line properties to use.
	 * Operating system name value can be any of the System.getProperty("os.name") values
	 * returned by JVM.
	 * 
	 * @param osName OS Name
	 */
	public void setOperatingSystem(String osName) {
		config.setCommandLineProperties(CommandLineProperties.getSystemProperties(osName));
	}
	
	/**
	 * Executes a command line handler which matches the command line arguments and if 
	 * a parsing error occurs prints the usage message.
	 * 
	 * @param args	command line arguments
	 */
	public void execute(String... args) {
		
		try {
			
			CommandLineParseResult result = CommandLineParser.getInstance().parse(config, args);
			
			LOGGER.info("Command match: " + result.isCommandMatch() 
					+ ",command=" + result.getMatchingCommand());
			
			ExecutorHandler handler = null;
			if(result.isCommandMatch()) {
				handler = handlerRegistry.getHandler(result.getMatchingCommand().getMetadata());
			} else {
				handler = handlerRegistry.getDefaultHandler();
			}
			
			if(handler!=null) {
				handler.execute(result);
			} else {
				printUsage( (InvalidInputException) null );
			}
			
		} catch (InvalidInputException e) {
			printUsage(e);
		} catch (Exception e) {
			printUsage("Unknwon exception. " + e.getMessage() 
					+ ". Cause = " + ((e.getCause()!=null)?e.getCause().getMessage():""));
		}
		
	}
	
	void printUsage(InvalidInputException e) {
		if(null==e) {
			outputStream.println(CommandLineFormat.getInstance().format(config));
		} else {
			outputStream.println(CommandLineFormat.getInstance().format(config, e));
		}
	}
	
	void printUsage(String error) {
		String usage = CommandLineFormat.getInstance().format(config, error);
		outputStream.println(usage);
	}
	
	/**
	 * Main method which calls the runtime annotation detection tool and runs the 
	 * parser and help text formatter automatically.
	 * 
	 * @param args	argument passed in command line input
	 */
	public static void main(String[] args) {
		
		try {
			
			LoggerUtil.initializeLogger();
			
			String osName = System.getProperty(Configuration.PROPERTY_JCLIF_OS_NAME, 
					System.getProperty("os.name"));
			
			Executor executor = new Executor(System.out);
			executor.setOperatingSystem(osName);
			executor.loadHandlers();
			executor.execute(args);
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
	}
	
}
