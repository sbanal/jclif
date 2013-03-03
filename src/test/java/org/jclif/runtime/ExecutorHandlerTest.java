package org.jclif.runtime;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.jclif.annotation.Command;
import org.jclif.parser.CommandLineParseResult;
import org.jclif.type.CommandLineConfiguration;
import org.jclif.type.OptionInput;
import org.jclif.type.OptionInputImpl;
import org.jclif.type.OptionInputSet;
import org.jclif.type.ParameterInput;
import org.jclif.type.ParameterInputImpl;
import org.jclif.type.ParameterInputSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest( { CommandLineParseResult.class, ExecutorHandler.class, Handler1.class })
public class ExecutorHandlerTest {
	
	@Test
	public void testExecutorHandlerClassOfQ() throws SecurityException, NoSuchMethodException {
		ExecutorHandler handler = new ExecutorHandler(Handler1.class);
		Method handlerMethod = Handler1.class.getMethod("execute");
		Assert.assertEquals( handlerMethod,handler.getHandlerMethod());
		Assert.assertEquals( Command.DEFAULT_COMMAND_IDENTIFIER,handler.getMetadata().getIdentifier());
		Assert.assertNotNull(handler.getMetadata().getOptionConfigurations().getOption("fieldCount"));
		Assert.assertNotNull(handler.getMetadata().getParameterConfigurations().get("opt1"));
	}

	@Test
	public void testGetMetadata() throws SecurityException, NoSuchMethodException {
		ExecutorHandler handler = new ExecutorHandler(Handler1.class);
		Method handlerMethod = Handler1.class.getMethod("execute");
		Assert.assertEquals( handlerMethod,handler.getHandlerMethod());
		Assert.assertEquals( Command.DEFAULT_COMMAND_IDENTIFIER,handler.getMetadata().getIdentifier());
		Assert.assertNotNull(handler.getMetadata().getOptionConfigurations().getOption("fieldCount"));
		Assert.assertNotNull(handler.getMetadata().getParameterConfigurations().get("opt1"));
	}

	@Test
	public void testGetHandlerClass() {
		ExecutorHandler handler = new ExecutorHandler(Handler1.class);
		Assert.assertEquals( Handler1.class, handler.getHandlerClass());
	}

	@Test
	public void testGetHandlerMethod() throws SecurityException, NoSuchMethodException {
		ExecutorHandler handler = new ExecutorHandler(Handler1.class);
		Method handlerMethod = Handler1.class.getMethod("execute");
		Assert.assertEquals( handlerMethod,handler.getHandlerMethod());
	}

	@Test
	public void testExecute() throws Exception {
		
		ExecutorHandler handler = new ExecutorHandler(Handler1.class);
		
		CommandLineConfiguration cmdConfig = new CommandLineConfiguration();
		cmdConfig.getOptionConfiguration().addAll(handler.getMetadata().getOptionConfigurations().values());
		cmdConfig.getParameterConfiguration().addAll(handler.getMetadata().getParameterConfigurations().values());
		
		OptionInputSet optionInput = new OptionInputSet(handler.getMetadata().getOptionConfigurations());
		ParameterInput paramInput = new ParameterInputImpl(
					handler.getMetadata().getOptionConfigurations().get("fieldCount").getParameterMetadata(), 
					2);
		OptionInput fieldCount = new OptionInputImpl(
					handler.getMetadata().getOptionConfigurations().get("fieldCount"), 
					paramInput);
		optionInput.add(fieldCount);
		
		ParameterInput paramOpt1Input = new ParameterInputImpl(
					handler.getMetadata().getParameterConfigurations().get("opt1"), 
					true);
		ParameterInputSet paramInputSet = new ParameterInputSet(
					handler.getMetadata().getParameterConfigurations());
		paramInputSet.add(paramOpt1Input);
		
		CommandLineParseResult result = EasyMock.createMock(CommandLineParseResult.class);
		EasyMock.expect(result.isCommandMatch()).andReturn(false);
		EasyMock.expect(result.getConfiguration()).andReturn(cmdConfig).atLeastOnce();
		EasyMock.expect(result.getOptionInput()).andReturn(optionInput).atLeastOnce();
		EasyMock.expect(result.getParameterInput()).andReturn(paramInputSet).atLeastOnce();
		
		PowerMock.replay(result, Handler1.class);
		
		handler.execute(result);
		
		Assert.assertEquals(1, Handler1.getCallCount());
		Assert.assertEquals(2, Handler1.getCallFieldCount());
		Assert.assertEquals(true, Handler1.getCallOpt1());

		PowerMock.verify(result, Handler1.class);
	}

}

