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

import java.util.Arrays;

/**
 * This class is collection class used to register all the commands supported by 
 * a command line application. This class is backed by a LinkedHashMap<String,CommandMetadata>
 * and requires that only one instance of the same CommandMetadata exist in a collection.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class CommandConfiguration extends Configuration<CommandMetadata> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7791557861511435195L;

	public CommandConfiguration() {
		super("command", "Command configuration");		
	}

	public CommandConfiguration addCommand(CommandMetadata metadata) {
		add(metadata);
		return this;
	}
	
	public CommandConfiguration addCommand(String keyword, OptionConfiguration optionConfiguration, String description) {
		CommandMetadata metadata = new CommandMetadataImpl( keyword, optionConfiguration, description, null);
		addCommand(metadata);
		return this;
	}
	
	public CommandConfiguration addCommand(String keyword, OptionConfiguration optionConfiguration, String description, ParameterMetadata... parameters) {
		CommandMetadata metadata = new CommandMetadataImpl( keyword, optionConfiguration, Arrays.asList(parameters), description, null);
		addCommand(metadata);
		return this;
	}
	
}
