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

package org.jclif.parser;

import org.jclif.type.Command;
import org.jclif.type.CommandMetadata;
import org.jclif.type.InvalidInputException;
import org.jclif.type.Option;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionSet;
import org.jclif.type.Parameter;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterSet;

/**
 * CommandLineParseResult class represents the result of command line parsing.
 * 
 * @author Stephen Lou Banal <stephen.banal@gmail.com>
 *
 */
public class CommandLineParseResult {

	private OptionSet optionSet;
	private ParameterSet parameterSet;
	private Command matchingCommand;
	private CommandLineConfiguration configuration;
	
	CommandLineParseResult(CommandLineConfiguration configuration) {
		this.configuration = configuration;
		optionSet = new OptionSet(configuration.getOptionConfiguration());
		parameterSet = new ParameterSet(configuration.getParameterConfiguration());
	}

	public CommandLineConfiguration getConfiguration() {
		return configuration;
	}
	
	public boolean isCommandMatch() {
		return getMatchingCommand() != null;
	}

	public Command getMatchingCommand() {
		return matchingCommand;
	}

	void setMatchingCommand(Command matchingCommand) {
		this.matchingCommand = matchingCommand;
		if(matchingCommand!=null) {
			CommandMetadata metadata = (CommandMetadata) matchingCommand.getMetadata();
			this.optionSet = new OptionSet(metadata.getOptionConfigurations());
			this.parameterSet = new ParameterSet(metadata.getParameterConfigurations());
		} else {
			optionSet = new OptionSet(configuration.getOptionConfiguration());
			parameterSet = new ParameterSet(configuration.getParameterConfiguration());
		}
	}
	
	public OptionSet getOptionSet() {
		return optionSet;
	}

	public ParameterSet getParameterSet() {
		return parameterSet;
	}
	
	public void validate() throws InvalidInputException {
		
		OptionConfiguration optionConfig = configuration.getOptionConfiguration();
		ParameterConfiguration parameterConig = configuration.getParameterConfiguration();
		if(this.isCommandMatch()) {
			CommandMetadata cmdMetadata = (CommandMetadata) this.getMatchingCommand().getMetadata();
			optionConfig = cmdMetadata.getOptionConfigurations();
			parameterConig = cmdMetadata.getParameterConfigurations();
		}
		
		// validate options
		for(OptionMetadata metadata : optionConfig.getOptions()) {
			if(metadata.isRequired()) {
				if(!getOptionSet().contains(metadata.getIdentifier())) {
					throw new InvalidInputException("Option " + configuration.getOptionConfiguration().getOptionPrefix() + metadata.getIdentifier() + " is required.");
				}
			}
			if(metadata.getParameterMetadata()!=null && metadata.getParameterMetadata().isRequired()) {
				Option option = getOptionSet().get(metadata.getIdentifier());
				if(option.getParameter() == null || option.getParameter().getValue() == null) {
					throw new InvalidInputException("Missing parameter for option " + configuration.getOptionConfiguration().getOptionPrefix() + metadata.getIdentifier() + ".");
				}
			}
		}
		
		// validate parameters
		for(ParameterMetadata metadata : parameterConig.values()) {
			if(metadata.isRequired()) {
				Parameter parameter = getParameterSet().get(metadata.getIdentifier());
				if(parameter == null || parameter.getValue() == null) {
					throw new InvalidInputException("Missing parameter " + configuration.getOptionConfiguration().getOptionPrefix() +  metadata.getIdentifier() + ".");
				}
			}
		}
			
	}
	
	public void clear() {
		matchingCommand = null;
		optionSet.clear();
		parameterSet.clear();
	}

}
