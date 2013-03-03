package org.jclif.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import examples.DefaultCommand;
import examples.ListCommand;
import examples.MyClass;

public class ConfigurationTest {

	@Test
	public void testConfigurationString() {
		Configuration config  = new Configuration("myapp");
		Assert.assertEquals("myapp", config.getName());
	}

	@Test
	public void testLoad() throws IOException {
		Configuration config  = new Configuration();
		config.load();
		Assert.assertEquals("MyClass", config.getName());
		Assert.assertEquals(1, config.getHandlerCount());
		Assert.assertEquals("examples", config.getHandlerPackage());
	}

	@Test
	public void testLoadInputStream() throws Exception {
		Configuration config  = new Configuration();
		FileInputStream fileStream = new FileInputStream(new File("src/test/resources/jclif.properties"));
		config.load(fileStream);
		fileStream.close();
		Assert.assertEquals("MyClass", config.getName());
		Assert.assertEquals(1, config.getHandlerCount());
		Assert.assertEquals("examples", config.getHandlerPackage());
	}

	@Test
	public void testSetHandlerPackage() throws Exception {
		Configuration config  = new Configuration();
		config.load();
		
		Assert.assertEquals("examples", config.getHandlerPackage());
		
		config.setHandlerPackage("org.jclif");
		
		Assert.assertEquals("org.jclif", config.getHandlerPackage());
	}

	@Test
	public void testAddHandler() throws Exception {
		Configuration config  = new Configuration();
		config.load();
		
		Assert.assertEquals(1, config.getHandlerCount());
		
		config.addHandler(DefaultCommand.class);
		
		Assert.assertEquals(2, config.getHandlerCount());
		
		List<Class<?>> handlerList = config.getHandlerList();
		Assert.assertEquals(2, handlerList.size());
		Assert.assertEquals(ListCommand.class, handlerList.get(0));
		Assert.assertEquals(DefaultCommand.class, handlerList.get(1));
	}

	@Test
	public void testGetPackageHandlerList() throws IOException {
	
		Configuration config  = new Configuration();
		config.load();
		
		Assert.assertEquals("examples", config.getHandlerPackage());
		List<Class<?>> handlerList = config.getPackageHandlerList();
		Assert.assertEquals(3, handlerList.size());
		Assert.assertTrue(handlerList.contains(MyClass.class));
		Assert.assertTrue(handlerList.contains(ListCommand.class));
		Assert.assertTrue(handlerList.contains(DefaultCommand.class));
	
	}

}
