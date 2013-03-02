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

import org.jclif.type.OptionMetadata.IdentifierType;


/**
 * OptionConfiguration class serves as container of all metadata information 
 * of an applications expected command line options.
 * 
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class OptionConfiguration extends Configuration<OptionMetadata>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6514260728828963130L;

	private Map<String, OptionMetadata> optionLongMap = new HashMap<String, OptionMetadata>();

	
	public OptionConfiguration() {
		super("option", "Option configuration");
	}

	/**
	 * Adds a new option metadata which describes the option's identifier and other information.
	 * 
	 * @param option	option metadata
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(OptionMetadata option) {
		
		String longIdentfier = option.getIdentifier(IdentifierType.LONG);
		validateIdentifier(longIdentfier, false);
		if (longIdentfier!=null 
				&& !longIdentfier.isEmpty() 
				&& null != optionLongMap.get(longIdentfier)) {
			throw new InvalidIdentifierException(this.getId() + " identifer '" + longIdentfier 
					+ "' already exist.");
		}
		
		optionLongMap.put(longIdentfier, option);
		super.add(option);
		
		return this;
	}
	
	/**
	 * Adds a new option with no description and no parameter.
	 * 
	 * @param identifier			identifier
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(String identifier) {
		addOption(identifier, "");
		return this;
	}
	
	/**
	 * Adds a new required command line option with description and no parameter.
	 * 
	 * @param identifier			identifier
	 * @param description			description of option
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(String identifier, String description) {
		addOption(identifier, true, description);
		return this;
	}
	
	/**
	 * Adds a new command line option with no parameter.
	 * 
	 * @param identifier			identifier
	 * @param required				true if option is required, otherwise false
	 * @param description			description of option
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(String identifier, boolean required, String description) {
		addOption(identifier, "", (ParameterMetadata) null, required, false, description, "");
		return this;
	}

	/**
	 * Adds a new command line option with no parameter.
	 * 
	 * @param identifier			identifier
	 * @param required				true if option is required, otherwise false
	 * @param multiValued			true if option is multi-valued, option is multi-valued if it can be specified more than once in comman line
	 * @param description			description of option
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(String identifier, boolean required, boolean multiValued, 
			String description) {
		addOption(identifier, "", (ParameterMetadata) null, required, multiValued, description, "");
		return this;
	}
	
	/**
	 * Adds a new command line option which supports short and long identifiers and has no parameter.
	 * 
	 * @param identifier			identifier
	 * @param longIdentifier		long identifier
	 * @param required				true if option is required, otherwise false
	 * @param multiValued			true if option is multi-valued, option is multi-valued if it can be specified more than once in comman line
	 * @param description			description of option
	 * @param longDescription		long description of option
	 * @return OptionConfiguration	this configuration
	 */
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
	
	/**
	 * Adds a new required option which accepts a parameter.
	 * 
	 * @param identifier		option identifier
	 * @param parameterType		parameter type
	 * @param description		description of option
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(String identifier, ParameterType parameterType, String description) {
		
		ParameterMetadata parameter = new ParameterMetadataImpl( identifier, parameterType);
		OptionMetadata option = new OptionMetadataImpl(
				identifier, "", 
				parameter,
				true, false, 
				description, "");
		addOption(option);
		
		return this;
	}
	
	/**
	 * Adds a new option which accepts a parameter.
	 * 
	 * @param identifier		option identifier
	 * @param longIdentifier	option long identifier
	 * @param parameterType		parameter type
	 * @param description		description of option
	 * @param longDescription	long description of option
	 * @return OptionConfiguration	this configuration
	 */
	public OptionConfiguration addOption(String identifier, String longIdentifier, 
			ParameterType parameterType,
			boolean required, boolean multiValued,
			String description, String longDescription) {
		
		ParameterMetadata parameter = new ParameterMetadataImpl( identifier, parameterType);
		OptionMetadata option = new OptionMetadataImpl(
				identifier, longIdentifier, 
				parameter,
				required, multiValued, 
				description, longDescription);
		addOption(option);
		
		return this;
	}
	
	/**
	 * Returns metadata of an option. Identifier can be the short or long identifier. 
	 * 
	 * @param identifier	identifier of option, can be the long or short identifier.
	 * @return OptionMetadata metadata of option
	 */
	public OptionMetadata getOption(String identifier) {
		OptionMetadata option = super.get(identifier);
		if(option==null) {
			option = optionLongMap.get(identifier);
		}
		return option;
	}

	/**
	 * Returns metadata of an option depending on the identifier type passed.
	 * 
	 * @param identifier	option identifier string
	 * @param type			identifier tpe
	 * @return OptionMetadata metadata of option
	 */
	public OptionMetadata getOption(String identifier, IdentifierType type) {
		if(type==IdentifierType.LONG) {
			return optionLongMap.get(identifier);
		}
		return super.get(identifier);
	}
	
	/**
	 * Returns the list of options registered to this configuration.
	 * 
	 * @return List<OptionMetadata> list of options.
	 */
	public List<OptionMetadata> getOptions() {
		return new ArrayList<OptionMetadata>(values());
	}

}
