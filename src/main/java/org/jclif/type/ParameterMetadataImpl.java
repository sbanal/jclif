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

package org.jclif.type;

import org.jclif.annotation.Parameter;
import org.jclif.annotation.ParameterType;

/**
 * This class provides concrete implementation of ParameterMetadata interface.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class ParameterMetadataImpl extends InputMetadataImpl implements ParameterMetadata {

  private ParameterType parameterType;
  private ParameterParser parameterValidator;

  public ParameterMetadataImpl(Parameter parameterAnnotation) {
    this(parameterAnnotation.identifier(), parameterAnnotation.required(), parameterAnnotation
        .multiValued(), parameterAnnotation.type(), parameterAnnotation.description(),
        parameterAnnotation.longDescription());
  }

  public ParameterMetadataImpl(String identifier, ParameterType parameterType) {
    this(identifier, false, false, parameterType, null);
  }

  public ParameterMetadataImpl(String identifier, boolean required, String description) {
    this(identifier, required, false, description, null, ParameterType.STRING, null);
  }

  public ParameterMetadataImpl(String identifier, boolean required, boolean multiValued,
      String description) {
    this(identifier, required, multiValued, description, null, ParameterType.STRING, null);
  }

  public ParameterMetadataImpl(String identifier, boolean required, boolean multiValued,
      ParameterType type, String description) {
    this(identifier, required, multiValued, description, null, type, null);
  }

  public ParameterMetadataImpl(String identifier, boolean required, boolean multiValued,
      ParameterType type, String description, String longDescription) {
    this(identifier, required, multiValued, description, longDescription, type, null);
  }

  public ParameterMetadataImpl(String identifier, boolean required, boolean multiValued,
      String description, String longDescription) {
    this(identifier, required, multiValued, description, longDescription, ParameterType.STRING,
        null);
  }

  public ParameterMetadataImpl(String identifier, boolean required, boolean multiValued,
      String description, String longDescription, ParameterType parameterType,
      ParameterParser parameterValidator) {
    super(identifier, required, multiValued, description, longDescription);
    this.parameterType = parameterType;
    this.parameterValidator = parameterValidator;
  }

  @Override
  public ParameterType getParameterType() {
    return this.parameterType;
  }

  @Override
  public ParameterParser getParameterValidator() {
    return parameterValidator;
  }

  @Override
  public String toString() {
    return String
        .format(
            "ParameterMetadataImpl[id=%s,desc=%s,ldesc=%s,type=%s,required=%b,multi-valued=%b,validator=%s]",
            getIdentifier(), this.getDescription(), this.getLongDescription(),
            this.getParameterType(), this.isRequired(), this.isMultiValued(),
            this.getParameterValidator());
  }

}
