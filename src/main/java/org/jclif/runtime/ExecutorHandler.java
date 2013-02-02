package org.jclif.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.parser.CommandLineParseResult;
import org.jclif.type.CommandMetadata;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionMetadata;
import org.jclif.util.ReflectionUtil;

/**
 * This class links the handler class and its handler method.
 * 
 * @author Stephen Lou Banal &lt;stephen.banal@gmail.com&gt;
 *
 */
public class ExecutorHandler {
	
	private static final Logger LOGGER = Logger.getLogger(ExecutorHandlerRegistry.class.getCanonicalName());
	
	private CommandMetadata metadata;
	private Class<?> handlerClass;
	private Method handlerMethod;
	
	public ExecutorHandler(CommandMetadata metadata, Class<?> handlerClass, Method handlerMethod) {
		this.metadata = metadata;
		this.handlerClass = handlerClass;
		this.handlerMethod = handlerMethod;
	}

	public CommandMetadata getMetadata() {
		return metadata;
	}
	
	public Class<?> getHandlerClass() {
		return handlerClass;
	}
	
	public Method getHandlerMethod() {
		return handlerMethod;
	}
	
	public void execute(CommandLineParseResult result) throws Exception {
		
		Object handlerInstance = handlerClass.newInstance();
		CommandMetadata cmdMetadata = (result.isCommandMatch())?(CommandMetadata) result.getMatchingCommand().getMetadata():null;
		OptionConfiguration optionConfig = null;
		
		if(cmdMetadata==null) {
			optionConfig = result.getConfiguration().getOptionConfiguration();
		} else {
			optionConfig = cmdMetadata.getOptionConfigurations();
		}
		
		for(Field field: handlerClass.getDeclaredFields()) {
		
			Object value = null;
			String fieldName = field.getName();
			LOGGER.info(String.format("processing field = " + field.getName()));
			if(field.isAnnotationPresent(Option.class)) {
				Option option = field.getAnnotation(Option.class);
				boolean optionExist = result.getOptionInput().contains(option.identifier());
					
				LOGGER.info(String.format("Setting field = " + field.getName() + " option specified"));
				OptionMetadata optMetadata = optionConfig.get(option.identifier());
				org.jclif.type.OptionInput optionValue = result.getOptionInput().get(option.identifier());
				if(optionValue!=null) {
					if(optMetadata.isParameterAccepted()) {
						value = optionValue.getParameter().getValue();
					} else {
						value = true;
					}
				} else {
					LOGGER.info(String.format("Option " + field.getName() + " is not specified"));
					if(!optMetadata.isParameterAccepted()) {
						value = optionExist;
					}
				}
				
				LOGGER.info(String.format("Setting field = " + field.getName() + ", value = " +  value));
			} else if(field.isAnnotationPresent(Parameter.class)) {
				Parameter parameter = field.getAnnotation(Parameter.class);
				if(result.getParameterInput().contains(parameter.identifier())) {
					value = result.getParameterInput().get(parameter.identifier()).getValue();
				}
				LOGGER.info(String.format("Setting parameter =" + field.getName() + ", value = " +  value));
			}
			
			if(value!=null) {
				LOGGER.info(String.format("Calling set method for field " + fieldName + ", value = " +  value));
				Class<?> paramTypeClass = value.getClass();
				Method m = ReflectionUtil.getSetterMethod(handlerClass, fieldName, paramTypeClass);
				if(m==null) {
					throw new UnsupportedOperationException("Unable to execute setter method " + handlerClass.getCanonicalName() 
							+ " for field " + fieldName + " with param " + value.getClass().getCanonicalName() + "");
				} else {
					m.invoke(handlerInstance, value);
					LOGGER.info(String.format("method invoked " + m.getName() + ", value = " +  value));
				}
			}
			
		}
		getHandlerMethod().invoke(handlerInstance);
	}

}
