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

package org.jclif.parser;

import org.jclif.type.CommandMetadata;


/**
 * InvalidInputException class is an exception thrown if a command line input is invalid.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class InvalidInputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 528304270306604990L;
	
	private CommandMetadata commandMetadata;

	public InvalidInputException(String msg) {
		this(msg, (CommandMetadata) null);
	}
	
	public InvalidInputException(String msg, CommandMetadata metadata) {
		super(msg);
		this.commandMetadata = metadata;
	}
	
	public InvalidInputException(String msg, Throwable t) {
		this(msg, t, (CommandMetadata) null);
	}
	
	public InvalidInputException(String msg, Throwable t, CommandMetadata metadata) {
		super(msg, t);
		this.commandMetadata = metadata;
	}
	
	public CommandMetadata getCommandMetadata() {
		return this.commandMetadata;
	}
	
	public boolean isCommandError() {
		return (null!=this.commandMetadata);
	}
	
}
