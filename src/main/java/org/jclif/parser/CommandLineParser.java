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

import org.jclif.type.CommandLineConfiguration;


/**
 * CommandLineFormat class provides the parsing and formatting functions of command line inputs.
 * This is the main entry point for using JCLIF API. 
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public abstract class CommandLineParser {
	
	private static final CommandLineParser INSTANCE = new DefaultCommandLineParser();
	
	protected CommandLineParser() {
		
	}
	
	public static CommandLineParser getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Parses options provided in command line.
	 * 
	 * @param configuration	command line option metadata information
	 * @param args	option list passed in command line
	 * @return	OptionSet a set of options parsed
	 * @throws InvalidInputException	thrown if options passed does not match 
	 * 									with the option metadata configuration
	 */
	public abstract CommandLineParseResult parse(CommandLineConfiguration configuration, String... args) throws InvalidInputException;
	
	/**
	 * Checks if a given command line options matches the option metadata configuration.
	 * 
	 * @param configuration	command line option metadata information
	 * @param resultSet	 output set which contains the options parsed, if parsing succeeds
	 * @param args	option list passed in command line
	 * @return boolean true if it configuration matches options, otherwise false
	 */
	public abstract  boolean matches(CommandLineConfiguration configuration, CommandLineParseResult resultSet, String... args);
	
	public abstract String format(CommandLineConfiguration config, InvalidInputException e);
	
	public abstract String format(CommandLineConfiguration config, String errorMessage) ;
	
	public abstract String format(CommandLineConfiguration config);
	
	public abstract  String format(CommandLineConfiguration config, CommandLineFormatType formatType);
	
	
}
