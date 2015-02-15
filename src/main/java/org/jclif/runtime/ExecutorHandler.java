package org.jclif.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.parser.CommandLineParseResult;
import org.jclif.type.CommandMetadata;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionInput;
import org.jclif.type.OptionMetadata;
import org.jclif.util.ReflectionUtil;


/**
 * This class executes the handler method of a command line handler class.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 */
public class ExecutorHandler {
	
	private static final Logger LOGGER = Logger.getLogger(ExecutorHandlerRegistry.class.getCanonicalName());
	
	private CommandMetadata metadata;
	private Class<?> handlerClass;
	private Method handlerMethod;
	
	/**
	 * Creates an instance of ExecutorHandler.
	 * 
	 * @param handlerClass handler class
	 */
	public ExecutorHandler(Class<?> handlerClass) {
		this(AnnotationProcessor.getHandlerMetadata(handlerClass),
				handlerClass, 
				AnnotationProcessor.getHandlerMethod(handlerClass));	
	}
	
	/**
	 * Creates an instance ExecutorHandler. 
	 * 
	 * @param metadata
	 * @param handlerClass
	 * @param handlerMethod
	 */
	public ExecutorHandler(CommandMetadata metadata, Class<?> handlerClass, Method handlerMethod) {
		this.metadata = metadata;
		this.handlerClass = handlerClass;
		this.handlerMethod = handlerMethod;
	}

	/**
	 * Returns metadata of a handler class.
	 * 
	 * @return CommandMetadata metadata
	 */
	public CommandMetadata getMetadata() {
		return metadata;
	}
	
	/**
	 * Returns handler class.
	 * 
	 * @return
	 */
	public Class<?> getHandlerClass() {
		return handlerClass;
	}
	
	/**
	 * Returns handler method of handler class.
	 * @return
	 */
	public Method getHandlerMethod() {
		return handlerMethod;
	}
	
	/**
	 * Executes a handler. Execution will create a new instance of the handler class
	 * then populates the handler fields right before executing the handler method.
	 * 
	 * @param result						parse result
	 * @throws ExecutorRuntimeException 	thrown if an error occurs while executing the handler
	 */
	public void execute(CommandLineParseResult result) {
		
		try {
			
			Object handlerInstance = handlerClass.newInstance();
			CommandMetadata cmdMetadata = (result.isCommandMatch()) ? (CommandMetadata) result.getMatchingCommand().getMetadata() : null;
			OptionConfiguration optionConfig = (cmdMetadata==null) ? result.getConfiguration().getOptionConfiguration() : cmdMetadata.getOptionConfigurations();
			
			for(Field field: handlerClass.getDeclaredFields()) {
				LOGGER.fine(String.format("processing field = " + field.getName()));
				Object value = getHandlerFieldInputValue(field, optionConfig, result);			
				invokeSetterMethod(field, handlerInstance, value);
			}
			
			getHandlerMethod().invoke(handlerInstance);
			
		} catch (Exception e) {
			throw new ExecutorRuntimeException("Unable to create a new instance of handler class " 
					+ handlerClass.getCanonicalName(), e);
		}
		
	}
	
	/**
	 * Returns the command line input mapped to an annotated field in a handler.
	 * 
	 * @param field
	 * @param optionConfig
	 * @param result
	 * @return Object command line input value of an annotated handler field
	 */
	Object getHandlerFieldInputValue(Field field, OptionConfiguration optionConfig, CommandLineParseResult result) {
		
		if(field.isAnnotationPresent(Option.class)) {
			Option option = field.getAnnotation(Option.class);
			OptionMetadata optMetadata = optionConfig.get(option.identifier());
			if(optMetadata.isParameterAccepted()) {
				OptionInput optionValue = result.getOptionInput().get(option.identifier());
				return (optionValue == null) ? null : optionValue.getParameter().getValue();
			} else {
				return result.getOptionInput().contains(option.identifier());
			}
		} else if(field.isAnnotationPresent(Parameter.class)) {
			Parameter parameter = field.getAnnotation(Parameter.class);
			if(result.getParameterInput().contains(parameter.identifier())) {
				return result.getParameterInput().get(parameter.identifier()).getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Invokes setter method of handler.
	 * 
	 * @param field
	 * @param handlerInstance
	 * @param value
	 */
	void invokeSetterMethod(Field field, Object handlerInstance, Object value) {
		
		if(value==null) {
			LOGGER.fine(String.format("Calling set method for field " + field.getName() + 
					" using field value = " +  value + " skipped."));
			return;
		}
		
		try {
			LOGGER.fine(String.format("Calling set method for field " + field.getName() + ", value = " +  value));
			
			Class<?> paramTypeClass = value.getClass();
			Method m = ReflectionUtil.getSetterMethod(handlerClass, field.getName(), paramTypeClass);
			m.invoke(handlerInstance, value);
			
			LOGGER.fine(String.format("method invoked " + m.getName() + ", value = " +  value));
		} catch (NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, "Setter method not found for field " + field.getName(), e);
			throw new UnsupportedOperationException("Unable to execute"
					+ " setter method " + handlerClass.getCanonicalName() 
					+ " for field " + field.getName() + " with param " 
					+ value.getClass().getCanonicalName() + "");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Setter method call failed for field " + field.getName(), e);
			throw new ExecutorRuntimeException("Setter method failed for field " + field.getName(), e);
		}
		
	}
	
}
