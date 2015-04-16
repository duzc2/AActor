package com.ourpalm.hot.aactor;

import java.util.LinkedList;

public class ActorContext {
	private MessageFilter messageFilter;
	private ErrorHandler errorHandler;
	private DefaultMessageHandler DefaultMessageHandler;
	private LinkedList<Command> messageQueue = new LinkedList<>();

	public ActorContext() {
	}

	public LinkedList<Command> getMessageQueue() {
		return messageQueue;
	}

	public ActorContext(MessageFilter mf) {
		this.messageFilter = mf;
	}

	public MessageFilter getMessageFilter() {
		return messageFilter;
	}

	public void setMessageFilter(MessageFilter messageFilter) {
		this.messageFilter = messageFilter;
	}

	public void setErrorHandler(ErrorHandler eh) {
		this.errorHandler = eh;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public DefaultMessageHandler getDefaultMessageHandler() {
		return DefaultMessageHandler;
	}

	public void setDefaultMessageHandler(
			DefaultMessageHandler defaultMessageHandler) {
		DefaultMessageHandler = defaultMessageHandler;
	}
}
