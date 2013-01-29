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

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class CommandLinePatternScannerTest {

	@Test 
	public void testQuotedTextRegex() {
		Pattern p = Pattern.compile("[\"']{1}([\\p{Alnum}\\p{Punct}\\p{Space}]+)[\"']{1}");
		Matcher m = p.matcher("\"a simple phrase\"");
		Assert.assertTrue(m.matches());
		Assert.assertEquals("\"a simple phrase\"", m.group(0));
		Assert.assertEquals("a simple phrase", m.group(1));
	}
	
	@Test 
	public void testPhraseWithStringRegEx() {
		Assert.assertTrue(" a simple phrase".matches("(.*)[\\s]+(.*)"));
	}
	
	@Test
	public void testScanner() {
		
		String cmd = "-f test.txt --debug=DEBUG --file \"some file.txt\" -parameter1 parameter2";
		Scanner scanner = new Scanner(cmd);
		testScan(scanner, cmd);
		scanner.close();
		
		String cmd2 = "pull -f test.txt -d=DEBUG parameter1 parameter2";
		scanner = new Scanner(cmd2);
		testScan(scanner, cmd);
		scanner.close();
		
	}
	
	private void testScan(Scanner scanner, String cmd) {
		
		System.out.println("Scan: " + cmd);
		
		Pattern commandsParam = Pattern.compile("^(pull|push|commit)[ ]+(.*)");
		String token = null;		
		
		// parse commands
		if((token = scanner.findInLine(commandsParam))!=null) {
			MatchResult result = scanner.match();
			System.out.printf("Token=%s%n", token);
			System.out.printf("Command=%s%n", result.group(1));
			System.out.printf("Options=%s%n", result.group(2));
			Scanner scannerOptions = new Scanner(result.group(2));
			parseOptions(scannerOptions, true);
			scannerOptions.close();
			return;
		} else {
			parseOptions(scanner, true);
		}
		
	}
	
	private void parseOptions(Scanner scanner, boolean withParams) {
		
		Pattern optionsParam = Pattern.compile("(-f|-d|--file|--debug)[ =]+([A-Za-z.]+|\"[A-Za-z. ]\")[ ]+");
		String token = null;		
			
		// parse options with required parameters
		while((token = scanner.findInLine(optionsParam))!=null) {
			try {
				System.out.printf("Options={%s}%n", token);
				MatchResult result = scanner.match();
				for(int i = 0; i <= result.groupCount(); i++) {
					System.out.printf("Options Group[%d]={%s}%n", i, result.group(i));		
				}
			} catch(NoSuchElementException e) {
				e.printStackTrace();
			}
		}
		
		// parse parameters
		while(withParams && scanner.hasNext()) {
			System.out.printf("Parameter={%s}%n", scanner.next());
		}
	
	}
	
	@Test
	public void testAsciiWithSpaceRegex() {
		Assert.assertTrue(Pattern.matches("[\\p{Alnum}\\p{Punct}\\p{Space}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/ \t\r\n\f"));
		Assert.assertTrue(Pattern.matches("[\\p{ASCII}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/ \t\r\n\f"));
	}
	
	@Test
	public void testAsciiNoSpaceRegex() {
		Assert.assertTrue(Pattern.matches("[\\p{Alnum}\\p{Punct}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/"));
		Assert.assertFalse(Pattern.matches("[\\p{Alnum}\\p{Punct}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/\t"));
		Assert.assertFalse(Pattern.matches("[\\p{Alnum}\\p{Punct}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/\n"));
		Assert.assertFalse(Pattern.matches("[\\p{Alnum}\\p{Punct}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/\r"));
		Assert.assertFalse(Pattern.matches("[\\p{Alnum}\\p{Punct}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/\f"));
		Assert.assertFalse(Pattern.matches("[\\p{Alnum}\\p{Punct}]+", "ABCDEFGHIJKLMNOPQRSTUVQXYZ0123456789~!@#$%^&*()_+=-`:\"';<>,.?/ "));
	}
	
}
