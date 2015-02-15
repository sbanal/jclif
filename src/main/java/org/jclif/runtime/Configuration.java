package org.jclif.runtime;

import org.jclif.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a configuration used by the executor when processing command line
 * inputs. A Configuration contains the properties which defines the package handler name, 
 * command handler list and application name.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class Configuration extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5801691693374599163L;

	private static final Logger LOGGER = Logger.getLogger(Configuration.class.getCanonicalName());
	private static final int MAX_HANDLERS = Short.MAX_VALUE;
	
	public static final String DEFAULT_EXECUTOR_CONFIG_FILE = "jclif.properties";
	public static final String PROPERTY_JCLIF_INSTALL_PATH = "org.jclif.app.installation.path";
	public static final String PROPERTY_JCLIF_CONFIG_FILE = "org.jclif.app.configuration.file";
	public static final String PROPERTY_JCLIF_OS_NAME = "org.jclif.runtime.system.os.name";
	
	public static final String CONFIG_PROPERTY_APP_NAME = "org.jclif.app.name";
	public static final String CONFIG_PROPERTY_APP_MAIN = "org.jclif.app.main";
	public static final String CONFIG_PROPERTY_APP_HANDLER_LIST = "org.jclif.app.handler";
	public static final String CONFIG_PROPERTY_APP_HANDLER_PACKAGE = "org.jclif.app.handler.package";
	
	private int handlerCount = 0;
	
	/**
	 * Creates an empty configuration.
	 */
	public Configuration() {
		
	}
	
	/**
	 * Creates an empty configuration with application name set to specified name parameter.
	 * 
	 * @param name	name of application
	 */
	public Configuration(String name) {
		put(CONFIG_PROPERTY_APP_NAME, name);
	}
	
	/**
	 * Loads configuration properties from default configuration input stream and 
	 * loads handler list.
	 * 
	 * @throws IOException	thrown if an I/O error occurs while reading the configuration stream
	 */
	public void load() throws IOException {
		this.load(getDefaultConfigInputStream());
	}
	
	/**
	 * Loads configuration properties from default configuration input stream and 
	 * loads handler list.
	 * 
	 * @param  inputStream  stream of properties file to read
	 * @throws IOException	thrown if an I/O error occurs while reading the configuration stream
	 */
	@Override
	public void load(InputStream inputStream) throws IOException {
		super.load(inputStream);
		getHandlerList();
	}
	
	/**
	 * Returns input stream of default JCLIF configuration file located in class path.
	 * 
	 * @return InputStream	input stream of default configuration file
	 * @throws IOException  throw if any error occurs while creating input stream of file
	 */
	public static InputStream getDefaultConfigInputStream() throws IOException {
		InputStream stream = Executor.class.getClassLoader().getResourceAsStream(DEFAULT_EXECUTOR_CONFIG_FILE);
		if(stream==null) {
			String configFileName = System.getProperty(PROPERTY_JCLIF_CONFIG_FILE, DEFAULT_EXECUTOR_CONFIG_FILE);
			String configDirPath = System.getProperty(PROPERTY_JCLIF_INSTALL_PATH, System.getProperty("user.dir"));
			File configFile = new File(new File(configDirPath).getCanonicalFile(), configFileName);
			stream = new FileInputStream(configFile);
			LOGGER.info(String.format("Executor using file config file '%s'", configFile.getCanonicalPath()));
		} else {
			LOGGER.info(String.format("Executor using resource config file '%s'", DEFAULT_EXECUTOR_CONFIG_FILE));
		}
		return stream;
	}
	
	public void setHandlerPackage(String packageName) {
		put(CONFIG_PROPERTY_APP_HANDLER_PACKAGE, packageName);
	}
	
	public void addHandler(Class<?> handler) {
		put(CONFIG_PROPERTY_APP_HANDLER_LIST + "." + (++handlerCount), handler.getCanonicalName());
	}
	
	/**
	 * Retrieves command handlers registered in the properties file. Returned list contains
	 * all classes which are registered under properties org.jclif.app.handler.[index]
	 * in properties file.
	 * 
	 * @return List<Class<?>> list of handler classes
	 */
	public List<Class<?>> getHandlerList() {
		List<Class<?>> handlerList = new ArrayList<Class<?>>();
		for(int i = 1; i < MAX_HANDLERS; i++) {
			String className = getProperty(CONFIG_PROPERTY_APP_HANDLER_LIST + "." + i, "").trim();
			if(className==null || className.isEmpty()) {
				break;
			}
			try {
				handlerList.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.WARNING, "Handler class " + className + " not found in classpath", 
						e);
			}
		}
		this.handlerCount = handlerList.size();
		return handlerList;
	}
	
	/**
	 * Retrieves command handler list detected in package name specified under 
	 * org.jclif.app.handler.package property of properties file. 
	 * 
	 * @return List<Class<?>> list of handler classes
	 * @throws IOException 
	 */
	public List<Class<?>> getPackageHandlerList() throws IOException {

		String appPackage = getHandlerPackage();
		if(appPackage==null || appPackage.trim().isEmpty()) {
            return new ArrayList<Class<?>>();
        }

        String appMainPackage = appPackage.trim().replace(".", "/");
        URL url = Executor.class.getClassLoader().getResource(DEFAULT_EXECUTOR_CONFIG_FILE);
        if(!appMainPackage.isEmpty() && url==null) {
            throw new IllegalArgumentException("Handler package is set but no "
                    + DEFAULT_EXECUTOR_CONFIG_FILE + " is found in classpath.");
        }

		LOGGER.info("Loading Main handler '" + appMainPackage + "' from config " + url);

        try {

            List<Class<?>> packageHandlerList = new ArrayList<Class<?>>();
            URLConnection urlConn = url.openConnection();

            if (urlConn instanceof JarURLConnection){
                extractJarHandlerList((JarURLConnection) urlConn, appMainPackage, packageHandlerList);
            } else if("file".equalsIgnoreCase(url.getProtocol())){
                File srcDir = new File(url.toURI()).getCanonicalFile().getParentFile();
                LOGGER.info("Loading Handler from local path: " + srcDir);
                extractClassPackages(srcDir, appMainPackage, packageHandlerList);
            }

            if(packageHandlerList.isEmpty()) {
                throw new IllegalArgumentException("Handler package not configured or no "
                        + Configuration.CONFIG_PROPERTY_APP_HANDLER_LIST
                        + ".<X> entry found. Please check configuration file.");
            }

            return packageHandlerList;

        } catch (URISyntaxException e) {
            throw new IOException("Unable to load handlers from class packages due to URI error. " +
                    "Source url " + url, e);
        }

	}

    void extractJarHandlerList(JarURLConnection conn, String appMainPackage, List<Class<?>> packageHandlerList)
            throws IOException {

        LOGGER.info("Loading Handler from jar: " + conn.getJarFile().getName());
		for(Enumeration<JarEntry> e = conn.getJarFile().entries(); e.hasMoreElements(); ) {
			JarEntry entry = e.nextElement();
			if(entry==null) {
				continue;
			}
			LOGGER.info("Checking Handler from jar path: " + entry.getName());
			if(entry.getName().startsWith(appMainPackage) && !entry.isDirectory() && entry.getName().endsWith(".class")) {
				
				try {
					Class<?> classType = Class.forName(StringUtil.pathToClassName(entry.getName()));
					packageHandlerList.add(classType);
				} catch (ClassNotFoundException ex) {
					LOGGER.log(Level.WARNING, "Loading Handler from jar path " + conn.getURL(), ex);
				}
			}
		}

	}

    void extractClassPackages(File srcDir, String appMainPackage, List<Class<?>> packageClasses) throws IOException {

		File packageDir = new File(srcDir, appMainPackage);
		File[] files =  packageDir.listFiles();
		LOGGER.fine("extractClassPackages files: " + files);

		for(File file : files) {
			
			String entryFilePath;
			try {
				entryFilePath = file.getCanonicalPath();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "extractClassPackages failed for file " + file, e);
				continue;
			}
			
			if(file.isDirectory()) {
				extractClassPackages(file, appMainPackage, packageClasses);
			} else {
				if(entryFilePath.endsWith(appMainPackage + File.separator +  file.getName())
					&& !file.isDirectory() 
					&& file.getName().endsWith(".class")) {
					
					try {
						Class<?> classType = Class.forName(StringUtil.pathToClassName(
								appMainPackage + "." + file.getName()));
						packageClasses.add(classType);	
					} catch (ClassNotFoundException e) {
						// Skipping those classes which cannot be found in classpath!
						LOGGER.log(Level.WARNING, "Loading Handler from local path failed for " 
								+ file, e);
					}
				}
			}
			
		}

	}
	
	public int getHandlerCount() {
		return handlerCount;
	}
	
	public String getName() {
		return (String) get(CONFIG_PROPERTY_APP_NAME);
	}
	
	public String getHandlerPackage() {
		return (String) get(CONFIG_PROPERTY_APP_HANDLER_PACKAGE);
	}

}
