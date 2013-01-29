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

package org.jclif;

import org.jclif.type.Option;
import org.jclif.type.OptionMetadata;
import org.jclif.type.Parameter;


/**
 * Option class represents an instance of an option parsed from the 
 * command line option list parsed by CommandLineFormat parse method.
 * 
 * @author Stephen Lou Banal <stephen.banal@gmail.com>
 *
 */
public class OptionImpl implements Option {

	private OptionMetadata option;
	private Parameter parameter;
	
	public OptionImpl(OptionMetadata option, Parameter parameter) {
		this.option = option;
		this.parameter = parameter;
	}
	
	@Override
	public OptionMetadata getMetadata() {
		return option;
	}
	
	@Override
	public Parameter getParameter() {
		return parameter;
	}
	
	public String toString() {
		return String.format("OptionImpl[option=%s,parameter=%s]", option, parameter);
	}

}
