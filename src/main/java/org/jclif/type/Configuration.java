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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class is a collection class used to store command line input
 * configuration metadata information. Class is a LinkedHashMap<String, InputMetadata>
 * type and ensures that only one metadata exist in a collection. Class also ensures
 * that the metadeta identifiers are valid and does not exist in the collection.
 * 
 * Any metadata added to the collection which has an invalid identifier or currently
 * exist in the collection will trigger a InvalidIdentifierException from being thrown.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public abstract class Configuration<T extends InputMetadata> extends LinkedHashMap<String, T> {

	private static final Logger LOGGER = Logger.getLogger(Configuration.class.getCanonicalName());
	
	private final Pattern IDENTIFIER_REGEX = Pattern.compile("^([\\w]+)$");
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7031525779156792585L;
	
	private String id;
	private String description = "";
	
	protected Configuration(String id, String desc) {
		this.id = id;
		this.description = desc;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	public T add(T metadata) {
		LOGGER.info("Add identifier: " + metadata.getIdentifier() + ", metadata-desc:" + metadata.getDescription());
		validateIdentifier(metadata.getIdentifier(), true);
		if (null != get(metadata.getIdentifier())) {
			throw new InvalidIdentifierException(this.getId() + " identifer '" + metadata.getIdentifier() + "' already exist.");
		}
		return super.put(metadata.getIdentifier(), metadata);
	}
	
	public boolean addAll(Collection<T> metadataList) {
		for(T metadata: metadataList) {
			add(metadata);
		}
		return !metadataList.isEmpty();
	}
	
	public T put(String identifier, T metadata) {
		throw new UnsupportedOperationException("Operation not supported, use T add(T value) instead.");
	}
	
	public void putAll(Map<? extends String,? extends T> m) {
		throw new UnsupportedOperationException("Operation not supported, use boolean addAll(Collection<T> metadataList) instead.");
	}

	public T get(T metadata) {
		return get(metadata.getIdentifier());
	}
	
	public boolean contains(T metadata) {
		return containsKey(metadata.getIdentifier());
	}
	
	public boolean contains(String id) {
		return containsKey(id);
	}
	
	public T remove(T metadata) {
		return remove(metadata.getIdentifier());
	}
	
	void validateIdentifier(String identifier, boolean required) {
		if(!required && (identifier==null || identifier.isEmpty())) { 
			return;
		}
		if(identifier==null || identifier.isEmpty()) {
			throw new InvalidIdentifierException("Identifier invalid, value is empty or null.");
		}
		if(!IDENTIFIER_REGEX.matcher(identifier).matches()) {
			throw new InvalidIdentifierException("Identifier " + identifier + " is not valid. "
					+ "Only characters A-Z,a-z,0-9 and _ is allowed (in regex \\w) .");
		}
	}
	
}
