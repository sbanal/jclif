/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jclif.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jclif.annotation.ParameterType;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterParser;

/**
 * ParameterParserFactory class is a factory of all supported parameter value parsers.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public final class ParameterParserFactory {

  public static final ParameterParser INTEGER_PARAMETER_PARSER = new ParameterParser() {
    @Override
    public Object parseValue(ParameterMetadata metadata, String value) {
      if (value == null) {
        return null;
      }
      return Integer.parseInt(value);
    }
  };

  public static final ParameterParser DIRECTORY_PARAMETER_PARSER = new ParameterParser() {
    @Override
    public Object parseValue(ParameterMetadata metadata, String value) {
      if (value == null) {
        return null;
      }
      File file = new File(value);
      if (!file.isDirectory()) {
        throw new IllegalArgumentException("Directory parameter value " + value
            + " is not a directory.");
      }
      return file;
    }
  };

  public static final ParameterParser FILE_PARAMETER_PARSER = new ParameterParser() {
    @Override
    public Object parseValue(ParameterMetadata metadata, String value) {
      if (value == null) {
        return null;
      }
      return new File(value);
    }
  };

  public static final ParameterParser BOOLEAN_PARAMETER_PARSER = new ParameterParser() {
    @Override
    public Object parseValue(ParameterMetadata metadata, String value) {
      return Boolean.parseBoolean(value);
    }
  };

  public static final ParameterParser NONE_PARAMETER_PARSER = new ParameterParser() {
    @Override
    public Object parseValue(ParameterMetadata metadata, String value) {
      return null;
    }
  };

  public static final ParameterParser STRING_PARAMETER_PARSER = new ParameterParser() {
    @Override
    public Object parseValue(ParameterMetadata metadata, String value) {
      return value;
    }
  };

  private static Map<ParameterType, ParameterParser> parameterParserMap =
      new HashMap<ParameterType, ParameterParser>();

  private static final ParameterParserFactory INSTANCE = new ParameterParserFactory();

  private ParameterParserFactory() {
    parameterParserMap.put(ParameterType.INTEGER, ParameterParserFactory.INTEGER_PARAMETER_PARSER);
    parameterParserMap.put(ParameterType.DIRECTORY,
        ParameterParserFactory.DIRECTORY_PARAMETER_PARSER);
    parameterParserMap.put(ParameterType.FILE, ParameterParserFactory.FILE_PARAMETER_PARSER);
    parameterParserMap.put(ParameterType.BOOLEAN, ParameterParserFactory.BOOLEAN_PARAMETER_PARSER);
    parameterParserMap.put(ParameterType.NONE, ParameterParserFactory.NONE_PARAMETER_PARSER);
    parameterParserMap.put(ParameterType.STRING, ParameterParserFactory.STRING_PARAMETER_PARSER);
  }

  public ParameterParser createParser(ParameterMetadata metadata) {
    ParameterParser parser = parameterParserMap.get(metadata.getParameterType());
    if (parser == null) {
      parser = metadata.getParameterValidator();
    }
    if (parser == null) {
      throw new IllegalArgumentException("No valid parameter parser found for metadata " + metadata);
    }
    return parser;
  }

  public static ParameterParserFactory getInstance() {
    return INSTANCE;
  }

}
