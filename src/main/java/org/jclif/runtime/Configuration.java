package org.jclif.runtime;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.logging.Logger;

import org.jclif.util.StringUtil;

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
	
	public static final String DEFAULT_EXECUTOR_CONFIG_FILE = "jclif.properties";
	public static final String PROPERTY_JCLIF_INSTALL_PATH = "org.jclif.app.installation.path";
	public static final String PROPERTY_JCLIF_CONFIG_FILE = "org.jclif.app.configuration.file";
	public static final String PROPERTY_JCLIF_OS_NAME = "org.jclif.runtime.system.os.name";
	
	public static final String CONFIG_PROPERTY_APP_NAME = "org.jclif.app.name";
	public static final String CONFIG_PROPERTY_APP_MAIN = "org.jclif.app.main";
	public static final String CONFIG_PROPERTY_APP_HANDLER_LIST = "org.jclif.app.handler";
	public static final String CONFIG_PROPERTY_APP_HANDLER_PACKAGE = "org.jclif.app.handler.package";
	
	private int handlerCount = 1;
	
	/**
	 * Creates an empty configuration.
	 */
	public Configuration() {
		
	}
	
	/**
	 * Creates a configuration based on an existing properties.
	 *  
	 * @param prop	Properties to copy
	 */
	public Configuration(Properties prop) {
		super(prop);
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
	 * @param  inputStream  stream of propertie file to read
	 * @throws IOException	thrown if an I/O error occurs while reading the configuration stream
	 */
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
		put(CONFIG_PROPERTY_APP_HANDLER_LIST + "." + (handlerCount++), handler.getCanonicalName());
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
		for(int i = 1; ; i++) {
			String className = getProperty(CONFIG_PROPERTY_APP_HANDLER_LIST + "." + i, "").trim();
			if(className==null || className.isEmpty()) {
				break;
			}
			try {
				handlerList.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				// skip this class
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
		
		URL url = null;
		String appMainPackage = null;
		String appPackage = getHandlerPackage();
		if(appPackage!=null) {
			appMainPackage = appPackage.replace(".", "/");
			url = Executor.class.getClassLoader().getResource(DEFAULT_EXECUTOR_CONFIG_FILE);
		} else {
			return new ArrayList<Class<?>>();
		}
		
		LOGGER.info("Loading Main handler: " + appMainPackage);
		LOGGER.info("Loading Main handler url path " + url);
		
		if(!appMainPackage.isEmpty() && url==null) {
			throw new IllegalArgumentException("Handler package is set but no " 
					+ DEFAULT_EXECUTOR_CONFIG_FILE + " is found in classpath.");
		}
		
		Set<Class<?>> packageHandlerList = new HashSet<Class<?>>();
			
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
				if(entry.getName().startsWith(appMainPackage) && !entry.isDirectory() && entry.getName().endsWith(".class")) {
					
					try {
						Class<?> classType = Class.forName(StringUtil.pathToClassName(entry.getName()));
						packageHandlerList.add(classType);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
					
				}
			}
			
		} else if(url.getProtocol().equalsIgnoreCase("file")){
			try {
				File fileDir = new File(url.toURI());
				LOGGER.info("Loading Handler from local path: " + fileDir.getCanonicalFile().getParentFile());
				extractClassPackages(fileDir.getCanonicalFile().getParentFile(), 
						appMainPackage, packageHandlerList);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
			
		if(packageHandlerList.isEmpty()) {
			throw new IllegalArgumentException("Handler package not configured or no "
					+ Configuration.CONFIG_PROPERTY_APP_HANDLER_LIST 
					+ ".<X> entry found. Please check configuration file.");
		}
		
		return new ArrayList<Class<?>>(packageHandlerList);
	}
	
	private static final FileFilter classFilter = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(".class");
		}
	};
	
	void extractClassPackages(File srcDir, String appMainPackage, 
			Collection<Class<?>> packageClasses) {
		
		File[] files = srcDir.listFiles(classFilter);
		
		for(File file : files) {
			
			String entryFilePath;
			try {
				entryFilePath = file.getCanonicalPath();
			} catch (IOException e1) {
				continue;
			}
			
			if(file.isDirectory()) {
				extractClassPackages(file, appMainPackage, packageClasses);
			} else {
				if(entryFilePath.endsWith(appMainPackage+ "/" +  file.getName())
					&& !file.isDirectory() 
					&& file.getName().endsWith(".class")) {
					
					try {
						Class<?> classType = Class.forName(StringUtil.pathToClassName(
								appMainPackage + "/" + file.getName()));
						packageClasses.add(classType);	
					} catch (ClassNotFoundException e) {
						// Skipping those classes which cannot be found in classpath!
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
