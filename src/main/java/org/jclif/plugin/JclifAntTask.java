package org.jclif.plugin;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class JclifAntTask extends Task {

	private String applicationName;
	private String sourceDirectory;
	private String annotatedPackage;
	private String buildDirectory;
	
	@Override
	public void execute() {
		try {
			CodeGenerator codeGen = new CodeGenerator(this.getApplicationName(), this.getSourceDirectory());
			codeGen.generateMain(this.getAnnotatedPackage(), this.getBuildDirectory());
		} catch (IOException e) {
			throw new BuildException("JCLIF configuration generator failed", e);
		}
	}
	
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String getAnnotatedPackage() {
		return annotatedPackage;
	}

	public void setAnnotatedPackage(String annotatedPackage) {
		this.annotatedPackage = annotatedPackage;
	}

	public String getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getBuildDirectory() {
		return buildDirectory;
	}

	public void setBuildDirectory(String buildDirectory) {
		this.buildDirectory = buildDirectory;
	}
	
}
