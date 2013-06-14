package org.jclif.text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jclif.annotation.ParameterType;
import org.jclif.parser.InvalidInputException;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandMetadata;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionMetadata.IdentifierType;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterMetadata;
import org.jclif.util.StringUtil;

public class DefaultCommandLineFormat extends CommandLineFormat {
	
	private final static Logger LOGGER = Logger.getLogger(DefaultCommandLineFormat.class.getName());
	
	DefaultCommandLineFormat() {
		
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
