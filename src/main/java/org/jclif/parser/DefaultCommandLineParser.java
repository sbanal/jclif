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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jclif.type.CommandInputImpl;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandLineProperties;
import org.jclif.type.CommandMetadata;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionInput;
import org.jclif.type.OptionInputImpl;
import org.jclif.type.OptionInputSet;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionMetadata.IdentifierType;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterInput;
import org.jclif.type.ParameterInputImpl;
import org.jclif.type.ParameterInputSet;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterParser;
import org.jclif.util.StringUtil;


/**
 * Default implementation of CommandLineFormat.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
class DefaultCommandLineParser extends CommandLineParser {

	private final static Logger LOGGER = Logger.getLogger(DefaultCommandLineParser.class.getName());
	
	private static final Pattern optionsIdRegEx = Pattern.compile("([\\w]+)");
	private static final Pattern paramValueRegEx = Pattern.compile("((\"[\\p{Alnum}\\p{Punct}\\p{Space}&&[^\"]]+\")|('[\\p{Alnum}\\p{Punct}\\p{Space}&&[^']]+')|([\\p{Alnum}\\p{Punct}]+))");
	
	
	@Override
	public CommandLineParseResult parse(CommandLineConfiguration configuration,
			String... args) throws InvalidInputException {
		
		CommandLineParseResult resultSet = new CommandLineParseResult(configuration);
		
		StringBuffer sb = new StringBuffer();
		for(String arg: args) {
			if(StringUtil.containsSpace(arg)) {
				sb.append(String.format("\"%s\"", arg));
			} else {
				sb.append(arg);
			}
			sb.append(" ");
		}
		
		LOGGER.info(String.format("Command input:%s", sb.toString()));
		
		Scanner scanner = new Scanner(sb.toString());
		boolean cmdFound = parseCommand(scanner, configuration, resultSet);
		if(!cmdFound){
			parseOptions(scanner, configuration, null, configuration.getOptionConfiguration(), resultSet.getOptionInput());
			parseParameters(scanner, configuration, null, configuration.getParameterConfiguration(), resultSet.getParameterInput());
		}
		scanner.close();
		
		validate( configuration, resultSet);
		
		return resultSet;
	}
	
	private boolean parseCommand(Scanner scanner, CommandLineConfiguration configuration, 
			CommandLineParseResult resultSet) throws InvalidInputException {
		
		if(configuration.getCommandConfiguration().isEmpty()) {
			return false;
		}
		
		StringBuffer sbOptionFlags = new StringBuffer("(");
		for(CommandMetadata cmdMetadata : configuration.getCommandConfiguration().values()) {
			sbOptionFlags.append(cmdMetadata.getIdentifier());
			sbOptionFlags.append("|");
		}
		sbOptionFlags.setCharAt(sbOptionFlags.length()-1, ')');
		
		Pattern commandsParam = Pattern.compile(String.format("^%s", sbOptionFlags.toString()));
		String token = scanner.findInLine(commandsParam);
		if(token==null) {
			return false;
		}
		
		MatchResult result = scanner.match();
		String cmdIdentifier = result.group(0);
		CommandMetadata cmdMetadata = configuration.getCommandMetadata(cmdIdentifier);
		resultSet.setMatchingCommand(new CommandInputImpl(cmdIdentifier, cmdMetadata));
		
		LOGGER.info(String.format("Token=%s%n", token));
		LOGGER.info(String.format("Command=%s%n", scanner));
		
		String paramsToken = scanner.findInLine("[ \t]+(.*)");
		if(paramsToken!=null) {
			MatchResult paramResult = scanner.match();
			LOGGER.info(String.format("Options=%s%n", paramResult.group(1)));
			Scanner scannerOptions = new Scanner(paramResult.group(1));
			parseOptions(scannerOptions, configuration, cmdMetadata, cmdMetadata.getOptionConfigurations(), resultSet.getOptionInput());
			parseParameters(scannerOptions, configuration, cmdMetadata, cmdMetadata.getParameterConfigurations(), resultSet.getParameterInput());
			scannerOptions.close();
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean parseOptions(Scanner scanner, CommandLineConfiguration config, CommandMetadata cmdMetadata, 
			OptionConfiguration optionConfig, OptionInputSet resultSet) throws InvalidInputException {
		
		if(optionConfig.getOptions().isEmpty()) {
			return true;
		}
		
		String token = null;
		CommandLineProperties cmdLineProperties = config.getCommandLineProperties();
		final Pattern optionsShortPrefix = Pattern.compile(cmdLineProperties.getOptionPrefix());
		final Pattern optionsLongPrefix = Pattern.compile(cmdLineProperties.getOptionLongPrefix());
		
		// parse options
		while (true) {
			
			// parse the option prefix
			String optionPrefix = scanner.findInLine(optionsLongPrefix);
			if(optionPrefix==null) {
				optionPrefix = scanner.findInLine(optionsShortPrefix);
				if(optionPrefix==null) {
					break; // word is not a prefix
				}
			}
			
			// parse option identifier extract option identifier and get its metadata
			String optionId = scanner.findInLine(optionsIdRegEx);
			if(optionId==null) {
				break; // prefix has no option id
			}
			
			LOGGER.info(String.format("Options Prefix={%s}, Id={%s}", optionPrefix, optionId));
			
			OptionMetadata metadata = null;
			if(cmdLineProperties.getOptionPrefix().equals(cmdLineProperties.getOptionLongPrefix())) {
				metadata = optionConfig.getOption(optionId);
			} else {
				boolean longPrefix = optionPrefix.equals(cmdLineProperties.getOptionLongPrefix());
				metadata = optionConfig.getOption(optionId, (longPrefix)?IdentifierType.LONG:IdentifierType.SHORT);
			}
			if (metadata == null) {
				throw new InvalidInputException("Option " + optionPrefix + optionId + " is invalid.", cmdMetadata);
			}

			// parse the parameter
			Object parameterValue = null;
			token = scanner.findInLine(config.getCommandLineProperties().getOptionParameterDelim());
			if (metadata.isParameterAccepted() && token!=null) {
				
				token = scanner.findInLine(paramValueRegEx);
				
				ParameterMetadata parameterMetadata = metadata.getParameterMetadata();
				if (parameterMetadata.isRequired() && token==null) {
					throw new InvalidInputException("Parameter required for option " + optionPrefix + optionId + ".", cmdMetadata);
				}
				
				if (token != null) {
					MatchResult paramResult = scanner.match();
					for(int i=0;i<paramResult.groupCount();i++) {
						LOGGER.info(String.format("[%d] Delim={%s}, Param={%s}", i, config.getCommandLineProperties().getOptionParameterDelim(), paramResult.group(i)));
					}
					String groupValue = StringUtil.extractQuotedValue(paramResult.group(1));
					LOGGER.info(String.format("Delim={%s}, Param={%s}", config.getCommandLineProperties().getOptionParameterDelim(), groupValue));
					parameterValue = getParameterValue(cmdMetadata, parameterMetadata, groupValue);
				}
				
			}

			// create the parameter value
			ParameterInput parameter = null;
			OptionInput optionValue = resultSet.get(optionId);
			if (optionValue == null) {
				if (metadata.isParameterAccepted()) {
					if (metadata.isMultiValued()) {
						List<Object> valueList = new ArrayList<Object>();
						valueList.add(parameterValue);
						parameter = new ParameterInputImpl(metadata.getParameterMetadata(), valueList);
					} else {
						parameter = new ParameterInputImpl(metadata.getParameterMetadata(), parameterValue);
					}
				}
				optionValue = new OptionInputImpl(metadata, parameter);
				resultSet.add(optionValue);
			} else {
				parameter = optionValue.getParameter();
				if (parameter != null && metadata.isMultiValued()) {
					List<Object> valueList = (List<Object>) parameter.getValue();
					valueList.add(parameterValue);
				} else {
					LOGGER.info("Skipping parameter for arg = "
							+ optionId
							+ " since value alreay exist. Might be arg was specified twice.");
				}
			}

		}
	
		return !resultSet.isEmpty();
	}
	
	private Object getParameterValue(CommandMetadata cmdMetadata, ParameterMetadata metadata, String paramValue) throws InvalidInputException {
		try {
			ParameterParser parser = ParameterParserFactory.getInstance().createParser(metadata);
			return parser.parseValue(metadata, paramValue);
		}catch(Exception e) {
			throw new InvalidInputException("Invalid parameter value '" + paramValue + "' for parameter " + metadata.getIdentifier(), e, cmdMetadata);
		}
	}
	
	private boolean parseParameters(Scanner scanner, CommandLineConfiguration config, CommandMetadata cmdMetadata, 
			ParameterConfiguration parameterConfig, ParameterInputSet resultSet) throws InvalidInputException {
		
		if(parameterConfig.isEmpty()) {
			return true;
		}
		
		boolean parametersFound = false;
		for(ParameterMetadata paramMeta : parameterConfig.values()) {
			Object paraValue = null;
			if(paramMeta.isMultiValued()) {
				List<Object> valueList = new ArrayList<Object>();
				int i = 0;
				while(scanner.hasNext()) {
					String token = scanner.next();
					LOGGER.info(String.format("Parameter[%d]={%s}", (i++), token));
					valueList.add(getParameterValue(cmdMetadata, paramMeta, token));
				}
				if(paramMeta.isRequired() && valueList.isEmpty()) {
					throw new InvalidInputException("Parameter " + paramMeta.getIdentifier() + " is required but not specified", cmdMetadata);
				}
				resultSet.add(new ParameterInputImpl(paramMeta, valueList));
			} else {
				if(scanner.hasNext()) {
					String token = scanner.next();
					LOGGER.info(String.format("Parameter={%s}", token));
					paraValue = getParameterValue(cmdMetadata, paramMeta, token);
					resultSet.add(new ParameterInputImpl(paramMeta, paraValue));
				} else {
					if(paramMeta.isRequired()) {
						throw new InvalidInputException("Parameter " + paramMeta.getIdentifier() + " is required but not specified", cmdMetadata);
					}
				}
			}
			parametersFound = true;
		}
		
		return parametersFound;
	}

	@Override
	public boolean matches(CommandLineConfiguration configuration, CommandLineParseResult resultSet, String... args) {
		try {
			CommandLineParseResult result = parse(configuration, args);
			resultSet.clear();
			resultSet.setMatchingCommand(result.getMatchingCommand());
			resultSet.getOptionInput().addAll(result.getOptionInput());
			resultSet.getParameterInput().addAll(result.getParameterInput());
			return true;
		} catch (InvalidInputException e) {
			return false;
		}
	}
	
	void validate(CommandLineConfiguration configuration, CommandLineParseResult result) throws InvalidInputException {
		
		OptionConfiguration optionConfig = configuration.getOptionConfiguration();
		ParameterConfiguration parameterConig = configuration.getParameterConfiguration();
		if(result.isCommandMatch()) {
			CommandMetadata cmdMetadata = (CommandMetadata) result.getMatchingCommand().getMetadata();
			optionConfig = cmdMetadata.getOptionConfigurations();
			parameterConig = cmdMetadata.getParameterConfigurations();
		}
		
		// validate options
		for(OptionMetadata metadata : optionConfig.getOptions()) {
			if(metadata.isRequired()) {
				if(!result.getOptionInput().contains(metadata.getIdentifier())) {
					throw new InvalidInputException("Option " + configuration.getCommandLineProperties().getOptionPrefix() + metadata.getIdentifier() + " is required.");
				}
			}
			if(metadata.getParameterMetadata()!=null && metadata.getParameterMetadata().isRequired()) {
				OptionInput option = result.getOptionInput().get(metadata.getIdentifier());
				if(option.getParameter() == null || option.getParameter().getValue() == null) {
					throw new InvalidInputException("Missing parameter for option " + configuration.getCommandLineProperties().getOptionPrefix() + metadata.getIdentifier() + ".");
				}
			}
		}
		
		// validate parameters
		for(ParameterMetadata metadata : parameterConig.values()) {
			if(metadata.isRequired()) {
				ParameterInput parameter = result.getParameterInput().get(metadata.getIdentifier());
				if(parameter == null || parameter.getValue() == null) {
					throw new InvalidInputException("Missing parameter " + configuration.getCommandLineProperties().getOptionPrefix() +  metadata.getIdentifier() + ".");
				}
			}
		}
			
	}

}
