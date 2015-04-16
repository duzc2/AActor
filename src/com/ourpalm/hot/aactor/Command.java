package com.ourpalm.hot.aactor;

import java.io.Serializable;

public class Command implements Serializable {
	private static final long serialVersionUID = 1L;

	public Command(String command2, Object[] arg) {
		this.command = command2;
		this.args = arg;
	}

	private String command;
	private Object[] args;

	public Object[] getArgs() {
		return args;
	}

	public String getCommand() {
		return command;
	}
}
