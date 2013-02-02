package org.jclif.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RuntimeConfiguration extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5801691693374599163L;
	
	public static final String CONFIG_PROPERTY_APP_NAME = "org.jclif.app.name";
	public static final String CONFIG_PROPERTY_APP_MAIN = "org.jclif.app.main";
	public static final String CONFIG_PROPERTY_APP_HANDLER_LIST = "org.jclif.app.handler";
	public static final String CONFIG_PROPERTY_APP_HANDLER_PACKAGE = "org.jclif.app.handler.package";
	
	private int handlerCount = 1;
	
	public RuntimeConfiguration(String name) {
		put(CONFIG_PROPERTY_APP_NAME, name);
	}
	
	public void setHandlerPackage(String packageName) {
		put(CONFIG_PROPERTY_APP_HANDLER_PACKAGE, packageName);
	}
	
	public void addHandler(Class<?> handler) {
		put(CONFIG_PROPERTY_APP_HANDLER_LIST + "." + (handlerCount++), handler.getCanonicalName());
	}
	
	public List<Class<?>> getHandlerList() {
		List<Class<?>> handlerList = new ArrayList<Class<?>>();
		for(int i = 1; ; i++) {
			String className = (String) get(CONFIG_PROPERTY_APP_HANDLER_LIST + "." + i);
			if(className==null) break;
			try {
				handlerList.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				// skip this class
			}
		}
		this.handlerCount = handlerList.size();
		return handlerList;
	}
	
	public int getHandlerCount() {
		return handlerCount;
	}
	
	public String getName() {
		return (String) get(CONFIG_PROPERTY_APP_NAME);
	}
	
	public String getHandlerPackage() {
		return (String) get(CONFIG_PROPERTY_APP_HANDLER_PACKAGE);
	}
	
}
