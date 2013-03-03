package org.jclif.runtime;

import org.jclif.annotation.Command;
import org.jclif.annotation.Handler;
import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.annotation.ParameterType;

@Command
class Handler1 {
	
	static int callCount = 0;
	static boolean callOpt1;
	static int callFieldCount;
	
	@Option(identifier="fieldCount", required=true,type=ParameterType.INTEGER)
	int fieldCount;
	
	@Parameter(identifier="opt1", required=true)
	boolean opt1;
	
	@Handler
	public void execute() {
		System.out.println("FieldCount=" + fieldCount + ", opt1=" + opt1);
		callCount++;
		callFieldCount = fieldCount;
		callOpt1 = opt1;
	}
	
	public void setFieldCount(Integer count) {
		this.fieldCount = count;
	}
	
	public void setOpt1(Boolean value) {
		this.opt1 = value;
	}
	
	public static int getCallCount() {
		return callCount;
	}
	
	public static int getCallFieldCount() {
		return callFieldCount;
	}
	
	public static boolean getCallOpt1() {
		return callOpt1;
	}
	
};
