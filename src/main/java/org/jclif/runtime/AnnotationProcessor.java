package org.jclif.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import org.jclif.annotation.Command;
import org.jclif.annotation.Handler;
import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.type.CommandMetadata;
import org.jclif.type.CommandMetadataImpl;
import org.jclif.type.OptionConfiguration;
import org.jclif.type.OptionMetadata;
import org.jclif.type.OptionMetadataImpl;
import org.jclif.type.ParameterConfiguration;
import org.jclif.type.ParameterMetadata;
import org.jclif.type.ParameterMetadataImpl;
import org.jclif.type.ParameterType;
import org.jclif.util.ReflectionUtil;

public class AnnotationProcessor {

	private static Logger LOGGER = Logger.getLogger(AnnotationProcessor.class.getCanonicalName());
	
	public static ExecutorHandler createExecutorHandler(Class<?> commandHandler)
	{
		
		// extract class annotation of command
		Command commandAnnotation = commandHandler.getAnnotation(Command.class);
		if(commandAnnotation==null) {
			throw new IllegalArgumentException("Unable to extract annotation from class" 
					+ commandHandler + ". Skipping processing.");
		}
		
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
		
		// extract option annotations of command
		OptionConfiguration optionConfig = new OptionConfiguration();
		ParameterConfiguration parameterConfig = new ParameterConfiguration();
		for(Field field : commandHandler.getDeclaredFields()) {
			
			if(field.isAnnotationPresent(Option.class)) {
			
				Option optionAnnotation = field.getAnnotation(Option.class);
				if(optionAnnotation==null) {
					continue;
				}
				
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
				if(parameterAnnotation==null) {
					continue;
				}
				ParameterMetadata parameterMetadata = new ParameterMetadataImpl(parameterAnnotation);
				parameterConfig.add(parameterMetadata);
			}
		}
		
		
		LOGGER.info("Adding handler Command[command=" + commandAnnotation.identifier() 
				+ ", desc=" + commandAnnotation.description() + "]");
		
		LOGGER.info("--- End Processing annotation details: " + commandHandler.getCanonicalName());
		
		try {
			CommandMetadata metadata = new CommandMetadataImpl(commandAnnotation.identifier(), 
					optionConfig, 
					parameterConfig, 
					commandAnnotation.description(), 
					commandAnnotation.longDescription());
			return new ExecutorHandler(metadata, commandHandler, handlerMethod);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to register command metadata to registry", e);
		}
		
	}
	
}
