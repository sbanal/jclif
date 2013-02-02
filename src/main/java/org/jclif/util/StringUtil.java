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

package org.jclif.util;

import java.util.regex.Pattern;

/**
 * This class provides all the utility methods used to manipulate or check string values.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public final class StringUtil {

	private StringUtil(){}
	
	private static final Pattern SPACE_REGEX = Pattern.compile("(.*)[\\s]+(.*)");
	public static boolean containsSpace(String str) {
		return SPACE_REGEX.matcher(str).matches();
	}
	
	public static String formatDelimValue(String delim) {
		String delimFormat = delim;
		if(delim.equals("\t")) {
			delimFormat = "<tab>";
		} else if(delim.equals(" ")) {
			delimFormat = "<space>";
		}
		return delimFormat;
	}
	
	public static String extractQuotedValue(String value) {
		if(value!=null && value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() -1);
		} else if(value!=null && value.startsWith("'") && value.endsWith("'")) {
			return value.substring(1, value.length() -1);
		} else {
			return value;
		}
	}
	
	public static String pathToClassName(String path) {
		if(path.endsWith(".class")) {
			return path.substring(0, path.length() - 6).replace("/", ".");
		} else {
			return path.replace("/", ".");
		}
	}
	
	public static String sourceNameToClassName(String path) {
		if(path.endsWith(".java")) {
			return path.substring(0, path.length() - 5).replace("/", ".");
		} else {
			return path.replace("/", ".");
		}
	}
	
}
