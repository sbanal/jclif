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
 * InputMetadata interface defines the common properties of all command line input type.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public interface InputMetadata {
	
	/**
	 * Returns the input identifier.
	 * 
	 * @return String option identifier
	 */
	public String getIdentifier();
	
	/**
	 * Returns the short description of a command line input. 
	 * @return String description 
	 */
	public String getDescription();
	
	/**
	 * Returns the long description of a command line input.
	 * @return String long description
	 */
	public String getLongDescription();
	
	/**
	 * Returns true if a command line input is multi-valued. Mutli-valued 
	 * inputs are those which can be specified multiple times in the
	 * command line. 
	 * 
	 * @return	boolean 	true if input is multi-valued, otherwise false
	 */
	public boolean isMultiValued();
	
	/**
	 * Returns true if a command line input is required.
	 * 
	 * @return boolean	true if input is required, otherwise returns false
	 */
	public boolean isRequired();
	
}
