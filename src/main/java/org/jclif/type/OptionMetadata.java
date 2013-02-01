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
 * OptionMetadata interface defines the common properties that 
 * describes a command line option.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public interface OptionMetadata extends InputMetadata {

	/**
	 * Identifier type.
	 * 
	 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
	 */
	public enum IdentifierType {
		
		/**
		 * Short identifier. Example the "-f" in command <code>copy -f "file.txt"</code>.
		 */
		SHORT,
		
		/**
		 * Long identifier. Example the "--file" in command <code>copy --file "file.txt"</code>.
		 */
		LONG
		
	};
	
	/**
	 * Returns the option key passed in command line depending on
	 * identifier format. Returned value can be either the long
	 * identifier value or the short identifier. For example,
	 * -f=file.txt versus --file=file.txt.
	 * 
	 * @return String option identifier
	 */
	public String getIdentifier(IdentifierType type);
	
	/**
	 * Returns the parameter metedata of an option.
	 * 
	 * @return ParameterMetadata	parameter metadata
	 */
	public ParameterMetadata getParameterMetadata();
	
	/**
	 * Returns true if an option accepts an input parameter.
	 * 
	 * @return true if parameter can be accepted, otherwise returns false
	 */
	public boolean isParameterAccepted();
	
	
}
