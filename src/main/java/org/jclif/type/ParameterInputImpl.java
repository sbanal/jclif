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

import org.jclif.annotation.ParameterType;

/**
 * This class is a concrete implementation of Parameter interface. 
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class ParameterInputImpl implements ParameterInput {

	private ParameterMetadata parameterMetadata;
	private Object value;
	
	public ParameterInputImpl(ParameterMetadata parameterMetadata, Object value) {
		this.parameterMetadata = parameterMetadata;
		this.value = value;
	}
	
	@Override
	public ParameterMetadata getMetadata() {
		return parameterMetadata;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public File getFileValue() {
		if(parameterMetadata!=null && parameterMetadata.getParameterType() == ParameterType.FILE) {
			return (File) value;
		}
		return null;
	}

	@Override
	public File getDirectoryValue() {
		if(parameterMetadata!=null && parameterMetadata.getParameterType() == ParameterType.DIRECTORY) {
			return (File) value;
		}
		return null;
	}

	@Override
	public String getStringValue() {
		if(parameterMetadata!=null && parameterMetadata.getParameterType() == ParameterType.STRING) {
			return (String) value;
		}
		return null;
	}

	@Override
	public Number getNumberValue() {
		if(parameterMetadata!=null && parameterMetadata.getParameterType() == ParameterType.INTEGER) {
			return (Number) value;
		}
		return null;
	}
	
	public String toString() {
		return String.format("ParameterImpl[%s, value=%s]", getMetadata(), getValue());
	}

}
