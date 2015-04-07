package com.ourpalm.hot.aactor;

public class Context {
	private MessageFilter messageFilter;

	public Context() {
	}

	public Context(MessageFilter mf) {
		this.messageFilter = mf;
	}

	public MessageFilter getMessageFilter() {
		return messageFilter;
	}

	public void setMessageFilter(MessageFilter messageFilter) {
		this.messageFilter = messageFilter;
	}
}
