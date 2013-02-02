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

import java.util.Arrays;
import java.util.regex.Pattern;


/**
 * CommandLineConfiguration class is used to define the command line inputs accepted by an application.
 * This is the class from which we specify the accepted options, parameters and commands of an application.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class CommandLineConfiguration {
 
	private final Pattern IDENTIFIER_REGEX = Pattern.compile("^([\\w]+)$");
	
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

	public OptionConfiguration getOptionConfiguration() {
		return optionConfiguration;
	}

	public CommandLineProperties getCommandLineProperties() {
		return commandLineProperties;
	}

	public void setCommandLineProperties(CommandLineProperties commandLineProperties) {
		this.commandLineProperties =  (CommandLineProperties) commandLineProperties.clone();
	}

	public ParameterConfiguration getParameterConfiguration() {
		return parameterConfiguration;
	}

	public CommandConfiguration getCommandConfiguration() {
		return commandConfiguration;
	}
	
	public CommandLineConfiguration addOption(String identifier) {
		addOption(identifier, "", (ParameterMetadata) null, true, false, "", "");
		return this;
	}
	
	public CommandLineConfiguration addOption(String identifier, String description) {
		addOption(identifier, "", (ParameterMetadata) null, true, false, description, "");
		return this;
	}
	
	public CommandLineConfiguration addOption(String identifier, boolean required, String description) {
		addOption(identifier, "", (ParameterMetadata) null, required, false, description, "");
		return this;
	}

	public CommandLineConfiguration addOption(String identifier, boolean required, boolean multiValued, String description) {
		addOption(identifier, "", (ParameterMetadata) null, required, multiValued, description, "");
		return this;
	}
	

	public CommandLineConfiguration addOption(String identifier, String longIdentifier, 
			ParameterType parameterType,
			boolean required, boolean multiValued,
			String description, String longDescription) {
		
		if(parameterType==ParameterType.CUSTOM) {
			throw new IllegalArgumentException("ParameterType.CUSTOM is not supported in this method. Use adOptions(id,longId, metadata, required, ..) method.");
		}
		
		ParameterMetadata parameter = new ParameterMetadataImpl( identifier, false, false, parameterType, null);
		addOption(identifier, longIdentifier, parameter, required, multiValued, description, longDescription);
		
		return this;
	}
	
	public CommandLineConfiguration addOption(String identifier, String longIdentifier, 
			ParameterMetadata parameter,
			boolean required, boolean multiValued,
			String description, String longDescription) {
		
		validateIdentifier(identifier);
		validateOptionIdentifier(identifier);
		OptionMetadata option = new OptionMetadataImpl(
				identifier, longIdentifier, 
				parameter,
				required, multiValued, 
				description, longDescription);
		optionConfiguration.addOption(option);
		
		return this;
	}
	
	public CommandLineConfiguration addOption(OptionMetadata option) {
		optionConfiguration.addOption(option);
		return this;
	}
	
	
	void validateIdentifier(String identifier) {
		if(identifier==null || identifier.isEmpty()) {
			throw new InvalidIdentifierException("Identifier invalid, value is empty or null.");
		}
		if(!IDENTIFIER_REGEX.matcher(identifier).matches()) {
			throw new InvalidIdentifierException("Identifier " + identifier + " is not valid. Only characters A-Z,a-z,0-9 and _ is allowed (in regex \\w) .");
		}
	}
	
	void validateCommandIdentifier(String identifier) {
		if(commandConfiguration.containsKey(identifier)) {
			throw new InvalidIdentifierException("Command " + identifier + " already exist.");
		}
	}
	
	void validateParameterIdentifier(String identifier) {
		if(getParameterConfiguration().contains(identifier)) {
			throw new InvalidIdentifierException("Parameter " + identifier + " already exist.");
		}
	}
	
	void validateOptionIdentifier(String identifier) {
		if(null!=getOption(identifier)) {
			throw new InvalidIdentifierException("Option " + identifier + " already exist.");
		}
	}
	
	public OptionMetadata getOption(String identifier) {
		return optionConfiguration.getOption(identifier);
	}
	
	public CommandLineConfiguration addParameter(ParameterMetadata metadata) {
		validateIdentifier(metadata.getIdentifier());		
		validateCommandIdentifier(metadata.getIdentifier());
		parameterConfiguration.add(metadata);
		return this;
	}
	
	public CommandLineConfiguration addParameter(String identifier, boolean required, String description) {
		ParameterMetadata param = new ParameterMetadataImpl(identifier, required, description);
		addParameter(param);
		return this;
	}
	
	public CommandLineConfiguration addParameter(String identifier, boolean required, boolean multiValued, String description) {
		ParameterMetadata param = new ParameterMetadataImpl(identifier, required, multiValued, description);
		addParameter(param);
		return this;
	}
	
	public CommandLineConfiguration addCommand(String keyword, OptionConfiguration optionConfiguration, String description) {
		CommandMetadata metadata = new CommandMetadataImpl( keyword, optionConfiguration, description, null);
		addCommand(metadata);
		return this;
	}
	
	public CommandLineConfiguration addCommand(String keyword, OptionConfiguration optionConfiguration, String description, ParameterMetadata... parameters) {
		CommandMetadata metadata = new CommandMetadataImpl( keyword, optionConfiguration, Arrays.asList(parameters), description, null);
		addCommand(metadata);
		return this;
	}
	
	public CommandLineConfiguration addCommand(CommandMetadata metadata) {
		validateIdentifier(metadata.getKeyword());
		validateParameterIdentifier(metadata.getKeyword());
		commandConfiguration.add(metadata);
		return this;
	}
	
	public CommandMetadata getCommandMetadata(String identifier) {
		return commandConfiguration.get(identifier);
	}
	
}
