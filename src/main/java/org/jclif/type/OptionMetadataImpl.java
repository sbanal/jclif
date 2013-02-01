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
 * OptionMetadataImpl class provides the core implementation of OptionMetadata.
 * 
 * @author Stephen Lou Banal <stephen.banal@gmail.com>
 *
 */
public class OptionMetadataImpl extends InputMetadataImpl implements OptionMetadata {

	private String longIdentifier;
	private ParameterMetadata parameterMetadata;
	
	public OptionMetadataImpl(String identifier, ParameterMetadata parameterMetadata, 
			boolean required, boolean multiValued, String description) {
		this(identifier, null, parameterMetadata, required, multiValued, description, null);
	}
	
	public OptionMetadataImpl(String identifier, String longIdentifier, ParameterMetadata parameterMetadata, 
			boolean required, boolean multiValued, String description, String longDescription) {
		super(identifier, required, multiValued, description, longDescription);
		this.longIdentifier = (longIdentifier==null)?"":longIdentifier;
		this.parameterMetadata = parameterMetadata;
	}
	
	@Override
	public String getIdentifier(IdentifierFormat type) {
		if(type==OptionMetadata.IdentifierFormat.LONG) {
			return longIdentifier;
		}
		return super.getIdentifier();
	}

	@Override
	public boolean isParameterAccepted() {
		return (parameterMetadata!=null && parameterMetadata.getParameterType() != ParameterType.NONE);
	}	

	@Override
	public ParameterMetadata getParameterMetadata() {
		return parameterMetadata;
	}
	
	public String toString() {
		return String.format("OptionMetadataImpl[id=%s,lid=%s,desc=%s,ldesc=%s,required=%b,multi-valued=%b,parameter-accepted=%b,parameter=%s]", 
				getIdentifier(), getIdentifier(IdentifierFormat.LONG), this.getDescription(), 
				this.getLongDescription(), this.isRequired(), this.isMultiValued(), isParameterAccepted(), 
				(getParameterMetadata()==null)?"":getParameterMetadata());
	}

}
