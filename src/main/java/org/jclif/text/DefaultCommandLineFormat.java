package org.jclif.text;

import org.jclif.annotation.ParameterType;
import org.jclif.parser.InvalidInputException;
import org.jclif.type.*;
import org.jclif.type.OptionMetadata.IdentifierType;
import org.jclif.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DefaultCommandLineFormat extends CommandLineFormat {

  private static final Logger LOGGER = Logger.getLogger(DefaultCommandLineFormat.class.getName());

  DefaultCommandLineFormat() {

  }

  @Override
  public String format(CommandLineConfiguration config, InvalidInputException e) {
    String usage;
    if (e.isCommandError()) {
      usage = format(config, e.getCommandMetadata(), CommandLineFormatType.SHORT);
    } else {
      usage = format(config, CommandLineFormatType.SHORT);
    }
    return String.format("Error: %s%n%s", e.getMessage(), usage);
  }

  @Override
  public String format(CommandLineConfiguration config, String errorMessage) {
    return String.format("Error: %s%n%s", errorMessage, format(config));
  }

  @Override
  public String format(CommandLineConfiguration config) {
    return format(config, CommandLineFormatType.SHORT);
  }

  @Override
  public String format(CommandLineConfiguration config, CommandMetadata commandMetadata,
      CommandLineFormatType formatType) {

    List<OptionMetadata> optionMetaDataList =
        commandMetadata.getOptionConfigurations().getOptions();
    Map<String, OptionMetadata> optionFormatMap = new LinkedHashMap<String, OptionMetadata>();
    Integer maxIdLength = 0;
    for (OptionMetadata metadata : optionMetaDataList) {
      String optionFormatStr = formatOption(metadata, config);
      maxIdLength = Math.max(optionFormatStr.length(), maxIdLength);
      optionFormatMap.put(optionFormatStr, metadata);
    }

    String parameterFormatList = formatParameterList(commandMetadata.getParameterConfigurations());
    StringBuilder sb = new StringBuilder();
    if (!commandMetadata.getDescription().isEmpty()) {
      sb.append(String.format("Description: %s%n", commandMetadata.getDescription()));
    }
    sb.append(String.format("Usage:    %s %s [options] %s%n", config.getName(),
        commandMetadata.getIdentifier(), parameterFormatList));
    sb.append(formatOptionList(config, commandMetadata.getOptionConfigurations(), formatType));

    return sb.toString();
  }

  @Override
  public String format(CommandLineConfiguration config, CommandLineFormatType formatType) {

    String parameterFormatList = formatParameterList(config.getParameterConfiguration());
    StringBuilder sb = new StringBuilder();
    if (!config.getDescription().isEmpty()) {
      sb.append(String.format("Description: %s%n", config.getDescription()));
    }

    sb.append("Usage:");
    boolean defaultCommandExist =
        !config.getOptionConfiguration().isEmpty() || !config.getParameterConfiguration().isEmpty();
    if (defaultCommandExist) {
      sb.append(String.format("  %s [options] %s%n", config.getName(), parameterFormatList));
    }
    if (defaultCommandExist && !config.getCommandConfiguration().isEmpty()) {
      sb.append(String.format("   or   %s [command] [options] parameters...%n", config.getName()));
    } else {
      sb.append(String.format("  %s [command] [options] parameters...%n", config.getName()));
    }

    sb.append(formatOptionList(config, config.getOptionConfiguration(), formatType));
    sb.append(formatCommands(config));

    return sb.toString();
  }

  String formatOptionList(CommandLineConfiguration config, OptionConfiguration optionConfig,
      CommandLineFormatType formatType) {

    StringBuilder sb = new StringBuilder();
    List<OptionMetadata> optionMetaDataList = optionConfig.getOptions();
    Map<String, OptionMetadata> optionFormatMap = new LinkedHashMap<String, OptionMetadata>();
    Integer maxIdLength = 0;
    for (OptionMetadata metadata : optionMetaDataList) {
      String optionFormatStr = formatOption(metadata, config);
      LOGGER.info("formatOption: " + optionFormatStr + "-->" + metadata);
      maxIdLength = Math.max(optionFormatStr.length(), maxIdLength);
      optionFormatMap.put(optionFormatStr, metadata);
    }

    final String optionStrFormat = "    %-" + (maxIdLength + 5) + "s%s%n";

    if (!optionFormatMap.isEmpty()) {
      sb.append(String.format("Options:%n"));
    }

    switch (formatType) {
      case FULL:
        appendFullFormattedOptions(optionFormatMap, optionStrFormat, sb);
        break;
      case SHORT:
      default:
        appendShortFormattedOptions(optionFormatMap, optionStrFormat, sb);
        break;
    }

    return sb.toString();
  }

  void appendFullFormattedOptions(Map<String, OptionMetadata> optionFormatMap,
      String optionStrFormat, StringBuilder sb) {
    for (Map.Entry<String, OptionMetadata> optionEntry : optionFormatMap.entrySet()) {
      OptionMetadata metadata = optionEntry.getValue();
      String desc =
          (metadata.getLongDescription().isEmpty()) ? metadata.getDescription() : metadata
              .getLongDescription();
      sb.append(String.format(optionStrFormat, optionEntry.getKey(), desc));
    }
  }

  void appendShortFormattedOptions(Map<String, OptionMetadata> optionFormatMap,
      String optionStrFormat, StringBuilder sb) {
    for (Map.Entry<String, OptionMetadata> optionEntry : optionFormatMap.entrySet()) {
      sb.append(String.format(optionStrFormat, optionEntry.getKey(), optionEntry.getValue()
          .getDescription()));
    }
  }

  String formatParameterList(ParameterConfiguration paramConfig) {
    StringBuilder sb = new StringBuilder();
    for (ParameterMetadata metadata : paramConfig.values()) {
      sb.append(formatParameter(metadata));
    }
    return sb.toString();
  }

  String formatParameter(ParameterMetadata metadata) {
    String multipleSuffix = (metadata.isMultiValued()) ? "..." : "";
    if (metadata.isRequired()) {
      return String.format("<%s>%s ", metadata.getIdentifier(), multipleSuffix);
    } else {
      return String.format("[%s]%s ", metadata.getIdentifier(), multipleSuffix);
    }
  }

  String formatOption(OptionMetadata metadata, CommandLineConfiguration config) {
    if (metadata.isParameterAccepted()) {
      return formatOptionWithParameter(metadata, config);
    } else {
      return formatOptionWithoutParameter(metadata, config);
    }
  }

  private String formatParameterUsageString(OptionMetadata metadata, CommandLineConfiguration config) {
    boolean isParamRequired =
        metadata.getParameterMetadata() != null && metadata.getParameterMetadata().isRequired();
    String paramDelim =
        StringUtil.formatDelimValue(config.getCommandLineProperties().getOptionParameterDelim());
    String paramUsageStr = "";
    if (metadata.getParameterMetadata() != null
        && metadata.getParameterMetadata().getParameterType() != ParameterType.NONE) {
      String paramName = "";
      if (metadata.getParameterMetadata().getParameterType() == ParameterType.CUSTOM) {
        paramName = metadata.getParameterMetadata().getIdentifier();
      } else {
        paramName = metadata.getParameterMetadata().getParameterType().name();
      }
      paramUsageStr =
          String.format((isParamRequired) ? "%s<%s>" : "[%s<%s>]", paramDelim, paramName);
      if (metadata.isMultiValued()) {
        paramUsageStr += "...";
      }
    }
    return paramUsageStr;
  }

  private String formatOptionWithParameter(OptionMetadata metadata, CommandLineConfiguration config) {
    String paramUsageStr = formatParameterUsageString(metadata, config);
    String longIdentifier = metadata.getIdentifier(IdentifierType.LONG);
    if (longIdentifier != null && !longIdentifier.isEmpty()) {
      return String.format("%s%s, %s%s%s", config.getCommandLineProperties().getOptionPrefix(),
          metadata.getIdentifier(), config.getCommandLineProperties().getOptionLongPrefix(),
          metadata.getIdentifier(IdentifierType.LONG), paramUsageStr);
    } else {
      return String.format("%s%s%s", config.getCommandLineProperties().getOptionPrefix(),
          metadata.getIdentifier(), paramUsageStr);
    }
  }

  private String formatOptionWithoutParameter(OptionMetadata metadata,
      CommandLineConfiguration config) {
    String longIdentifier = metadata.getIdentifier(IdentifierType.LONG);
    if (longIdentifier != null && !longIdentifier.isEmpty()) {
      return String.format("%s%s, %s%s", config.getCommandLineProperties().getOptionPrefix(),
          metadata.getIdentifier(), config.getCommandLineProperties().getOptionLongPrefix(),
          metadata.getIdentifier(IdentifierType.LONG));
    } else {
      return String.format("%s%s", config.getCommandLineProperties().getOptionPrefix(),
          metadata.getIdentifier());
    }
  }

  String formatCommands(CommandLineConfiguration commandConfig) {

    List<String> commands = new ArrayList<String>();
    int maxCmdLength = 0;
    for (CommandMetadata metadata : commandConfig.getCommandConfiguration().values()) {
      commands.add(metadata.getIdentifier());
      maxCmdLength = Math.max(maxCmdLength, metadata.getIdentifier().length());
    }

    final String cmdStrFormat = "   %-" + (maxCmdLength + 5) + "s%s%n";
    StringBuilder sb = new StringBuilder();
    if (!commands.isEmpty()) {
      sb.append(String.format("Commands:%n"));
    }
    for (String cmdStr : commands) {
      sb.append(String.format(cmdStrFormat, cmdStr, commandConfig.getCommandMetadata(cmdStr)
          .getDescription()));
    }
    return sb.toString();
  }

}
