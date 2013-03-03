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

package org.jclif.runtime;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jclif.annotation.Command;
import org.jclif.type.CommandMetadata;
import org.jclif.type.InputMetadata;

/**
 * This class is used by Executor class as container of all command handlers detected at runtime.
 * 
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class ExecutorHandlerRegistry {
	
	private static final Logger LOGGER = Logger.getLogger(ExecutorHandlerRegistry.class.getCanonicalName());
	
	private InputMetadata defaultMetadata;
	private Map<InputMetadata, ExecutorHandler> handlerRegistry = new HashMap<InputMetadata,ExecutorHandler>();

	/**
	 * Registers a handler to the registry identified by its metadata information.
	 * 
	 * @param metadata		metadata of input
	 * @param handlerClass	handler class
	 * @param handlerMethod	handler method annotated by Handler annotatin
	 */
	public void add(CommandMetadata metadata, Class<?> handlerClass, Method handlerMethod) {
		add(new ExecutorHandler(metadata, handlerClass, handlerMethod));
	}
	
	/**
	 * Adds an executor handler.
	 * 
	 * @param handler handler instance
	 */
	public void add(ExecutorHandler handler) {
		handlerRegistry.put(handler.getMetadata(), handler);
		LOGGER.info("Added handler for " + handler.getMetadata());
		if(handler.getMetadata().getIdentifier().equals(Command.DEFAULT_COMMAND_IDENTIFIER)) {
			defaultMetadata = handler.getMetadata();
		}
	}
	
	/**
	 * Returns the handler of a metadata.
	 * 
	 * @param metadata	metadata of handler
	 * @return ExecutorHandler 	handler			
	 */
	public ExecutorHandler getHandler(InputMetadata metadata) {
		return handlerRegistry.get(metadata);
	}
	
	/**
	 * Returns the default handler.
	 * 
	 * @return ExecutorHandler 	handler
	 */
	public ExecutorHandler getDefaultHandler() {
		return getHandler(defaultMetadata);
	}
	
}
