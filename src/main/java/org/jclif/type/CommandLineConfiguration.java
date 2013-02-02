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
 * CommandLineConfiguration class is used to define the command line inputs accepted by an application.
 * This is the class from which we specify the accepted options, parameters and commands of an application.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class CommandLineConfiguration {
 
	
	/**
	 * Name of application
	 */
	private String name;
	
	/**
	 * Description of application used in help text.
	 */
	private String description;
	
	private OptionConfiguration optionConfiguration = new OptionConfiguration();
	private ParameterConfiguration parameterConfiguration = new ParameterConfiguration();
	private CommandConfiguration commandConfiguration = new CommandConfiguration();
	private CommandLineProperties commandLineProperties = null;
	
	/**
	 * Creates a new command line configuration using systems default command line properties.
	 */
	public CommandLineConfiguration() {
		this("appname", "", CommandLineProperties.getSystemProperties());
	}
	
	/**
	 * Creates a new command line configuration using systems default command line properties.
	 * 
	 * @param name			name of application, value is used in printing help text
	 * @param description 	description of application
	 */
	public CommandLineConfiguration(String name, String description) {
		this(name, description, CommandLineProperties.getSystemProperties());
	}
	
	/**
	 * Creates a new command line configuration.
	 * 
	 * @param name			name of application, value is used in printing help text
	 * @param description 	description of application
	 * @param commandLineProperties	command line properties to use in parsing command line input
	 */
	public CommandLineConfiguration(String name, String description, CommandLineProperties commandLineProperties) {
		this.name = name;
		this.description = description;
		this.commandLineProperties = (CommandLineProperties) commandLineProperties.clone();
	}

	/**
	 * Returns the name of the console based application.
	 * 
	 * @return	String	name of console based application
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of a console based application.
	 * 
	 * @param name		name of console based application
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns description of a console based application as shown in help or usage text.
	 * 
	 * @return	String 	description of application
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns option configuration which contains all the supported options.
	 * 
	 * @return	OptionConfiguration	option configuration
	 */
	public OptionConfiguration getOptionConfiguration() {
		return optionConfiguration;
	}

	/**
	 * Returns command line properties used by command line input parser.
	 * 
	 * @return	CommandLineProperties	command configuration
	 */
	public CommandLineProperties getCommandLineProperties() {
		return commandLineProperties;
	}

	/**
	 * Sets command line properties used by command line input parser.
	 * 
	 * @param	OptionConfiguration	option configuration
	 */
	public void setCommandLineProperties(CommandLineProperties commandLineProperties) {
		this.commandLineProperties =  (CommandLineProperties) commandLineProperties.clone();
	}

	/**
	 * Returns parameter configuration which contains all the parameters supported in a command line input.
	 * 
	 * @return	ParameterConfiguration	parameter configuration
	 */
	public ParameterConfiguration getParameterConfiguration() {
		return parameterConfiguration;
	}

	/**
	 * Returns command configuration which contains all the commands supported in a command line input.
	 * 
	 * @return CommandConfiguration	 command configuration
	 */
	public CommandConfiguration getCommandConfiguration() {
		return commandConfiguration;
	}
	
	/**
	 * Returns the option metadata.
	 * 
	 * @param identifier	identifier string of an option
	 * @return	OptionMetadata	option metadata
	 */
	public OptionMetadata getOption(String identifier) {
		return optionConfiguration.getOption(identifier);
	}

	/**
	 * Returns the command metadata.
	 * 
	 * @param identifier	identifier of command
	 * @return	CommandMetadata	command metadata
	 */
	public CommandMetadata getCommandMetadata(String identifier) {
		return commandConfiguration.get(identifier);
	}
	
}
