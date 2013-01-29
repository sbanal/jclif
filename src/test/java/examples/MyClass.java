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

package examples;

import java.io.File;
import java.util.List;

import org.jclif.CommandLineConfiguration;
import org.jclif.CommandLineFormat;
import org.jclif.CommandLineParseResult;
import org.jclif.InvalidInputException;
import org.jclif.annotation.Command;
import org.jclif.type.ParameterType;

@Command(identifier="",description="List files")
public class MyClass {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.addOption("i", "input", ParameterType.FILE, true, true, "Sample desc", "A very long sample desc of the option");
		config.addOption("x", "Testing single valued");
		try {
			CommandLineParseResult resultSet = CommandLineFormat.getInstance().parse(config, args);
			List<File> fileList = (List<File>) resultSet.getOptionSet().get("i").getParameter().getValue();
			// do your logic
			for(File file: fileList) {
				System.out.println("File:" + file.getName());
			}
		} catch (InvalidInputException e) {
			String usage = CommandLineFormat.getInstance().format(config, e);
			System.out.println(usage);
		}
	}

}
