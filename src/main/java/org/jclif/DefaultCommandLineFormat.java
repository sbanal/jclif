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

package org.jclif;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jclif.parser.ParameterParser;
import org.jclif.parser.ParameterParserFactory;
import org.jclif.type.CommandMetadata;
import org.jclif.type.Option;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionSet;
import org.jclif.type.Parameter;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterSet;
import org.jclif.type.ParameterType;
import org.jclif.type.OptionMetadata.IdentifierFormat;
import org.jclif.util.StringUtil;



class DefaultCommandLineFormat extends CommandLineFormat {

	private final static Logger LOGGER = Logger.getLogger(DefaultCommandLineFormat.class.getName());
	
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
			parseOptions(scanner, null, configuration.getOptionConfiguration(), resultSet.getOptionSet());
			parseParameters(scanner, null, configuration.getParameterConfiguration(), resultSet.getParameterSet());
		}
		scanner.close();
		
		resultSet.validate();
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
		
		Pattern commandsParam = Pattern.compile(String.format("^(%s)", sbOptionFlags.toString()));
		String token = scanner.findInLine(commandsParam);
		if(token==null) {
			return false;
		}
		
		MatchResult result = scanner.match();
		String cmdIdentifier = result.group(1);
		CommandMetadata cmdMetadata = configuration.getCommandMetadata(cmdIdentifier);
		resultSet.setMatchingCommand(new CommandImpl(cmdIdentifier, cmdMetadata));
		
		LOGGER.info(String.format("Token=%s%n", token));
		LOGGER.info(String.format("Command=%s%n", scanner));
		
		String paramsToken = scanner.findInLine("[ \t]+(.*)");
		if(paramsToken!=null) {
			MatchResult paramResult = scanner.match();
			LOGGER.info(String.format("Options=%s%n", paramResult.group(1)));
			Scanner scannerOptions = new Scanner(paramResult.group(1));
			parseOptions(scannerOptions, cmdMetadata, cmdMetadata.getOptionConfigurations(), resultSet.getOptionSet());
			parseParameters(scannerOptions, cmdMetadata, cmdMetadata.getParameterConfigurations(), resultSet.getParameterSet());
			scannerOptions.close();
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean parseOptions(Scanner scanner, CommandMetadata cmdMetadata, OptionConfiguration configuration, 
			OptionSet resultSet) throws InvalidInputException {
		
		if(configuration.getOptions().isEmpty()) {
			return true;
		}
		
		// Build the regex of all possible identifiers
		StringBuffer sbOptionFlags = new StringBuffer("(");
		for(OptionMetadata optionMetadata : configuration.getOptions()) {
			sbOptionFlags.append(String.format("%s%s", configuration.getOptionPrefix(), optionMetadata.getIdentifier()));
			String longIdentifier = optionMetadata.getIdentifier(IdentifierFormat.LONG);
			if(longIdentifier!=null && !longIdentifier.isEmpty()) {
				sbOptionFlags.append(String.format("|%s%s", configuration.getOptionLongPrefix(), longIdentifier));
			}
			sbOptionFlags.append("|");
		}
		sbOptionFlags.setCharAt(sbOptionFlags.length()-1, ')');
		LOGGER.info(String.format("OptionsFlags regex={%s}", sbOptionFlags.toString()));
		
		// parse options
		String token = null;		
		Pattern optionsParam = Pattern.compile(sbOptionFlags.toString());
		while ((token = scanner.findInLine(optionsParam)) != null) {

			MatchResult result = scanner.match();
			LOGGER.info(String.format("Options={%s}", token));
			for (int i = 0; i <= result.groupCount(); i++) {
				LOGGER.info(String.format("Options Group[%d]={%s}", i, result.group(i)));
			}

			// parse option identifier extract option identifier and get its metadata
			String paramIdentifierStr = result.group(1);
			String paramId = paramIdentifierStr.startsWith(configuration.getOptionLongPrefix()) ? paramIdentifierStr.substring(2) : paramIdentifierStr.substring(1);
			OptionMetadata metadata = configuration.getOption(paramId);
			if (metadata == null) {
				throw new InvalidInputException("Option " + paramIdentifierStr + " is not found.", cmdMetadata);
			}

			// parse the parameter
			Object parameterValue = null;
			token = scanner.findInLine(configuration.getParameterDelimitter());
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
						LOGGER.info(String.format("[%d] Delim={%s}, Param={%s}", i, configuration.getParameterDelimitter(), paramResult.group(i)));
					}
					String groupValue = StringUtil.extractQuotedValue(paramResult.group(1));
					LOGGER.info(String.format("Delim={%s}, Param={%s}", configuration.getParameterDelimitter(), groupValue));
					parameterValue = getParameterValue(cmdMetadata, parameterMetadata, groupValue);
				}
				
			}

			// create the parameter value
			Parameter parameter = null;
			Option optionValue = resultSet.get(paramId);
			if (optionValue == null) {
				if (metadata.isParameterAccepted()) {
					if (metadata.isMultiValued()) {
						List<Object> valueList = new ArrayList<Object>();
						valueList.add(parameterValue);
						parameter = new ParameterImpl(metadata.getParameterMetadata(), valueList);
					} else {
						parameter = new ParameterImpl(metadata.getParameterMetadata(), parameterValue);
					}
				}
				optionValue = new OptionImpl(metadata, parameter);
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
	
	private boolean parseParameters(Scanner scanner, CommandMetadata cmdMetadata, ParameterConfiguration parameterConfig, ParameterSet resultSet) throws InvalidInputException {
		
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
				resultSet.add(new ParameterImpl(paramMeta, valueList));
			} else {
				if(scanner.hasNext()) {
					String token = scanner.next();
					LOGGER.info(String.format("Parameter={%s}", token));
					paraValue = getParameterValue(cmdMetadata, paramMeta, token);
					resultSet.add(new ParameterImpl(paramMeta, paraValue));
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
			resultSet.getOptionSet().addAll(result.getOptionSet());
			resultSet.getParameterSet().addAll(result.getParameterSet());
			return true;
		} catch (InvalidInputException e) {
			return false;
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
		sb.append(String.format("Usage:    %s [options] %s%n", config.getName(), parameterFormatList));
		if(!config.getCommandConfiguration().isEmpty()) {
			sb.append(String.format("      or  %s [command] [options] parameters...%n", config.getName()));
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
			String paramDelim = StringUtil.formatDelimValue(config.getOptionConfiguration().getParameterDelimitter());
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
			String longIdentifier = metadata.getIdentifier(IdentifierFormat.LONG);
			if(longIdentifier!=null && !longIdentifier.isEmpty()) {
				formattedOption = String.format("%s%s, %s%s%s", 
						config.getOptionConfiguration().getOptionPrefix(), 
						metadata.getIdentifier(), 
						config.getOptionConfiguration().getOptionLongPrefix(), 
						metadata.getIdentifier(IdentifierFormat.LONG),
						paramUsageStr);
			} else {
				formattedOption = String.format("%s%s%s", 
						config.getOptionConfiguration().getOptionPrefix(), 
						metadata.getIdentifier(), 
						paramUsageStr);
			}
		} else {
			formattedOption = String.format("%s%s", 
					config.getOptionConfiguration().getOptionPrefix(),  
					metadata.getIdentifier());
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
