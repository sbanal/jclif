package org.jclif.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.jclif.annotation.Command;
import org.jclif.annotation.Handler;
import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.annotation.ParameterType;
import org.jclif.type.CommandMetadata;
import org.jclif.type.CommandMetadataImpl;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionMetadataImpl;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterMetadataImpl;
import org.jclif.util.ReflectionUtil;

/**
 * This class processes annotation information of a handler class.
 * 
 * @author stephen
 */
public class AnnotationProcessor {

	/**
	 * Returns an ExecutorHandler of a JCLIF annotated class.
	 * 
	 * @param commandHandler	handler class
	 * @return ExecutorHandler	executor handler of handler class
	 * @throws IllegalArgumentException thrown if a class is not a recognized as a valid handler
	 */
	public static ExecutorHandler createExecutorHandler(Class<?> commandHandler)
	{
		CommandMetadata metadata = getHandlerMetadata(commandHandler);
		Method handlerMethod = getHandlerMethod(commandHandler);
		return new ExecutorHandler(metadata, commandHandler, handlerMethod);
	}
	
	/**
	 * Retrieved handler command meta data,
	 * 
	 * @param commandHandler	command handler class
	 * @return CommandMetadata handler command metadata
	 */
	public static CommandMetadata getHandlerMetadata(Class<?> commandHandler) {
		
		// extract class annotation of command
		Command commandAnnotation = commandHandler.getAnnotation(Command.class);
		if(commandAnnotation==null) {
			throw new IllegalArgumentException("Unable to extract annotation from class" 
					+ commandHandler + ". Skipping processing.");
		}
		
		// extract option annotations of command
		OptionConfiguration optionConfig = new OptionConfiguration();
		ParameterConfiguration parameterConfig = new ParameterConfiguration();
		for(Field field : commandHandler.getDeclaredFields()) {
			
			if(field.isAnnotationPresent(Option.class)) {
			
				Option optionAnnotation = field.getAnnotation(Option.class);
				ParameterType paramType = optionAnnotation.type();
				if(paramType==null) {
					paramType = ParameterType.toParamType(field.getType());
				}
				
				ParameterMetadata parameter = new ParameterMetadataImpl(
						optionAnnotation.identifier(), paramType);
				boolean multiValued = field.getType() == List.class;
				OptionMetadata option = new OptionMetadataImpl(optionAnnotation, parameter, multiValued);
				optionConfig.addOption(option);
				
			} else if(field.isAnnotationPresent(Parameter.class)) {
				
				Parameter parameterAnnotation = field.getAnnotation(Parameter.class);
				ParameterMetadata parameterMetadata = new ParameterMetadataImpl(parameterAnnotation);
				parameterConfig.add(parameterMetadata);
				
			}
			
		}
		
		CommandMetadata metadata = new CommandMetadataImpl(commandAnnotation.identifier(), 
				optionConfig, 
				parameterConfig, 
				commandAnnotation.description(), 
				commandAnnotation.longDescription());
		return metadata;
	}
	
	/**
	 * Returns handler method of handler class. A handler method is a method annotated with
	 * @Handler. A handler method must be a non-public no argument method.
	 * 
	 * @param commandHandler
	 * @return Method handler method.
	 */
	public static Method getHandlerMethod(Class<?> commandHandler) {
		
		// extract executor method
		Method handlerMethod = null;
		for(Method method : commandHandler.getMethods()) {
			if(ReflectionUtil.isPublicNoArgMethod(method)) {
				Handler annotation = method.getAnnotation(Handler.class);
				if(annotation!=null) {
					handlerMethod = method;
					break;
				}
			}
		}
		
		if(handlerMethod == null) {
			throw new IllegalArgumentException("Command handler annotation not found in " 
					+ commandHandler.getCanonicalName() + ". Skipping processing.");
		}
		
		return handlerMethod;
	}
	
}
