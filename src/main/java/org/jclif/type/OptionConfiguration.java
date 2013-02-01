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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jclif.type.OptionMetadata.IdentifierFormat;


/**
 * OptionConfiguration class serves as container of all metadata information 
 * of an applications expected command line options.
 * 
 * 
 * @author Stephen Lou Banal <stephen.banal@gmail.com>
 *
 */
public class OptionConfiguration extends Configuration<OptionMetadata>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6514260728828963130L;

	private final static Logger LOGGER = Logger.getLogger(OptionConfiguration.class.getName());
	
	private Map<String, OptionMetadata> optionLongMap = new HashMap<String, OptionMetadata>();
	private CommandLineProperties commandLineProperties = null;
	
	public OptionConfiguration() {
		this(CommandLineProperties.UNIX_COMMAND_LINE_PROPERTIES);
	}
	
	public OptionConfiguration(CommandLineProperties commandLineProperties) {
		super("option", "Option configuration");
		this.commandLineProperties = new CommandLineProperties(
				commandLineProperties.getOptionPrefix(),
				commandLineProperties.getOptionLongPrefix(),
				commandLineProperties.getOptionParameterDelim());
	}

	public CommandLineProperties getCommandLineProperties() {
		return commandLineProperties;
	}

	public OptionConfiguration addOption(OptionMetadata option) {
		super.add(option);
		optionLongMap.put(option.getIdentifier(IdentifierFormat.LONG), option);
		LOGGER.info("Adding option " + option);
		return this;
	}
	
	public OptionConfiguration addOption(String identifier) {
		addOption(identifier, "");
		return this;
	}
	
	public OptionConfiguration addOption(String identifier, String description) {
		addOption(identifier, true, description);
		return this;
	}
	
	public OptionConfiguration addOption(String identifier, boolean required, String description) {
		addOption(identifier, "", (ParameterMetadata) null, required, false, description, "");
		return this;
	}

	public OptionConfiguration addOption(String identifier, boolean required, boolean multiValued, String description) {
		addOption(identifier, "", (ParameterMetadata) null, required, multiValued, description, "");
		return this;
	}
	
	public OptionConfiguration addOption(String identifier, String longIdentifier, 
			ParameterMetadata parameter,
			boolean required, boolean multiValued,
			String description, String longDescription) {
		
		OptionMetadata option = new OptionMetadataImpl(
				identifier, longIdentifier, 
				parameter,
				required, multiValued, 
				description, longDescription);
		addOption(option);
		
		return this;
	}
	
	public OptionConfiguration addOption(String identifier, ParameterType parameterType, String description) {
		
		ParameterMetadata parameter = new ParameterMetadataImpl( identifier, false, false, parameterType, null);
		OptionMetadata option = new OptionMetadataImpl(
				identifier, "", 
				parameter,
				true, false, 
				description, "");
		addOption(option);
		
		return this;
	}
	
	public OptionConfiguration addOption(String identifier, String longIdentifier, 
			ParameterType parameterType,
			boolean required, boolean multiValued,
			String description, String longDescription) {
		
		ParameterMetadata parameter = new ParameterMetadataImpl( identifier, false, false, parameterType, null);
		OptionMetadata option = new OptionMetadataImpl(
				identifier, longIdentifier, 
				parameter,
				required, multiValued, 
				description, longDescription);
		addOption(option);
		
		return this;
	}
	
	public OptionMetadata getOption(String identifier) {
		OptionMetadata option = super.get(identifier);
		if(option==null) {
			option = optionLongMap.get(identifier);
		}
		return option;
	}

	public OptionMetadata getOption(String identifier, IdentifierFormat type) {
		if(type==IdentifierFormat.LONG) {
			return optionLongMap.get(identifier);
		}
		return super.get(identifier);
	}
	
	public List<OptionMetadata> getOptions() {
		return new ArrayList<OptionMetadata>(values());
	}

	public String getParameterDelimitter() {
		return commandLineProperties.getOptionParameterDelim();
	}

	public void setParameterDelimitter(String parameterDelimitter) {
		this.commandLineProperties.setOptionParameterDelim(parameterDelimitter);
	}

	public String getOptionPrefix() {
		return commandLineProperties.getOptionPrefix();
	}

	public void setOptionPrefix(String optionPrefix) {
		commandLineProperties.setOptionPrefix(optionPrefix);
	}

	public String getOptionLongPrefix() {
		return commandLineProperties.getOptionLongPrefix();
	}

	public void setOptionLongPrefix(String optionLongPrefix) {
		commandLineProperties.setOptionLongPrefix(optionLongPrefix);
	}

}
