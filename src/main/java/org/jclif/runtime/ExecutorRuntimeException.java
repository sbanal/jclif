package org.jclif.runtime;

public final class ExecutorRuntimeException extends RuntimeException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1579818008725810277L;

  public ExecutorRuntimeException(String msg) {
    super(msg);
  }

  public ExecutorRuntimeException(String msg, Throwable t) {
    super(msg, t);
  }

}
