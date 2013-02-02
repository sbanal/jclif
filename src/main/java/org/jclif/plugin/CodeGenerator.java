package org.jclif.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Logger;

import org.jclif.runtime.Executor;
import org.jclif.runtime.ExecutorHandler;
import org.jclif.runtime.RuntimeConfiguration;
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
		
		String fileSeparator = System.getProperty("file.separator");
		
		// always check if the target directory exists
		File targetDirectory = new File(targetDir);
		if(!targetDirectory.exists()) {
			throw new IllegalArgumentException("Target Directory " + targetDir + " does not exist");
		}
		
		// retrieve all the java files only
		File srcMainDir = new File(getSourceMainDirectory());
		if(!srcMainDir.exists()) {
			throw new IllegalArgumentException("Source Directory " + srcMainDir + " does not exist");
		}
		File classSourceDir = new File(srcMainDir, commandAnnotatedPackage.replace(".", fileSeparator));
		File[] files = classSourceDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".java");
			}
		});
		if(files == null || 0 == files.length) {
			throw new IllegalArgumentException("No Java files found in directory " + classSourceDir.getCanonicalPath());
		}
		
		// scan all classes of a directory and detect which ones are JCLIF annotated
		RuntimeConfiguration jclifProperties = new RuntimeConfiguration(getAppName());
		for(File srcFile : files) {
			String className = StringUtil.sourceNameToClassName(srcFile.getName());
			try {
				Class<?> classType = Class.forName(commandAnnotatedPackage + "." + className);
				ExecutorHandler handler = Executor.annotationToMetadata(classType);
				if(handler!=null) {
					jclifProperties.addHandler(handler.getHandlerClass());
				}
			} catch (ClassNotFoundException e) {
				LOGGER.info(String.format("Unable to load class %s due to error '%s'", className, e.getMessage()));
			} catch(Exception e) {
				LOGGER.info(String.format("Unable to load class %s due to error '%s'", className, e.getMessage()));
			}
			
		}
		
		// write to file
		File generatedFile = new File(targetDirectory, Executor.DEFAULT_EXECUTOR_CONFIG_FILE);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(generatedFile)));
		jclifProperties.store(out, "JCLIF Generated file - " + Calendar.getInstance().getTime());
		
	}
	
}
