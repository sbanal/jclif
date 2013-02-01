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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterParser;
import org.jclif.type.ParameterType;

/**
 * ParameterParserFactory class is a factory of all supported parameter value parsers.
 *   
 * @author stephen
 *
 */
public final class ParameterParserFactory {
	
	private static final ParameterParserFactory INSTANCE = new ParameterParserFactory();
	
	private Map<ParameterType, ParameterParser> parserMap = new HashMap<ParameterType, ParameterParser>();
	
	private ParameterParserFactory() {
		parserMap.put(ParameterType.INTEGER,
				new ParameterParser(){
					@Override
					public Object parseValue(ParameterMetadata metadata, String value) {
						if(value==null) {
							return null;
						}
						return Integer.parseInt(value);
					}
				}
		);
		parserMap.put(ParameterType.DIRECTORY,
				new ParameterParser(){
					@Override
					public Object parseValue(ParameterMetadata metadata, String value) {
						if(value==null) {
							return null;
						}
						File file = new File(value);
						if(!file.isDirectory()) {
							throw new IllegalArgumentException("Directory parameter value " + value + " is not a directory.");
						}
						return file;
					}
				}
		);
		parserMap.put(ParameterType.FILE,
				new ParameterParser(){
					@Override
					public Object parseValue(ParameterMetadata metadata, String value) {
						if(value==null) {
							return null;
						}
						return new File(value);
					}
				}
		);
		parserMap.put(ParameterType.BOOLEAN,
				new ParameterParser(){
					@Override
					public Object parseValue(ParameterMetadata metadata, String value) {
						return Boolean.parseBoolean(value);
					}
				}
		);
		parserMap.put(ParameterType.NONE,
				new ParameterParser(){
					@Override
					public Object parseValue(ParameterMetadata metadata, String value) {
						return null;
					}
				}
		);
		parserMap.put(ParameterType.STRING,
				new ParameterParser(){
					@Override
					public Object parseValue(ParameterMetadata metadata, String value) {
						return value;
					}
				}
		);
		
	}
	
	public static ParameterParserFactory getInstance() {
		return INSTANCE;
	}
	
	public ParameterParser createParser(ParameterMetadata metadata) {
		ParameterParser parser = this.parserMap.get(metadata.getParameterType());
		if(parser==null) {
			parser = metadata.getParameterValidator();
		}
		if(parser==null) {
			throw new IllegalArgumentException("No valid parameter parser found for metadata " + metadata);
		}
		return parser;
	}
	
}
