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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jclif.annotation.ParameterType;
import org.jclif.parser.CommandLineParser;
import org.jclif.parser.CommandLineParseResult;
import org.jclif.parser.InvalidInputException;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.CommandLineProperties;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterMetadataImpl;
import org.jclif.type.ParameterParser;
import org.junit.Assert;
import org.junit.Test;


public class CommandLineParseTest {
	
	public static void main(String[] args) {
		System.out.println("Main Input: " + Arrays.asList(args));
	}
	
	@Test
	public void testParseSimpleOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i").addOption("o", "output fule");
		String[] args = {"-i", "-o"};
		CommandLineParseResult parseResult = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("o"));
	}
	
	@Test
	public void testWindowsParseSimpleOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.setCommandLineProperties(CommandLineProperties.getSystemProperties("Windows"));
		config.getOptionConfiguration().addOption("i").addOption("o", "output file");
		String[] args = {"/i", "/o"};
		CommandLineParseResult parseResult = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("o"));
	}
	
	@Test
	public void testParseLongOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i", "input", ParameterType.FILE, false, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
	}
	
	@Test(expected=InvalidInputException.class)
	public void testParseWrongLongOptionWithInvalidPrefix() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i", "input", ParameterType.FILE, false, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"-input"};
		CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue( "expected to fail", false);
	}
	
	@Test
	public void testParseStringOptionParameterWithSpaceDelim() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getCommandLineProperties().setOptionParameterDelim(" ");
		config.getOptionConfiguration().addOption("i", "input", ParameterType.STRING, false, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input", "This is a test text"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertEquals("This is a test text", parseResult.getOptionInput().get("i").getParameter().getStringValue());
	}
	
	@Test
	public void testParseStringOptionParameterWithEqualDelim() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getCommandLineProperties().setOptionParameterDelim("=");
		config.getOptionConfiguration().addOption("i", "input", ParameterType.STRING, false, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input=\"This is a test text\""};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertEquals("This is a test text", parseResult.getOptionInput().get("i").getParameter().getStringValue());
	}
	
	@Test
	public void testParseStringOptionParameterWithEqualDelimQuoted() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getCommandLineProperties().setOptionParameterDelim("=");
		config.getOptionConfiguration().addOption("i", "input", ParameterType.STRING, false, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input=\"This is a test text\""};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertEquals("This is a test text", parseResult.getOptionInput().get("i").getParameter().getStringValue());
	}
	
	@Test
	public void testParseStringOptionParameterWithEqualDelimSingleQuoted() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getCommandLineProperties().setOptionParameterDelim("=");
		config.getOptionConfiguration().addOption("i", "input", ParameterType.STRING, false, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input='This is a test text'"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertEquals("This is a test text", parseResult.getOptionInput().get("i").getParameter().getStringValue());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParseMultiValuedOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i", "input", ParameterType.FILE, false, true, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input", "test.txt", "--input", "test2.txt"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertNotNull(parseResult.getOptionInput().get("i"));
		Assert.assertTrue(parseResult.getOptionInput().get("i").getParameter().getValue() instanceof List);
		List<?> fileList = (List<Object>) parseResult.getOptionInput().get("i").getParameter().getValue();
		Assert.assertEquals(2, fileList.size());
		Assert.assertTrue(fileList.get(0) instanceof File);
		Assert.assertTrue(fileList.get(1) instanceof File);
		Assert.assertEquals("test.txt", ((File)fileList.get(0)).getName());
		Assert.assertEquals("test2.txt", ((File)fileList.get(1)).getName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParseMultiValuedOptionWithEqualDelim() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getCommandLineProperties().setOptionParameterDelim("=");
		config.getOptionConfiguration().addOption("i", "input", ParameterType.FILE, false, true, "Sample desc", "A very long sample desc of the option");
		String[] args = {"--input=test.txt", "--input=test2.txt"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertNotNull(parseResult.getOptionInput().get("i"));
		Assert.assertTrue(parseResult.getOptionInput().get("i").getParameter().getValue() instanceof List);
		List<?> fileList = (List<Object>) parseResult.getOptionInput().get("i").getParameter().getValue();
		Assert.assertEquals(2, fileList.size());
		Assert.assertTrue(fileList.get(0) instanceof File);
		Assert.assertTrue(fileList.get(1) instanceof File);
		Assert.assertEquals("test.txt", ((File)fileList.get(0)).getName());
		Assert.assertEquals("test2.txt", ((File)fileList.get(1)).getName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParseMultiValuedWithSingleValuedOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i", "input", ParameterType.FILE, false, true, "Sample desc", "A very long sample desc of the option");
		config.getOptionConfiguration().addOption("x", "Testing single valued");
		String[] args = {"--input", "test.txt", "--input", "test2.txt", "-x"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertTrue(parseResult.getOptionInput().contains("x"));
		Assert.assertNotNull(parseResult.getOptionInput().get("i"));
		Assert.assertTrue(parseResult.getOptionInput().get("i").getParameter().getValue() instanceof List);
		List<?> fileList = (List<Object>) parseResult.getOptionInput().get("i").getParameter().getValue();
		Assert.assertEquals(2, fileList.size());
		Assert.assertTrue(fileList.get(0) instanceof File);
		Assert.assertTrue(fileList.get(1) instanceof File);
		Assert.assertEquals("test.txt", ((File)fileList.get(0)).getName());
		Assert.assertEquals("test2.txt", ((File)fileList.get(1)).getName());
	}
	
	@Test(expected = InvalidInputException.class)
	public void testParseMultiValuedWithMissingRequiredOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		ParameterMetadata paramMetadata = new ParameterMetadataImpl("input", 
				true, false, "", "",
				ParameterType.FILE, null);
		config.getOptionConfiguration().addOption("i", "input", paramMetadata, true, true, "Sample desc", "A very long sample desc of the option");
		config.getOptionConfiguration().addOption("x", "Testing single valued");
		String[] args = {"--input", "test.txt", "--input"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("input"));
		Assert.assertFalse(parseResult.getOptionInput().contains("x"));
		Assert.assertNotNull(parseResult.getOptionInput().get("i"));
		Assert.assertTrue(parseResult.getOptionInput().get("i").getParameter().getValue() instanceof List);
		@SuppressWarnings("unchecked")
		List<?> fileList = (List<Object>) parseResult.getOptionInput().get("i").getParameter().getValue();
		Assert.assertEquals(2, fileList.size());
		Assert.assertTrue(fileList.get(0) instanceof File);
		Assert.assertTrue(fileList.get(1) instanceof File);
		Assert.assertEquals("test.txt", ((File)fileList.get(0)).getName());
		Assert.assertEquals("test2.txt", ((File)fileList.get(1)).getName());
	}
	
	@Test
	public void testParseMultiValuedWithMissingOptionaldOption() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i", "input", ParameterType.FILE, false, true, "Sample desc", "A very long sample desc of the option");
		String[] args = {};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.getOptionInput().isEmpty());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testParseOptionWithCustomParameter() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		ParameterParser validator = new ParameterParser() {

			@Override
			public Object parseValue(ParameterMetadata option, String value) {
				String[] values = value.split(",");
				List<Integer> valueList = new ArrayList<Integer>();
				for(String token : values) {
					if(token==null) continue;
					valueList.add(Integer.parseInt(token));
				}
				return valueList;
			}

		};
		
		ParameterMetadata paramMetadata = new ParameterMetadataImpl("input", 
				true, false, "", "",
				ParameterType.CUSTOM, validator);
		config.getOptionConfiguration().addOption("i", "input", paramMetadata, true, false, "Sample desc", "A very long sample desc of the option");
		String[] args = {"-i", "1,2,3,4"};
		CommandLineParseResult parseResult  = CommandLineParser.getInstance().parse(config, args);
		Assert.assertFalse(parseResult.getOptionInput().isEmpty());
		Assert.assertTrue(parseResult.getOptionInput().get("i").getParameter().getValue() instanceof List);
		List valueList = (List) parseResult.getOptionInput().get("i").getParameter().getValue();
		for(Object obj : valueList) {
			Assert.assertTrue(obj instanceof Integer);
		}
		List<Integer> intList = (List<Integer>) parseResult.getOptionInput().get("i").getParameter().getValue();
		Set<Integer> intSet = new HashSet<Integer>(intList);
		Assert.assertEquals(4, intSet.size());
		Assert.assertTrue(intSet.contains(1));
		Assert.assertTrue(intSet.contains(2));
		Assert.assertTrue(intSet.contains(3));
		Assert.assertTrue(intSet.contains(4));
	}
	
	@Test
	public void testShortFormat() {
		CommandLineConfiguration config = new CommandLineConfiguration();
		config.getOptionConfiguration().addOption("i", "input file").addOption("o", "output file");
		String formatValue = CommandLineParser.getInstance().format(config);
		System.out.printf("ShortFormatValue%n%s", formatValue);
	}
	
	@Test
	public void testParseSimpleCommand() throws InvalidInputException {
		CommandLineConfiguration config = new CommandLineConfiguration();
		OptionConfiguration argConfig = new OptionConfiguration();
		ParameterMetadata param1 = new ParameterMetadataImpl("dir1", true, "dir 1");
		ParameterMetadata param2 = new ParameterMetadataImpl("dir2", false, "dir 2");
		argConfig.addOption("i", ParameterType.STRING, "").addOption("o", ParameterType.STRING, "output fule");
		config.getCommandConfiguration().addCommand("list", argConfig, "Shows list of files", param1, param2);
		String[] args = {"list", "-i", "input.txt", "-o", "output.txt", "directory1"};
		CommandLineParseResult parseResult = CommandLineParser.getInstance().parse(config, args);
		Assert.assertTrue(parseResult.isCommandMatch());
		Assert.assertNotNull(parseResult.getMatchingCommand());
		Assert.assertEquals("list", parseResult.getMatchingCommand().getIdentifier());
		Assert.assertTrue(parseResult.getOptionInput().contains("i"));
		Assert.assertTrue(parseResult.getOptionInput().contains("o"));
		Assert.assertEquals("input.txt", parseResult.getOptionInput().get("i").getParameter().getStringValue());
		Assert.assertEquals("output.txt", parseResult.getOptionInput().get("o").getParameter().getStringValue());
		Assert.assertEquals("directory1", parseResult.getParameterInput().get("dir1").getStringValue());
	}
	
}
