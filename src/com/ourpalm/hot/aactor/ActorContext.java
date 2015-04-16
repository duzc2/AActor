package com.ourpalm.hot.aactor;

import java.util.LinkedList;
import java.util.Optional;

public class ActorContext {
	public static final ActorContext defaultActorContext = new ActorContext();
	private Optional<MessageFilter> messageFilter = Optional.empty();
	private Optional<ErrorHandler> errorHandler = Optional.empty();
	private Optional<DefaultMessageHandler> defaultMessageHandler = Optional
			.empty();
	private LinkedList<Command> messageQueue = new LinkedList<>();

	public ActorContext() {
	}

	public Optional<MessageFilter> getOptionalMessageFilter() {
		return messageFilter;
	}

	public Optional<ErrorHandler> getOptionalErrorHandler() {
		return errorHandler;
	}

	public Optional<DefaultMessageHandler> getOptionalDefaultMessageHandler() {
		return defaultMessageHandler;
	}

	public LinkedList<Command> getMessageQueue() {
		return messageQueue;
	}

	public ActorContext(MessageFilter mf) {
		this.messageFilter = Optional.ofNullable(mf);
	}

	public MessageFilter getMessageFilter() {
		return messageFilter.get();
	}

	public void setMessageFilter(MessageFilter messageFilter) {
		this.messageFilter = Optional.ofNullable(messageFilter);
	}

	public void setErrorHandler(ErrorHandler eh) {
		this.errorHandler = Optional.ofNullable(eh);
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler.get();
	}

	public DefaultMessageHandler getDefaultMessageHandler() {
		return defaultMessageHandler.get();
	}

	public void setDefaultMessageHandler(
			DefaultMessageHandler defaultMessageHandler) {
		this.defaultMessageHandler = Optional.ofNullable(defaultMessageHandler);
	}

}
