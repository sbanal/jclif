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

/**
 * This class serves as container of command line related properties used for parsing
 * like option prefix and option-parameter character delimiter.
 *  
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class CommandLineProperties {
	
	public final static String DEFAULT_OPTION_PARAMETER_DELIM = " ";
	
	public final static String DEFAULT_UNIX_OPTION_PREFIX = "-";
	public final static String DEFAULT_UNIX_OPTION_LONG_PREFIX = "--";
	
	public final static String DEFAULT_WIN_OPTION_PREFIX = "/";
	public final static String DEFAULT_WIN_OPTION_LONG_PREFIX = "/";
	
	
	public static final CommandLineProperties WINDOWS_COMMAND_LINE_PROPERTIES  = new CommandLineProperties(
			DEFAULT_WIN_OPTION_PREFIX,
			DEFAULT_WIN_OPTION_LONG_PREFIX, 
			DEFAULT_OPTION_PARAMETER_DELIM);
	public static final CommandLineProperties UNIX_COMMAND_LINE_PROPERTIES  = new CommandLineProperties(
			DEFAULT_UNIX_OPTION_PREFIX,
			DEFAULT_UNIX_OPTION_LONG_PREFIX, 
			DEFAULT_OPTION_PARAMETER_DELIM);	
	
	private String optionPrefix;
	private String optionLongPrefix;
	private String optionParameterDelim;
	
	
	public CommandLineProperties(String optionPrefix,
			String optionLongPrefix,
			String optionParameterDelim) {
		this.optionPrefix = optionPrefix;
		this.optionLongPrefix = optionLongPrefix;
		this.optionParameterDelim = optionParameterDelim;
	}


	public String getOptionPrefix() {
		return optionPrefix;
	}


	public void setOptionPrefix(String optionPrefix) {
		this.optionPrefix = optionPrefix;
	}


	public String getOptionLongPrefix() {
		return optionLongPrefix;
	}


	public void setOptionLongPrefix(String optionLongPrefix) {
		this.optionLongPrefix = optionLongPrefix;
	}


	public String getOptionParameterDelim() {
		return optionParameterDelim;
	}


	public void setOptionParameterDelim(String optionParameterDelim) {
		this.optionParameterDelim = optionParameterDelim;
	}
	
	
}
