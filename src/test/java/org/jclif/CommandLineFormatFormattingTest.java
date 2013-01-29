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

package org.jclif;

import org.jclif.CommandLineConfiguration;
import org.jclif.CommandLineFormat;
import org.jclif.CommandLineFormatType;
import org.jclif.InvalidInputException;
import org.jclif.OptionConfiguration;
import org.jclif.ParameterMetadataImpl;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterType;
import org.junit.Test;


public class CommandLineFormatFormattingTest {

	@Test
	public void testShortFormatWithError() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("i", "input file").addOption("o", "output file");
		String formatValue = CommandLineFormat.getInstance().format(config, "Some very short error message.");
		System.out.printf("ShortFormatValue%n%s", formatValue);
	}
	
	@Test
	public void testLongFormat() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("i", "input", ParameterType.FILE, false, false, "This is a short desc 1", "This is a very very very very long description 1")
			.addOption("o", "output", ParameterType.FILE, false, false, "This is a short desc 2", "This is a very very very very long description 2");
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.FULL);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testLongFormatWithParameter() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("i", "input", ParameterType.FILE, false, false, "This is a short desc 1", "This is a very very very very long description 1")
			.addOption("o", "output", ParameterType.FILE, false, false, "This is a short desc 2", "This is a very very very very long description 2");
		config.addParameter("dir1", true, false, "Directory 1");
		config.addParameter("dir2", true, false, "Directory 2");
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.FULL);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testLongFormatWithMultiValuedParameter() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("i", "input", ParameterType.FILE, false, false, "This is a short desc 1", "This is a very very very very long description 1")
			.addOption("o", "output", ParameterType.FILE, false, false, "This is a short desc 2", "This is a very very very very long description 2");
		config.addParameter("dir1", true, false, "Directory 1");
		config.addParameter("dir2", true, true, "Directory 2");
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.FULL);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testLongFormatWithOptionalMultiValuedParameter() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("i", "input", ParameterType.FILE, false, false, "This is a short desc 1", "This is a very very very very long description 1")
			.addOption("o", "output", ParameterType.FILE, false, false, "This is a short desc 2", "This is a very very very very long description 2");
		config.addParameter("dir1", true, false, "Directory 1");
		config.addParameter("dir2", false, true, "Directory 2");
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.FULL);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testLongFormatWithException() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("i", "input", ParameterType.FILE, false, false, "This is a short desc 1", "This is a very very very very long description 1")
			.addOption("o", "output", ParameterType.FILE, false, false, "This is a short desc 2", "This is a very very very very long description 2");
		String formatValue = CommandLineFormat.getInstance().format(config, new InvalidInputException("Option -i is missing"));
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testShortLongFormat() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("a", "Some option named a").addOption("b", "Some option named b")
			.addOption("i", "input", ParameterType.FILE, false, false, "This is a short desc 1", "This is a very very very very long description 1")
			.addOption("o", "output", ParameterType.FILE, false, false, "This is a short desc 2", "This is a very very very very long description 2");
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.FULL);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testCommandShortFormat() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		OptionConfiguration argConfig = new OptionConfiguration();
		ParameterMetadata param1 = new ParameterMetadataImpl("dir1", true, "dir 1");
		ParameterMetadata param2 = new ParameterMetadataImpl("dir2", false, "dir 2");
		argConfig.addOption("i", ParameterType.STRING, "").addOption("o", ParameterType.STRING, "output fule");
		config.addCommand("list", argConfig, "List all modified files", param1, param2);
		config.addCommand("pull", argConfig, "Pull files from remote", param1, param2);
		config.addCommand("push", argConfig, "Push files to remote", param1, param2);
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.SHORT);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
	@Test
	public void testFullFormat() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setName("mysampleapp");
		config.addOption("a", "All files").addOption("d", "delim", ParameterType.STRING, true, false, "Delimitter value", "");
		config.addParameter("sourcedirectory", true, "Source directory blah blah blah");
		config.addParameter("targetdirectory", false, "Target directory blah blah blah");
		OptionConfiguration argConfig = new OptionConfiguration();
		ParameterMetadata param1 = new ParameterMetadataImpl("dir1", true, "dir 1");
		ParameterMetadata param2 = new ParameterMetadataImpl("dir2", false, "dir 2");
		argConfig.addOption("i", ParameterType.STRING, "").addOption("o", ParameterType.STRING, "output fule");
		config.addCommand("list", argConfig, "List all modified files", param1, param2);
		config.addCommand("pull", argConfig, "Pull files from remote", param1, param2);
		config.addCommand("push", argConfig, "Push files to remote", param1, param2);
		String formatValue = CommandLineFormat.getInstance().format(config, CommandLineFormatType.SHORT);
		System.out.printf("LongFormatValue:%n%s", formatValue);
	}
	
}
