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

import java.io.File;

/**
 * Parameter interface defines the common methods of a parameter.
 * A parameter represents the parameter value parsed from a command line input.
 * A parameter can be either as an argument to an option or as an input to a command.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public interface ParameterInput extends Input {
	
	/**
	 * Returns the metadata of this parameter.
	 * @return ParameterMetadata	metadata
	 */
	@Override
	public ParameterMetadata getMetadata();
	
	/**
	 * Returns the parameter value of an option.
	 *  
	 * @return Object option parameter value
	 */
	public Object getValue();
	
	/**
	 * Returns the File parameter value of an option.
	 * If option's parameter type does not match with the
	 * return type the value returned is null.
	 *  
	 * @return File file value
	 */
	public File getFileValue();
	
	/**
	 * Returns the directory parameter value of an option.
	 * If option's parameter type does not match with the
	 * return type the value returned is null.
	 *  
	 * @return File a directory file
	 */	
	public File getDirectoryValue();

	/**
	 * Returns the String parameter value of an option.
	 * If option's parameter type does not match with the
	 * return type the value returned is null.
	 *  
	 * @return String string value
	 */
	public String getStringValue();
	
	/**
	 * Returns the Number parameter value of an option.
	 * If option's parameter type does not match with the
	 * return type the value returned is null.
	 *  
	 * @return Number value
	 */
	public Number getNumberValue();
	
}
