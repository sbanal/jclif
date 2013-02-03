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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jclif.type.CommandInputImpl;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandMetadata;
import org.jclif.type.OptionInput;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionInputImpl;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionInputSet;
import org.jclif.type.ParameterInput;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterInputImpl;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterParser;
import org.jclif.type.ParameterInputSet;
import org.jclif.type.ParameterType;
import org.jclif.type.OptionMetadata.IdentifierType;
import org.jclif.util.StringUtil;


/**
 * Default implementation of CommandLineFormat.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
class DefaultCommandLineParser extends CommandLineParser {

	private final static Logger LOGGER = Logger.getLogger(DefaultCommandLineParser.class.getName());
	
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
	private boolean parseOptions(Scanner scanner, CommandLineConfiguration config, CommandMetadata cmdMetadata, OptionConfiguration optionConfig, 
			OptionInputSet resultSet) throws InvalidInputException {
		
		if(optionConfig.getOptions().isEmpty()) {
			return true;
		}
		
		// Build the regex of all possible identifiers
		StringBuffer sbOptionFlags = new StringBuffer("(");
		for(OptionMetadata optionMetadata : optionConfig.getOptions()) {
			sbOptionFlags.append(String.format("%s%s", config.getCommandLineProperties().getOptionPrefix(), optionMetadata.getIdentifier()));
			String longIdentifier = optionMetadata.getIdentifier(IdentifierType.LONG);
			if(longIdentifier!=null && !longIdentifier.isEmpty()) {
				sbOptionFlags.append(String.format("|%s%s", config.getCommandLineProperties().getOptionLongPrefix(), longIdentifier));
			}
			sbOptionFlags.append("|");
		}
		sbOptionFlags.setCharAt(sbOptionFlags.length()-1, ')');
		LOGGER.info(String.format("OptionsFlags regex={%s}", sbOptionFlags.toString()));
		
		// parse options
		Pattern optionsParam = Pattern.compile(sbOptionFlags.toString());
		String token = null;
		
		
		
		while ((token = scanner.findInLine(optionsParam)) != null) {

			MatchResult result = scanner.match();
			LOGGER.info(String.format("Options={%s}", token));
			for (int i = 0; i <= result.groupCount(); i++) {
				LOGGER.info(String.format("Options Group[%d]={%s}", i, result.group(i)));
			}

			// parse option identifier extract option identifier and get its metadata
			String paramIdentifierStr = result.group(1);
			String paramId = paramIdentifierStr.startsWith(config.getCommandLineProperties().getOptionLongPrefix()) ? paramIdentifierStr.substring(2) : paramIdentifierStr.substring(1);
			OptionMetadata metadata = optionConfig.getOption(paramId);
			if (metadata == null) {
				throw new InvalidInputException("Option " + paramIdentifierStr + " is not found.", cmdMetadata);
			}

			// parse the parameter
			Object parameterValue = null;
			token = scanner.findInLine(config.getCommandLineProperties().getOptionParameterDelim());
			if (metadata.isParameterAccepted() && token!=null) {
				
				Pattern paramValueRegEx = Pattern.compile("((\"[\\p{Alnum}\\p{Punct}\\p{Space}&&[^\"]]+\")|('[\\p{Alnum}\\p{Punct}\\p{Space}&&[^']]+')|([\\p{Alnum}\\p{Punct}]+))");
				token = scanner.findInLine(paramValueRegEx);
				
				ParameterMetadata parameterMetadata = metadata.getParameterMetadata();
				if (parameterMetadata.isRequired() && token==null) {
					throw new InvalidInputException("Parameter required for option " + result.group(1) + ".", cmdMetadata);
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
			OptionInput optionValue = resultSet.get(paramId);
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
							+ paramId
							+ " since value alreay exist. Might be arg was specified twice.");
				}
			}

		}
		
		// check if the next token is a possible flag but was wrongly specified
		if(token==null) {
			Pattern prefixPattern = null;
			if(!config.getCommandLineProperties().getOptionLongPrefix().isEmpty()) {
				prefixPattern = Pattern.compile(String.format("^(%s|%s)(\\w)",
						config.getCommandLineProperties().getOptionPrefix(), 
						config.getCommandLineProperties().getOptionLongPrefix()));
			} else {
				prefixPattern = Pattern.compile(String.format("^(%s)(\\w)",
						config.getCommandLineProperties().getOptionPrefix()));
			}
			token = scanner.findInLine(prefixPattern);
			if(token!=null) {
				MatchResult result = scanner.match();
				String paramIdentifierStr = result.group(2);
				throw new InvalidInputException("Invalid Option " + paramIdentifierStr);
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

	public String format(CommandLineConfiguration config, InvalidInputException e) {
		String usage;
		if(e.isCommandError()) {
			usage = format(config, e.getCommandMetadata(), CommandLineFormatType.SHORT);
		} else {
			usage = format(config, CommandLineFormatType.SHORT);
		}
		return String.format("Error: %s%n%s",  e.getMessage(), usage);
	}
	
	public String format(CommandLineConfiguration config, String errorMessage) {
		return String.format("Error: %s%n%s",  errorMessage, format(config));
	}
	
	public String format(CommandLineConfiguration config) {
		return format(config, CommandLineFormatType.SHORT);
	}
	
	public String format(CommandLineConfiguration config, CommandMetadata commandMetadata, CommandLineFormatType formatType) {
		
		List<OptionMetadata> optionMetaDataList = commandMetadata.getOptionConfigurations().getOptions();
		Map<String, OptionMetadata> optionFormatMap = new LinkedHashMap<String, OptionMetadata>();
		Integer maxIdLength = 0;
		for(OptionMetadata metadata : optionMetaDataList) {
			String optionFormatStr = formatOption(metadata, config);
			maxIdLength = Math.max(optionFormatStr.length(), maxIdLength);
			optionFormatMap.put(optionFormatStr, metadata);
		}
		
		String parameterFormatList = formatParameterList(config, commandMetadata.getParameterConfigurations());
		StringBuffer sb = new StringBuffer();
		if(!commandMetadata.getDescription().isEmpty()) {
			sb.append(String.format("Description: %s%n", commandMetadata.getDescription()));
		}
		sb.append(String.format("Usage:    %s %s [options] %s%n", config.getName(), commandMetadata.getIdentifier(), parameterFormatList));
		sb.append(formatOptionList(config, commandMetadata.getOptionConfigurations(), formatType));
		
		return sb.toString();
	}
	
	public String format(CommandLineConfiguration config, CommandLineFormatType formatType) {
		
		String parameterFormatList = formatParameterList(config, config.getParameterConfiguration());
		StringBuffer sb = new StringBuffer();
		if(!config.getDescription().isEmpty()) {
			sb.append(String.format("Description: %s%n", config.getDescription()));
		}
		
		sb.append("Usage:");
		boolean defaultCommandExist = (!config.getOptionConfiguration().isEmpty() || !config.getParameterConfiguration().isEmpty());
		if(defaultCommandExist){
			sb.append(String.format("  %s [options] %s%n", config.getName(), parameterFormatList));
		}
		if(defaultCommandExist && !config.getCommandConfiguration().isEmpty()) {
			sb.append(String.format("   or   %s [command] [options] parameters...%n", config.getName()));
		} else {
			sb.append(String.format("  %s [command] [options] parameters...%n", config.getName()));
		}
		
		sb.append(formatOptionList(config, config.getOptionConfiguration(), formatType));
		sb.append(formatCommands(config));
		
		return sb.toString();
	}
	
	String formatOptionList(CommandLineConfiguration config, OptionConfiguration optionConfig, CommandLineFormatType formatType) { 
		
		StringBuffer sb = new StringBuffer();
		List<OptionMetadata> optionMetaDataList = optionConfig.getOptions();
		Map<String, OptionMetadata> optionFormatMap = new LinkedHashMap<String, OptionMetadata>();
		Integer maxIdLength = 0;
		for(OptionMetadata metadata : optionMetaDataList) {
			String optionFormatStr = formatOption(metadata, config);
			LOGGER.info("formatOption: " + optionFormatStr + "-->"+ metadata);
			maxIdLength = Math.max(optionFormatStr.length(), maxIdLength);
			optionFormatMap.put(optionFormatStr, metadata);
		}
		
		final String optionStrFormat = "    %-" + (maxIdLength + 5) + "s%s%n";
		
		if(!optionFormatMap.isEmpty()) {
			sb.append(String.format("Options:%n"));
		}
		
		switch(formatType) {
		case FULL:
			for(Map.Entry<String,OptionMetadata> optionEntry : optionFormatMap.entrySet()) {
				OptionMetadata metadata = optionEntry.getValue();
				String desc = (metadata.getLongDescription().isEmpty())?metadata.getDescription():metadata.getLongDescription();
				sb.append(String.format(optionStrFormat, optionEntry.getKey(), desc));
			}
			break;
		case SHORT:
		default:
			for(Map.Entry<String,OptionMetadata> optionEntry : optionFormatMap.entrySet()) {
				sb.append(String.format(optionStrFormat, optionEntry.getKey(), optionEntry.getValue().getDescription()));
			}
			break;
		}
		
		return sb.toString();
	}
	
	String formatParameterList(CommandLineConfiguration config, ParameterConfiguration paramConfig) {
		StringBuffer sb = new StringBuffer();
		for(ParameterMetadata metadata: paramConfig.values()) {
			sb.append(formatParameter(metadata));
		}
		return sb.toString();
	}
	
	String formatParameter(ParameterMetadata metadata) {
		String multipleSuffix = (metadata.isMultiValued())?"...":"";
		if(metadata.isRequired()) {
			return String.format("<%s>%s ", metadata.getIdentifier(), multipleSuffix);
		} else {
			return String.format("[%s]%s ", metadata.getIdentifier(), multipleSuffix);
		}
	}
	
	String formatOption(OptionMetadata metadata, CommandLineConfiguration config) {
		String formattedOption = null;
		if(metadata.isParameterAccepted()) {
			boolean isParamRequired = (metadata.getParameterMetadata()!=null && metadata.getParameterMetadata().isRequired());
			String paramDelim = StringUtil.formatDelimValue(config.getCommandLineProperties().getOptionParameterDelim());
			String paramUsageStr = "";
			if(metadata.getParameterMetadata()!=null && metadata.getParameterMetadata().getParameterType()!=ParameterType.NONE) {
				String paramName =  "";
				if(metadata.getParameterMetadata().getParameterType()==ParameterType.CUSTOM) {
					paramName = metadata.getParameterMetadata().getIdentifier();
				} else {
					paramName = metadata.getParameterMetadata().getParameterType().name();
				}
				paramUsageStr = String.format((isParamRequired)?"%s<%s>":"[%s<%s>]", paramDelim, paramName);
				if(metadata.isMultiValued()) {
					paramUsageStr += "...";
				}
			}
			String longIdentifier = metadata.getIdentifier(IdentifierType.LONG);
			if(longIdentifier!=null && !longIdentifier.isEmpty()) {
				formattedOption = String.format("%s%s, %s%s%s", 
						config.getCommandLineProperties().getOptionPrefix(), 
						metadata.getIdentifier(), 
						config.getCommandLineProperties().getOptionLongPrefix(), 
						metadata.getIdentifier(IdentifierType.LONG),
						paramUsageStr);
			} else {
				formattedOption = String.format("%s%s%s", 
						config.getCommandLineProperties().getOptionPrefix(), 
						metadata.getIdentifier(), 
						paramUsageStr);
			}
		} else {
			String longIdentifier = metadata.getIdentifier(IdentifierType.LONG);
			if(longIdentifier!=null && !longIdentifier.isEmpty()) {
				formattedOption = String.format("%s%s, %s%s", 
						config.getCommandLineProperties().getOptionPrefix(), 
						metadata.getIdentifier(), 
						config.getCommandLineProperties().getOptionLongPrefix(), 
						metadata.getIdentifier(IdentifierType.LONG));
			} else {
				formattedOption = String.format("%s%s", 
						config.getCommandLineProperties().getOptionPrefix(), 
						metadata.getIdentifier());
			}
		}
		return formattedOption;
	}
	
	String formatCommands(CommandLineConfiguration commandConfig) {
		
		List<String> commands = new ArrayList<String>();
		int maxCmdLength = 0;
		for(CommandMetadata metadata: commandConfig.getCommandConfiguration().values()) {
			commands.add(metadata.getIdentifier());
			maxCmdLength = Math.max(maxCmdLength, metadata.getIdentifier().length());
		}
		
		final String cmdStrFormat = "   %-" + (maxCmdLength + 5) + "s%s%n";
		StringBuffer sb = new StringBuffer();
		if(!commands.isEmpty()) {
			sb.append(String.format("Commands:%n"));
		}
		for(String cmdStr : commands) {
			sb.append(String.format(cmdStrFormat, cmdStr, commandConfig.getCommandMetadata(cmdStr).getDescription()));
		}
		return sb.toString();
	}
	
}
