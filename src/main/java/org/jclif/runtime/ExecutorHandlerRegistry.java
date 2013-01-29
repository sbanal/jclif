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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jclif.CommandLineParseResult;
import org.jclif.OptionConfiguration;
import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.type.CommandMetadata;
import org.jclif.type.InputMetadata;
import org.jclif.type.OptionMetadata;
import org.jclif.util.ReflectionUtil;

public class ExecutorHandlerRegistry {
	
	private static final Logger LOGGER = Logger.getLogger(ExecutorHandlerRegistry.class.getCanonicalName());
	
	private InputMetadata defaultMetadata;
	private Map<InputMetadata, ExecutorHandler> handlerRegistry = new HashMap<InputMetadata,ExecutorHandler>();
	
	static class ExecutorHandler {
		
		private Class<?> handlerClass;
		private Method handlerMethod;
		
		public ExecutorHandler(Class<?> handlerClass, Method handlerMethod) throws Exception {
			this.handlerClass = handlerClass;
			this.handlerMethod = handlerMethod;
		}
		
		public Class<?> getHandlerClass() {
			return handlerClass;
		}
		
		public Method getHandlerMethod() {
			return handlerMethod;
		}
		
		public void execute(CommandLineParseResult result) throws Exception {
			
			Object handlerInstance = handlerClass.newInstance();
			CommandMetadata cmdMetadata = (result.isCommandMatch())?(CommandMetadata) result.getMatchingCommand().getMetadata():null;
			OptionConfiguration optionConfig = null;
			
			if(cmdMetadata==null) {
				optionConfig = result.getConfiguration().getOptionConfiguration();
			} else {
				optionConfig = cmdMetadata.getOptionConfigurations();
			}
			
			for(Field field: handlerClass.getDeclaredFields()) {
			
				Object value = null;
				String fieldName = field.getName();
				LOGGER.info(String.format("processing field = " + field.getName()));
				if(field.isAnnotationPresent(Option.class)) {
					Option option = field.getAnnotation(Option.class);
					boolean optionExist = result.getOptionSet().contains(option.identifier());
						
					LOGGER.info(String.format("Setting field = " + field.getName() + " option specified"));
					OptionMetadata optMetadata = optionConfig.get(option.identifier());
					org.jclif.type.Option optionValue = result.getOptionSet().get(option.identifier());
					if(optionValue!=null) {
						if(optMetadata.isParameterAccepted()) {
							value = optionValue.getParameter().getValue();
						} else {
							value = true;
						}
					} else {
						LOGGER.info(String.format("Option " + field.getName() + " is not specified"));
						if(!optMetadata.isParameterAccepted()) {
							value = optionExist;
						}
					}
					
					LOGGER.info(String.format("Setting field = " + field.getName() + ", value = " +  value));
				} else if(field.isAnnotationPresent(Parameter.class)) {
					Parameter parameter = field.getAnnotation(Parameter.class);
					if(result.getParameterSet().contains(parameter.identifier())) {
						value = result.getParameterSet().get(parameter.identifier()).getValue();
					}
					LOGGER.info(String.format("Setting parameter =" + field.getName() + ", value = " +  value));
				}
				
				if(value!=null) {
					LOGGER.info(String.format("Calling set method for field " + fieldName + ", value = " +  value));
					Class<?> paramTypeClass = value.getClass();
					Method m = ReflectionUtil.getSetterMethod(handlerClass, fieldName, paramTypeClass);
					if(m==null) {
						throw new UnsupportedOperationException("Unable to execute setter method " + handlerClass.getCanonicalName() 
								+ " for field " + fieldName + " with param " + value.getClass().getCanonicalName() + "");
					} else {
						m.invoke(handlerInstance, value);
						LOGGER.info(String.format("method invoked " + m.getName() + ", value = " +  value));
					}
				}
				
			}
			getHandlerMethod().invoke(handlerInstance);
		}
			
	}
	
	public void add(InputMetadata metadata, Class<?> handlerClass, Method handlerMethod) throws Exception {
		handlerRegistry.put(metadata, new ExecutorHandler(handlerClass, handlerMethod));
		if(metadata.getIdentifier().equals("-default-")) {
			defaultMetadata = metadata;
		}
	}
	
	public ExecutorHandler getHandler(InputMetadata metadata) {
		return handlerRegistry.get(metadata);
	}
	
	public ExecutorHandler getDefaultHandler() {
		return getHandler(defaultMetadata);
	}
	
}
