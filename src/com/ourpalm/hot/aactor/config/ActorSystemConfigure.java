package com.ourpalm.hot.aactor.config;

public class ActorSystemConfigure {
	private final ActorBuilder actorBuilder;
	private final MessageDispatcher dispatcher;

	public ActorSystemConfigure(ActorBuilder actorBuilder,
			MessageDispatcher dispatcher) {
		this.actorBuilder = actorBuilder;
		this.dispatcher = dispatcher;
	}

	public ActorBuilder getActorBuilder() {
		return actorBuilder;
	}

	public MessageDispatcher getDispatcher() {
		return dispatcher;
	}

}
