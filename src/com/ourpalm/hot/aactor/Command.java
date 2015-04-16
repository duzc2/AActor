package com.ourpalm.hot.aactor;

public class Command {
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
