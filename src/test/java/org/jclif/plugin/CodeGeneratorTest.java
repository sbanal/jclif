package org.jclif.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.jclif.runtime.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class CodeGeneratorTest {

	@Test
	public void testCodeGenerator() {
		CodeGenerator codeGen = new CodeGenerator("app1", "src/main/java");
		Assert.assertEquals(codeGen.getAppName(), "app1");
		Assert.assertEquals(codeGen.getSourceMainDirectory(), "src/main/java");
	}

	@Test
	public void testGetAppName() throws IOException, ClassNotFoundException {
		
		CodeGenerator codeGen = new CodeGenerator("app1", "src/test/java");
		codeGen.generateMain("examples", "target/classes");
		File file = new File("target/classes/jclif.properties");
		
		Assert.assertTrue(file.exists());
		
		Configuration prop = new Configuration("appname");
		prop.load(new FileInputStream(file));
		prop.setHandlerPackage("");
		List<Class<?>> handlerList = prop.getHandlerList();
		
		Assert.assertTrue(handlerList.contains(Class.forName("examples.DefaultCommand")));
		Assert.assertTrue(handlerList.contains(Class.forName("examples.ListCommand")));
		
		file.delete();
	}
	
}
