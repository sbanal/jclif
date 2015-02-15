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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class is a collection which serves as container of all input types
 * parsed from a command line input. Collection enforces that each input
 * collected is unique based on the identifier of the input.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class InputSet<E extends Input, M extends InputMetadata> extends HashSet<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6882340659646375846L;
	private Map<String, E> inputMap = new HashMap<String, E>();
	
	
	@Override
	public boolean add(E obj) {
		inputMap.put(obj.getMetadata().getIdentifier(), obj);
		return super.add(obj);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> collection) {
		for(E arg: collection) {
			add(arg);
		}
		return !collection.isEmpty();
	}
	
	public boolean contains(M metadata) {
		return metadata!=null && inputMap.containsKey(metadata.getIdentifier());
	}
	
	public boolean contains(String identifier) {
		return inputMap.containsKey(identifier);
	}
	
	public E get(String indentifier) {
		return inputMap.get(indentifier);
	}
	
	public E get(M metadata) {
		return inputMap.get(metadata.getIdentifier());
	}
	
	@Override
	public void clear() {
		inputMap.clear();
	}

}
