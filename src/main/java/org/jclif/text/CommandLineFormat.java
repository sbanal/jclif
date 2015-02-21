package org.jclif.text;

import org.jclif.parser.InvalidInputException;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandMetadata;

public abstract class CommandLineFormat {

  private static final CommandLineFormat INSTANCE = new DefaultCommandLineFormat();

  protected CommandLineFormat() {

  }

  public static CommandLineFormat getInstance() {
    return INSTANCE;
  }

  public abstract String format(CommandLineConfiguration config, CommandMetadata metadata,
      CommandLineFormatType formatType);

  public abstract String format(CommandLineConfiguration config, InvalidInputException e);

  public abstract String format(CommandLineConfiguration config, String errorMessage);

  public abstract String format(CommandLineConfiguration config);

  public abstract String format(CommandLineConfiguration config, CommandLineFormatType formatType);

}
