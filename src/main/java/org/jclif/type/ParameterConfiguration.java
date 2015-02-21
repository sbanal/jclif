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

/**
 * ParameterConfiguration class serves as a container of all parameters accepted by a command line
 * input configuration.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class ParameterConfiguration extends Configuration<ParameterMetadata> {

  /**
	 * 
	 */
  private static final long serialVersionUID = 4680033761366612505L;

  public ParameterConfiguration() {
    super("parameter", "Parameter configuration");
  }

  public ParameterConfiguration addParameter(ParameterMetadata metadata) {
    add(metadata);
    return this;
  }

  public ParameterConfiguration addParameter(String identifier, boolean required, String description) {
    ParameterMetadata param = new ParameterMetadataImpl(identifier, required, description);
    addParameter(param);
    return this;
  }

  public ParameterConfiguration addParameter(String identifier, boolean required,
      boolean multiValued, String description) {
    ParameterMetadata param =
        new ParameterMetadataImpl(identifier, required, multiValued, description);
    addParameter(param);
    return this;
  }

}
