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

import java.util.HashSet;
import java.util.Set;

import org.jclif.ParameterConfiguration;


public class ParameterSet extends InputSet<Parameter, ParameterMetadata> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8323112069381380752L;
	
	private ParameterConfiguration parameterConfig = new ParameterConfiguration();
	
	public ParameterSet(ParameterConfiguration parameterConfig) {
		this.parameterConfig.addAll(parameterConfig.values());
	}
	
	public Set<ParameterMetadata> getParameterList() {
		return new HashSet<ParameterMetadata>(parameterConfig.values());
	}
	
}
