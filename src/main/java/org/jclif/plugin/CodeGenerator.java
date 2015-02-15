package org.jclif.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclif.runtime.Configuration;
import org.jclif.runtime.ExecutorHandler;
import org.jclif.util.StringUtil;

/**
 * 
 * Code gen
 * 1. properties gen
 * 
 * 
 * 2. main code generator
 * 
 * @author stephen
 *
 */
public class CodeGenerator {
	
	private static final Logger LOGGER = Logger.getLogger(CodeGenerator.class.getCanonicalName());
	
	private String appName;
	private String srcMainDir;
	
	public CodeGenerator(String appName, String srcMainDir) {
		this.appName = appName;
		this.srcMainDir = srcMainDir;
	}

	public String getAppName() {
		return appName;
	}
	
	public String getSourceMainDirectory() {
		return srcMainDir;
	}
	
	public void generateMain(String commandAnnotatedPackage, String targetDir) throws IOException {
		
		// always check if the target directory exists
		File targetDirectory = new File(targetDir);
		if(!targetDirectory.exists()) {
			throw new IllegalArgumentException("Target Directory " + targetDir + " does not exist");
		}
		
		// retrieve all the java files only
		File confSourceDir = new File(getSourceMainDirectory());
		if(!confSourceDir.exists()) {
			throw new IllegalArgumentException("Source Directory " + confSourceDir + " does not exist");
		}
		
		// scan all classes of a directory and detect which ones are JCLIF annotated
		File files[] = getClassSourceFiles(confSourceDir, commandAnnotatedPackage);
		Configuration handlerConfig = createHandlerConfiguration(files, commandAnnotatedPackage);
		
		// write to file
		File generatedFile = new File(targetDirectory, Configuration.DEFAULT_EXECUTOR_CONFIG_FILE);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(generatedFile)));
		handlerConfig.store(out, "JCLIF Generated file - " + Calendar.getInstance().getTime());
		
	}
	
	private Configuration createHandlerConfiguration(File[] files, String commandAnnotatedPackage) {
		Configuration handlerConfig = new Configuration(getAppName());
		for(File srcFile : files) {
			String className = commandAnnotatedPackage + "." + StringUtil.sourceNameToClassName(srcFile.getName());
			try {
				Class<?> classType = Class.forName(className);
				ExecutorHandler handler = new ExecutorHandler(classType);
				handlerConfig.addHandler(handler.getHandlerClass());
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.WARNING, String.format("Unable to load class %s due to error '%s'", className, e.getMessage()), e);
			} catch(Exception e) {
				LOGGER.log(Level.WARNING, String.format("Unable to load class %s due to error '%s'", className, e.getMessage()), e);
			}
		}
		return handlerConfig;
	}
	
	private File[] getClassSourceFiles(File confSourceDir, String commandAnnotatedPackage) throws IOException {
		
		File classSourceDir = new File(confSourceDir, commandAnnotatedPackage.replace(".", File.separator));
		File[] files = classSourceDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".java");
			}
		});
		
		if(files == null || 0 == files.length) {
			throw new IllegalArgumentException("No Java files found in directory " + classSourceDir.getCanonicalPath());
		}
		
		return files;
	}
	
}
