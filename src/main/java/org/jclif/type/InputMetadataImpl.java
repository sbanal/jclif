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
 * This class provides concrete implementation of InputMetadata interface.
 * 
 * @author stephen
 *
 */
public abstract class InputMetadataImpl implements InputMetadata {
	
	private String identifier;
	private boolean multiValued;
	private boolean required;
	private String description;
	private String longDescription;
	
	protected InputMetadataImpl(String identifier, boolean required, boolean multiValued, 
			String description, String longDescription) {
		this.identifier = identifier;
		this.multiValued = multiValued;
		this.required = required;
		this.description = description;
		this.longDescription = (longDescription==null)?description:longDescription;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLongDescription() {
		return longDescription;
	}

	@Override
	public boolean isMultiValued() {
		return multiValued;
	}
	
	@Override
	public boolean isRequired() {
		return required;
	}
	
	public int hashCode() {
		return 31*identifier.hashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof OptionMetadataImpl)) {
			return false;
		}
		return (((OptionMetadataImpl) obj).getIdentifier().equals(getIdentifier()));
	}
	
	public String toString() {
		return String.format("AbstractInpt[id=%s,desc=%s,ldesc=%s,required=%b,multi-valued=%b]", 
				getIdentifier(), this.getDescription(), this.getLongDescription(), 
				this.isRequired(), this.isMultiValued());
	}
	
}
