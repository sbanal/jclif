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

package org.jclif.type;

/**
 * This class serves as container of command line related properties used for parsing
 * like option prefix and option-parameter character delimiter.
 *  
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class CommandLineProperties implements Cloneable {
	
	/**
	 * Default delimiter between a command line option and its parameter
	 * in used in *nix based systems.
	 */
	public final static String DEFAULT_UNIX_OPTION_PARAMETER_DELIM = " ";

	/**
	 * Default delimiter between a command line option and its parameter
	 * in used in windows based systems.
	 */
	public final static String DEFAULT_WIN_OPTION_PARAMETER_DELIM = " ";
	
	/**
	 * Default prefix string of option identifier used in windows based systems.
	 */
	public final static String DEFAULT_UNIX_OPTION_PREFIX = "-";
	
	/**
	 * Default prefix string of option long identifier used in *nix based systems.
	 */
	public final static String DEFAULT_UNIX_OPTION_LONG_PREFIX = "--";
	
	/**
	 * Default prefix string of option identifier used in windows based systems.
	 */
	public final static String DEFAULT_WIN_OPTION_PREFIX = "/";
	
	/**
	 * Default prefix string of option long identifier used in windows based systems.
	 */
	public final static String DEFAULT_WIN_OPTION_LONG_PREFIX = "/";
	
	
	private static final CommandLineProperties WINDOWS_COMMAND_LINE_PROPERTIES  = new CommandLineProperties(
			DEFAULT_WIN_OPTION_PREFIX,
			DEFAULT_WIN_OPTION_LONG_PREFIX, 
			DEFAULT_UNIX_OPTION_PARAMETER_DELIM);
	private static final CommandLineProperties UNIX_COMMAND_LINE_PROPERTIES  = new CommandLineProperties(
			DEFAULT_UNIX_OPTION_PREFIX,
			DEFAULT_UNIX_OPTION_LONG_PREFIX, 
			DEFAULT_UNIX_OPTION_PARAMETER_DELIM);	
	
	private String optionPrefix;
	private String optionLongPrefix;
	private String optionParameterDelim;
	
	/**
	 * Creates a new instance of command line properties. 
	 * 
	 * @param optionPrefix		prefix used in command line options
	 * @param optionLongPrefix	prefix used in command line input options in long format
	 * @param optionParameterDelim delimiter string used between an option and its parameter
	 */
	public CommandLineProperties(String optionPrefix,
			String optionLongPrefix,
			String optionParameterDelim) {
		this.optionPrefix = optionPrefix;
		this.optionLongPrefix = optionLongPrefix;
		this.optionParameterDelim = optionParameterDelim;
	}
	
	/**
	 * Returns the command line properties based on system OS.
	 * @return CommandLineProperties command line properties
	 */
	public static CommandLineProperties getSystemProperties() {
		return getSystemProperties(System.getProperty("os.name"));
	}

	/**
	 * Returns the command line properties for a specific OS.
	 * @param osName	operation system name, e.g. value of <code>System.getProperty("os.name")</code>
	 * @return CommandLineProperties command line properties
	 */
	public static CommandLineProperties getSystemProperties(String osName) {
		if(osName.startsWith("Windows")) {
			return (CommandLineProperties) WINDOWS_COMMAND_LINE_PROPERTIES.clone();
		} else {
			return (CommandLineProperties) UNIX_COMMAND_LINE_PROPERTIES.clone();
		}
	}

	/**
	 * Returns the option prefix used in option identifiers.
	 * 
	 * @return String option prefix
	 */
	public String getOptionPrefix() {
		return optionPrefix;
	}

	/**
	 * Sets the prefix string used in option identifiers.
	 * 
	 * @param optionPrefix delimiter string
	 */
	public void setOptionPrefix(String optionPrefix) {
		this.optionPrefix = optionPrefix;
	}

	/**
	 * Returns the option prefix used in option long identifiers.
	 * 
	 * @return String option prefix
	 */
	public String getOptionLongPrefix() {
		return optionLongPrefix;
	}

	/**
	 * Sets the prefix string used in option long identifiers.
	 * 
	 * @param optionLongPrefix delimiter string
	 */
	public void setOptionLongPrefix(String optionLongPrefix) {
		this.optionLongPrefix = optionLongPrefix;
	}

	/**
	 * Returns the delimiter between a command line option and its parameter.
	 * 
	 * @return String option and delimiter prefix
	 */
	public String getOptionParameterDelim() {
		return optionParameterDelim;
	}

	/**
	 * Sets the delimiter used between a command line option and its parameter.
	 * 
	 * @param optionParameterDelim delimiter string
	 */
	public void setOptionParameterDelim(String optionParameterDelim) {
		this.optionParameterDelim = optionParameterDelim;
	}
	
	@Override
	public Object clone() {
		return new CommandLineProperties(this.getOptionPrefix(), this.getOptionLongPrefix(), this.getOptionParameterDelim());
	}
	
}
