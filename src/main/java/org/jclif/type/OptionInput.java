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
 * Option class represents the option parsed from a command line input.
 * An Option class may or may not have a corresponding parameter depending on 
 * if an options accepts a parameter or not.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public interface OptionInput extends Input {
	
	/**
	 * Returns the option meta data information of an option.
	 * 
	 * @return OptionMetadata metadata of option
	 */
	@Override
	public OptionMetadata getMetadata();
	
	/**
	 * Returns parameter of an option.
	 * 
	 * @return Parameter parameter
	 */
	public ParameterInput getParameter();
	
}
