package com.ourpalm.hot.aactor;

public class Context {
	private MessageFilter messageFilter;
	private ErrorHandler eh;

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
	public void setErrorHandler(ErrorHandler eh){
		this.eh = eh;
	}

	public ErrorHandler getErrorHandler(){
		return eh;
	}
}
