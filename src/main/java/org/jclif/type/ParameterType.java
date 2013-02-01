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
 * ParameterType enum defines the different option parameter types
 * supported. Parameter type basically represents the value provided with
 * the option in a command line.
 * <p>
 * Example:
 * <code>
 * java -jar myapp.jar -f "file.txt" -d ";"
 * </code>
 * <p>
 * In example above, the options are -f and -d while the parameters
 * are "file.txt" and ";" respectively. Types could be defined as 
 * ParameterType.FILE for "file.txt" while ParameterType.STRING for ";".
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public enum ParameterType {
	
	/**
	 * Parameter value is a string.
	 */
	STRING,
	
	/**
	 * Parameter value is an integer.
	 */
	INTEGER,
	
	/**
	 * Parameter value is a File.
	 */
	FILE,
	
	/**
	 * Parameter value is an existing directory.
	 */
	DIRECTORY,
	
	/**
	 * Parameter value is either true or false
	 */
	BOOLEAN,
	
	/**
	 * Parameter value is a custom value.
	 */
	CUSTOM,

	/**
	 * Parameter value is not used.
	 */
	NONE;
	
	/**
	 * Converts a class type to ParameterType value.
	 * 
	 * @param classType			class type of an object 
	 * @return ParameterType	parameter type equivalent of class type
	 */
	public static ParameterType toParamType(Class<?> classType) {
		if(classType == Boolean.class) {
			return ParameterType.BOOLEAN;
		} else if(classType == String.class) {
			return ParameterType.STRING;
		} else if(classType == File.class) {
			return ParameterType.FILE;
		} else if(classType == File.class) {
			return ParameterType.FILE;
		} else if(classType == Integer.class) {
			return ParameterType.INTEGER;
		} else {
			return ParameterType.CUSTOM;
		}
	}
	
};
