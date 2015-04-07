package com.ourpalm.hot.aactor.impl;

import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.config.ActorBuilder;
import com.ourpalm.hot.aactor.config.ActorSystemConfigure;
import com.ourpalm.hot.aactor.config.ConfigureLoader;

public class DefaultActorSystem implements ActorSystem {
	private ActorSystemConfigure config;
	private ActorRef rootActor;

	@Override
	public void start(Class<?> root, ActorSystemConfigure config,
			Object... args) {
		if (root == null) {
			throw new NullPointerException("root actor class is null");
		}
		Actor annotation = root.getAnnotation(Actor.class);
		if (annotation == null) {
			throw new java.lang.IllegalArgumentException(
					"Specified root actor class is not a Actor");
		}
		config = config != null ? config : ConfigureLoader.loadConfigure();
		this.config = config;
		ActorBuilder actorBuilder = config.getActorBuilder();
		actorBuilder.init(this);
		this.rootActor = actorBuilder.buildActorRef(root, args);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public ActorRef findActor(Class<?> class1) {
		return config.getActorBuilder().findActor(class1);
	}

	@Override
	public ActorRef findActorById(String actorId) {
		return config.getActorBuilder().findActorById(actorId);
	}

	@Override
	public ActorRef getRootActorRef() {
		return rootActor;
	}

	@Override
	public ActorSystemConfigure getConfigure() {
		return this.config;
	}

	@Override
	public ActorRef createActor(Class<?> clazz, Object... args) {
		return config.getActorBuilder().buildActorRef(clazz, args);
	}

	@Override
	public void detachActor(ActorRef ref) {
		config.getActorBuilder().detachActor(ref);
	}

}