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

import org.jclif.annotation.ParameterType;



/**
 * This interfaces defines the properties which describes a command
 * line parameter.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public interface ParameterMetadata extends InputMetadata {
	
	/**
	 * Returns the type of the parameter value.
	 * 
	 * @return ParameterType parameter type
	 */
	public ParameterType getParameterType();
	
	/**
	 * Returns the parameter validator used for parsing and validating custom parameter values.
	 * 
	 * @return ParameterValidator parameter validator of a metadata
	 */
	public ParameterParser getParameterValidator();
	
}
