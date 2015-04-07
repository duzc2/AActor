package com.ourpalm.hot.aactor;

import com.ourpalm.hot.aactor.config.ActorBuilder;
import com.ourpalm.hot.aactor.config.ActorSystemConfigure;
import com.ourpalm.hot.aactor.config.MessageDispatcher;
import com.ourpalm.hot.aactor.impl.DefaultActorBuilder;
import com.ourpalm.hot.aactor.impl.DefaultActorSystem;
import com.ourpalm.hot.aactor.impl.SingleThreadDispatcher;

public class ActorSystemBuilder {
	private MessageDispatcher messageDispatcher;
	private ActorBuilder actorBuilder;

	public ActorSystem build() {
		return new DefaultActorSystem(makeConfig());
	}

	private ActorSystemConfigure makeConfig() {
		return new ActorSystemConfigure(
				actorBuilder == null ? new DefaultActorBuilder() : actorBuilder,
				messageDispatcher == null ? new SingleThreadDispatcher()
						: messageDispatcher);
	}

	public ActorSystemBuilder setMessageDispatcher(MessageDispatcher dispatcher) {
		this.messageDispatcher = dispatcher;
		return this;
	}

	public ActorSystemBuilder setActorBuilder(ActorBuilder actorBuilder) {
		this.actorBuilder = actorBuilder;
		return this;
	}
}
