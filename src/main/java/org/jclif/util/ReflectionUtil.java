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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This class provides all the utility methods used by the framework to discover class methods and fields
 * using reflection API of java.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public final class ReflectionUtil {

	private ReflectionUtil() {
		
	}
	
	/**
	 * Returns true if a method is a non-static public method with no arguments.
	 * 
	 * @param method	Method to check
	 * @return boolean	true if method is a non-static public method with no arguments, otherwise false
	 */
	public static boolean isPublicNoArgMethod(Method method) {
		return Modifier.isPublic(method.getModifiers())  
				&& !Modifier.isStatic(method.getModifiers())
				&& method.getParameterTypes().length == 0;
	}
	
	public static Method getSetterMethod(Class<?> classType, String fieldName, Class<?> paramType) 
			throws NoSuchMethodException {
		
		String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method m = null;
		NoSuchMethodException firstException = null;
		
		while(paramType!=null && m==null) {
			try {
				m = classType.getDeclaredMethod( methodName, paramType);
			} catch (NoSuchMethodException e) {
				if(firstException==null) {
					firstException = e;
				}
				for(Class<?> interfaceClass : paramType.getInterfaces()) {
					try {
						m = classType.getDeclaredMethod( methodName, interfaceClass);
					} catch (NoSuchMethodException e1) {
						
					}
				}
				paramType = paramType.getSuperclass();
			}
			
		}
		
		if(m==null && firstException!=null) {
			throw firstException;
		}
		
		return m;
	}
}
