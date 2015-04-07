package com.ourpalm.hot.aactor;

public class ActorException extends RuntimeException {

	public ActorException(String string) {
		super(string);
	}

	public ActorException(String string, Throwable t) {
		super(string, t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
