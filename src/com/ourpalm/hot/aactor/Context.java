package com.ourpalm.hot.aactor;

public class Context {
	private MessageFilter messageFilter;
	private ErrorHandler errorHandler;
	private DefaultMessageHandler DefaultMessageHandler;

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
