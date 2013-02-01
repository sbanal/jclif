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

import org.jclif.parser.CommandLineConfiguration;
import org.jclif.parser.InvalidIdentifierException;
import org.jclif.type.ParameterType;
import org.junit.Assert;
import org.junit.Test;

public class CommandLineConfigurationTest {

	@Test
	public void testSetName() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "");
		Assert.assertEquals("app1", conf.getName());
		conf.setName("app2");
		Assert.assertEquals("app2", conf.getName());
	}

	@Test
	public void testGetDescription() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		Assert.assertEquals("testdesc", conf.getDescription());
	}

	@Test
	public void testGetOptionConfiguration() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		Assert.assertNotNull(conf.getOptionConfiguration());
	}

	@Test
	public void testGetParameterConfiguraiton() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		Assert.assertNotNull(conf.getParameterConfiguration());
	}

	@Test
	public void testGetCommandConfiguration() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		Assert.assertNotNull(conf.getCommandConfiguration());
	}

	@Test
	public void testAddOptionString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
		conf.addOption(id);
		assertDefaultOptionProperties(conf, id);
	}
	
	void assertDefaultOptionProperties(CommandLineConfiguration conf, String identifier) {
		Assert.assertNotNull(conf.getOption(identifier));
		Assert.assertEquals("", conf.getOption(identifier).getDescription());
		Assert.assertEquals(true, conf.getOption(identifier).isRequired());
		Assert.assertEquals(false, conf.getOption(identifier).isMultiValued());
		Assert.assertEquals(false, conf.getOption(identifier).isParameterAccepted());
		Assert.assertNull(conf.getOption(identifier).getParameterMetadata());
	}
	
	@Test(expected=InvalidIdentifierException.class)
	public void testAddOptionStringInvalid1() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		conf.addOption("te st1-");
	}
	
	@Test(expected=InvalidIdentifierException.class)
	public void testAddOptionStringInvalid2() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		conf.addOption("tes+*()t20");
	}

	@Test
	public void testAddOptionStringString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		conf.addOption("test", "");
		assertDefaultOptionProperties(conf, "test");
	}

	@Test
	public void testAddOptionStringBooleanString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addOption( id, false, "desc");
		Assert.assertNotNull(conf.getOption(id));
		Assert.assertEquals("desc", conf.getOption(id).getDescription());
		Assert.assertEquals(false, conf.getOption(id).isRequired());
		Assert.assertEquals(false, conf.getOption(id).isMultiValued());
		Assert.assertEquals(false, conf.getOption(id).isParameterAccepted());
		Assert.assertNull(conf.getOption(id).getParameterMetadata());
	}
	
	@Test
	public void testAddOptionStringBooleanString2() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addOption( id, true, "desc");
		Assert.assertNotNull(conf.getOption(id));
		Assert.assertEquals("desc", conf.getOption(id).getDescription());
		Assert.assertEquals(true, conf.getOption(id).isRequired());
		Assert.assertEquals(false, conf.getOption(id).isMultiValued());
		Assert.assertEquals(false, conf.getOption(id).isParameterAccepted());
		Assert.assertNull(conf.getOption(id).getParameterMetadata());
	}

	@Test
	public void testAddOptionStringBooleanBooleanString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addOption( id, true, false, "desc");
		Assert.assertNotNull(conf.getOption(id));
		Assert.assertEquals("desc", conf.getOption(id).getDescription());
		Assert.assertEquals(true, conf.getOption(id).isRequired());
		Assert.assertEquals(false, conf.getOption(id).isMultiValued());
		Assert.assertEquals(false, conf.getOption(id).isParameterAccepted());
		Assert.assertNull(conf.getOption(id).getParameterMetadata());
	}
	
	@Test
	public void testAddOptionStringBooleanBooleanString2() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addOption( id, false, true, "desc");
		Assert.assertNotNull(conf.getOption(id));
		Assert.assertEquals("desc", conf.getOption(id).getDescription());
		Assert.assertEquals(false, conf.getOption(id).isRequired());
		Assert.assertEquals(true, conf.getOption(id).isMultiValued());
		Assert.assertEquals(false, conf.getOption(id).isParameterAccepted());
		Assert.assertNull(conf.getOption(id).getParameterMetadata());
	}


	@Test
	public void testAddParameterStringBooleanString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addParameter(id, false, "desc");
		Assert.assertNotNull(conf.getParameterConfiguration().contains(id));
		Assert.assertEquals("desc", conf.getParameterConfiguration().get(id).getDescription());
		Assert.assertEquals(false, conf.getParameterConfiguration().get(id).isRequired());
		Assert.assertEquals(false, conf.getParameterConfiguration().get(id).isMultiValued());
		Assert.assertEquals(ParameterType.STRING, conf.getParameterConfiguration().get(id).getParameterType());
	}

	@Test
	public void testAddParameterStringBooleanBooleanString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addParameter(id, false, false, "desc");
		Assert.assertNotNull(conf.getParameterConfiguration().contains(id));
		Assert.assertEquals("desc", conf.getParameterConfiguration().get(id).getDescription());
		Assert.assertEquals(false, conf.getParameterConfiguration().get(id).isRequired());
		Assert.assertEquals(false, conf.getParameterConfiguration().get(id).isMultiValued());
		Assert.assertEquals(ParameterType.STRING, conf.getParameterConfiguration().get(id).getParameterType());
	}

	@Test
	public void testAddParameterStringBooleanBooleanString2() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addParameter(id, true, false, "desc");
		Assert.assertNotNull(conf.getParameterConfiguration().contains(id));
		Assert.assertEquals("desc", conf.getParameterConfiguration().get(id).getDescription());
		Assert.assertEquals(true, conf.getParameterConfiguration().get(id).isRequired());
		Assert.assertEquals(false, conf.getParameterConfiguration().get(id).isMultiValued());
		Assert.assertEquals(ParameterType.STRING, conf.getParameterConfiguration().get(id).getParameterType());
	}

	@Test
	public void testAddParameterStringBooleanBooleanString3() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addParameter(id, true, true, "desc");
		Assert.assertNotNull(id, conf.getParameterConfiguration().get(id).getIdentifier());
		Assert.assertEquals("desc", conf.getParameterConfiguration().get(id).getDescription());
		Assert.assertEquals(true, conf.getParameterConfiguration().get(id).isRequired());
		Assert.assertEquals(true, conf.getParameterConfiguration().get(id).isMultiValued());
		Assert.assertEquals(ParameterType.STRING, conf.getParameterConfiguration().get(id).getParameterType());
	}
	
	@Test
	public void testAddCommandStringOptionConfigurationString() {
		CommandLineConfiguration conf = new CommandLineConfiguration("app1", "testdesc");
		String id = "test1";
		conf.addCommand(id, null, "desc");
		Assert.assertNotNull(id, conf.getCommandConfiguration().get(id).getIdentifier());
		Assert.assertEquals("desc", conf.getCommandConfiguration().get(id).getDescription());
		Assert.assertEquals(false, conf.getCommandConfiguration().get(id).isRequired());
		Assert.assertEquals(false, conf.getCommandConfiguration().get(id).isMultiValued());
		Assert.assertNull(conf.getCommandConfiguration().get(id).getOptionConfigurations());
		Assert.assertTrue(conf.getCommandConfiguration().get(id).getParameterConfigurations().isEmpty());
	}


}
