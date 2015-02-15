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
 * OptionSet class is a HashSet of Option type. This class
 * serves as container of all the options parsed from command line input.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class OptionInputSet extends InputSet<OptionInput, OptionMetadata> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2671453277762411033L;
	
	private OptionConfiguration configuration;
	
	public OptionInputSet(OptionConfiguration config) {
		this.configuration = config;
	}
	
	@Override
	public boolean contains(String identifier) {
		OptionMetadata optionMetadata = configuration.getOption(identifier);
		return optionMetadata!=null && super.contains(optionMetadata.getIdentifier());
	}
	
	@Override
	public OptionInput get(String identifier) {
		OptionMetadata optionMetadata = configuration.getOption(identifier);
		if(optionMetadata!=null) {
			return super.get(optionMetadata);
		}
		return null;
	}

}
