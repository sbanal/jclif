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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jclif.annotation.Command;
import org.jclif.annotation.Handler;
import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.parser.CommandLineParser;
import org.jclif.parser.CommandLineParseResult;
import org.jclif.parser.InvalidInputException;
import org.jclif.runtime.ExecutorHandlerRegistry.ExecutorHandler;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandMetadata;
import org.jclif.type.CommandMetadataImpl;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionMetadata;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterMetadataImpl;
import org.jclif.type.ParameterType;
import org.jclif.util.StringUtil;

/**
 * This class is the main class used by applications which uses annotations. This class is registered
 * as the main class in its manifest files of applications to support JCLIF annotations
 * in their code.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public final class Executor {

	private static final Logger LOGGER = Logger.getLogger(Executor.class.getCanonicalName());
	
	public static final String DEFAULT_EXECUTOR_CONFIG_FILE = "jclif.properties";
	public static final String PROPERTY_JCLIF_INSTALL_PATH  = "org.jclif.app.installation.path";
	public static final String PROPERTY_JCLIF_CONFIG_FILE   = "org.jclif.app.configuration.file";
	
	public static final String CONFIG_PROPERTY_APP_NAME = "org.jclif.app.name";
	public static final String CONFIG_PROPERTY_APP_MAIN = "org.jclif.app.main";
	public static final String CONFIG_PROPERTY_APP_HANDLER_LIST = "org.jclif.app.handler";
	public static final String CONFIG_PROPERTY_APP_HANDLER_PACKAGE = "org.jclif.app.handler.package";
	
	private Properties appConfig = new Properties();
	private CommandLineConfiguration config = new CommandLineConfiguration();
	private ExecutorHandlerRegistry handlerRegistry = new ExecutorHandlerRegistry();
	
	private Executor() throws IOException {
		appConfig.load(getConfigInputStream());
	}
	
	private InputStream getConfigInputStream() throws IOException {
		InputStream stream = Executor.class.getClassLoader().getResourceAsStream(DEFAULT_EXECUTOR_CONFIG_FILE);
		if(stream==null) {
			String configFileName = System.getProperty(PROPERTY_JCLIF_CONFIG_FILE, DEFAULT_EXECUTOR_CONFIG_FILE);
			String configDirPath = System.getProperty(PROPERTY_JCLIF_INSTALL_PATH, System.getProperty("user.dir"));
			File configFile = new File(new File(configDirPath).getCanonicalFile(), configFileName);
			stream = new FileInputStream(configFile);
			LOGGER.info(String.format("Executor using file config file '%s'", configFile.getCanonicalPath()));
		}  else {
			LOGGER.info(String.format("Executor using resource config file '%s'", DEFAULT_EXECUTOR_CONFIG_FILE));
		}
		return stream;
	}
	
	public CommandLineConfiguration getConfig() {
		return config;
	}

	List<String> loadHandlerResources() throws Exception {
		
		String appName = appConfig.getProperty(CONFIG_PROPERTY_APP_NAME, "appname");
		config.setName(appName);
		
		URL url = null;
		String appMainPackage = null;
		String appMain = appConfig.getProperty(CONFIG_PROPERTY_APP_MAIN);
		if(appMain!=null) {
			Class<?> appMainClass = Class.forName(appMain);
			appMainPackage = appMainClass.getPackage().getName().replace(".", "/");
			String appJarEntryMainClass = appMain.replace(".", "/") + ".class";
			url = Executor.class.getClassLoader().getResource(appJarEntryMainClass);
		} else {
			String appPackage = appConfig.getProperty(CONFIG_PROPERTY_APP_HANDLER_PACKAGE,"");
			appMainPackage = appPackage.replace(".", "/");
			url = Executor.class.getClassLoader().getResource("jclif.properties");
			LOGGER.info("Loading Main handler url path " + url.getPath());
		}

		if(appMainPackage==null) {
			throw new Exception("Handler package not configured. Please check configuration file.");
		}
		
		LOGGER.info("Loading Main handler: " + appMainPackage);
		
		List<String> mainPackageClasses = new ArrayList<String>();
		URLConnection urlConn = url.openConnection();
		if (urlConn instanceof JarURLConnection){
			JarURLConnection conn = (JarURLConnection) urlConn;
			LOGGER.info("Loading Handler from jar: " + conn.getJarFile().getName());
			for(Enumeration<JarEntry> e = conn.getJarFile().entries(); e.hasMoreElements(); ) {
				JarEntry entry = e.nextElement();
				if(entry==null) {
					continue;
				}
				LOGGER.info("Checking Handler from jar path: " + entry.getName());
				if(entry.getName().startsWith(appMainPackage)
					&& !entry.isDirectory()
					&& entry.getName().endsWith(".class")) {
					mainPackageClasses.add(StringUtil.pathToClassName(entry.getName()));
				}
			}
		} else if(url.getProtocol().equalsIgnoreCase("file")){
			File fileDir = new File(url.toURI());
			LOGGER.info("Loading Handler from local path: " + fileDir.getCanonicalFile().getParentFile());
			extractClassPackages(fileDir.getCanonicalFile().getParentFile(), appMainPackage, mainPackageClasses);
		}
		
		// Add addition handler specified in configuration file
		for(int i = 1; ;i++) {
			String key = CONFIG_PROPERTY_APP_HANDLER_LIST + "." + i;
			String configHandler = this.appConfig.getProperty(key);
			if(configHandler==null) {
				break;
			}
			if(configHandler.isEmpty()) {
				continue;
			}
			configHandler = configHandler.trim();
			LOGGER.info("Loading Handler from config " + key + "='" + configHandler + "'");
			mainPackageClasses.add(configHandler);
		}

		LOGGER.info("Handler classe list: " + mainPackageClasses);

		return mainPackageClasses;
	}
	
	private static final FileFilter classFilter = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(".class");
		}
	};
	
	void extractClassPackages(File srcDir, String appMainPackage, List<String> mainPackageClasses) throws IOException {
		File[] files = srcDir.listFiles(classFilter);
		for(File file : files) {
			String entryFilePath = file.getCanonicalPath();
			LOGGER.info("Checking Handler from local path: " + entryFilePath);
			if(file.isDirectory()) {
				extractClassPackages(file, appMainPackage, mainPackageClasses);
			} else {
				if(entryFilePath.endsWith(appMainPackage+ "/" +  file.getName())
					&& !file.isDirectory() 
					&& file.getName().endsWith(".class")) {
					LOGGER.info("Adding Handler from local path: " + entryFilePath);
					mainPackageClasses.add(StringUtil.pathToClassName(appMainPackage + "/" + file.getName()));	
				}
			}
		}
	}
	
	void processConfigAnnotations(List<String> classList) {
	
		for(String className : classList) {
			try {
				Class<?> classInstance = Class.forName(className);
				CommandMetadata metadata = annotationToMetadata(classInstance);
				if(metadata==null) {
					continue;
				}
				if(metadata.getIdentifier().equals("-default-")) {
					for(OptionMetadata optMeta: metadata.getOptionConfigurations().values()) {
						config.getOptionConfiguration().addOption(optMeta);
					}
					for(ParameterMetadata paramMeta: metadata.getParameterConfigurations().values()) {
						config.getParameterConfiguration().addParameter(paramMeta);
					}
				} else {
					config.getCommandConfiguration().addCommand(metadata);
				}
				
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.WARNING, "Application configuration error. Handler class " + className + " not found", e);
			}
		}
		
	}
	
	CommandMetadata annotationToMetadata(Class<?> commandHandler)
	{
		LOGGER.info("--- Start Processing annotation details: " + commandHandler.getCanonicalName());
		
		// extract class annotation of command
		Command commandAnnotation = commandHandler.getAnnotation(Command.class);
		if(commandAnnotation==null) {
			LOGGER.log(Level.WARNING,"Unable to extract annotation from class" + commandHandler + ". Skipping processing.");
			return null;
		}
		
		// extract executor method
		Method handlerMethod = null;
		for(Method method : commandHandler.getMethods()) {
			if(!Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 0) {
				Handler annotation = method.getAnnotation(Handler.class);
				if(annotation!=null) {
					handlerMethod = method;
					break;
				}
			}
		}
		
		if(handlerMethod == null) {
			LOGGER.log(Level.WARNING,"Command handler annotation not found in " + commandHandler.getCanonicalName() + ". Skipping processing.");
			return null;
		}
		
		// extract option annotations of command
		OptionConfiguration optionConfig = new OptionConfiguration();
		ParameterConfiguration parameterConfig = new ParameterConfiguration();
		for(Field field : commandHandler.getDeclaredFields()) {
			if(field.isAnnotationPresent(Option.class)) {
				Option optionAnnotation = field.getAnnotation(Option.class);
				if(optionAnnotation==null) {
					LOGGER.info("skipping field " + field.getName() + ", no annotation found.");
					continue;
				}
				
				LOGGER.info("Option annotation[" + optionAnnotation + "] ");
				
				ParameterType paramType = optionAnnotation.type();
				if(paramType==null) {
					paramType = ParameterType.toParamType(field.getType());
				}
				
				LOGGER.info("Option param type = [" + paramType + "] ");
				
				boolean multiValued = field.getType() == List.class;
				optionConfig.addOption(
						optionAnnotation.identifier(),
						optionAnnotation.longIdentifier(),
						paramType,
						optionAnnotation.required(),
						multiValued,
						optionAnnotation.description(),
						optionAnnotation.longDescription());
			} else if(field.isAnnotationPresent(Parameter.class)) {
				Parameter parameterAnnotation = field.getAnnotation(Parameter.class);
				if(parameterAnnotation!=null) {
					ParameterMetadata parameterMetadata = new ParameterMetadataImpl(
							parameterAnnotation.identifier(),
							parameterAnnotation.required(),
							parameterAnnotation.multiValued(),
							parameterAnnotation.type(),
							parameterAnnotation.description(),
							parameterAnnotation.longDescription());
					parameterConfig.add(parameterMetadata);
				}
			}
		}
		
		
		LOGGER.info("Adding handler Command[command=" + commandAnnotation.identifier() + ", desc=" + commandAnnotation.description() + "]");
		
		LOGGER.info("--- End Processing annotation details: " + commandHandler.getCanonicalName());
		
		try {
			CommandMetadata metadata = new CommandMetadataImpl(commandAnnotation.identifier(), 
					optionConfig, 
					parameterConfig, 
					commandAnnotation.description(), 
					commandAnnotation.longDescription());
			handlerRegistry.add(metadata, commandHandler, handlerMethod);
			return metadata;
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to register command metadata to registry", e);
		}
		
		
	}
	
	public void execute(String... args) throws Exception {
		List<String> handlerList = loadHandlerResources();
		processConfigAnnotations(handlerList);
		CommandLineParseResult result = CommandLineParser.getInstance().parse(config, args);
		LOGGER.info("Command match: " + result.isCommandMatch() + ",command=" + result.getMatchingCommand());
		ExecutorHandler handler = null;
		if(result.isCommandMatch()) {
			handler = handlerRegistry.getHandler(result.getMatchingCommand().getMetadata());
		} else {
			handler = handlerRegistry.getDefaultHandler();
		}
		if(handler==null) {
			throw new IllegalArgumentException("Executor not found for command " + result.getMatchingCommand().getIdentifier());
		}
		handler.execute(result);
	}
	
	public void printUsage(InvalidInputException e) {
		String usage = CommandLineParser.getInstance().format(config, e);
		System.out.println(usage);
	}
	
	public void printUsage(Exception e) {
		String usage = CommandLineParser.getInstance().format(config, e.getMessage());
		System.out.println(usage);
	}
	
	public void printUsage(String error) {
		String usage = CommandLineParser.getInstance().format(config, error);
		System.out.println(usage);
	}
	
	static void configureLoggingProperties() {
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
	
	/**
	 * Main method which calls the runtime annotation detection tool and runs the parser and help text formatter
	 * automatically.
	 * 
	 * @param args	argument passed in command line input
	 */
	public static void main(String[] args) {
		
		configureLoggingProperties();
		
		Executor executor = null;
		
		try {
			executor = new Executor();
			executor.execute(args);
		} catch (InvalidInputException e) {
			executor.printUsage(e);
		} catch (FileNotFoundException e) {
			executor.printUsage("Unable to find JCLIF configuration. " + e.getMessage());
		} catch (IOException e) {
			executor.printUsage("Unable to read JCLIF configuration. " + e.getMessage());
		} catch (Exception e) {
			executor.printUsage("Unknwon excetpion. " + e.getMessage() + ". Cause = " + ((e.getCause()!=null)?e.getCause().getMessage():""));
		}
		
	}
	
}
