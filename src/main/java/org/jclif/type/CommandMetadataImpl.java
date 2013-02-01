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

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides concrete implementation of CommandMetadata interface.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class CommandMetadataImpl extends InputMetadataImpl implements CommandMetadata {

	private OptionConfiguration optionConfiguration;
	private ParameterConfiguration parameterConfiguration = new ParameterConfiguration();
	
	public CommandMetadataImpl(String keyword, OptionConfiguration optionConfiguration, ParameterConfiguration parameterConfiguration, 
			String description, String longDescription) {
		this(keyword,
				optionConfiguration, 
				(parameterConfiguration!=null)?new ArrayList<ParameterMetadata>(parameterConfiguration.values()):null, 
				description, longDescription);
	}
	
	public CommandMetadataImpl(String keyword, OptionConfiguration optionConfiguration, List<ParameterMetadata> parameters, 
			String description, String longDescription) {
		super(keyword, false, false, description, longDescription);
		this.optionConfiguration  = optionConfiguration;	
		if(parameters!=null && !parameters.isEmpty()) {
			this.parameterConfiguration.addAll(parameters);
		}
	}
	
	public CommandMetadataImpl(String keyword, OptionConfiguration optionConfiguration, String description, String longDescription) {
		this(keyword, optionConfiguration, (ParameterConfiguration) null, description, longDescription);
	}
	
	public CommandMetadataImpl(String keyword, String description, String longDescription) {
		this(keyword, new OptionConfiguration(), (ParameterConfiguration) null, description, longDescription);
	}
	
	@Override
	public String getKeyword() {
		return this.getIdentifier();
	}
	
	@Override
	public OptionConfiguration getOptionConfigurations() {
		return optionConfiguration;
	}
	
	@Override
	public ParameterConfiguration getParameterConfigurations() {
		return parameterConfiguration;
	}

	@Override
	public boolean isMultiValued() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

}
