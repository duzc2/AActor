package com.ourpalm.hot.aactor;

import java.util.Map;

import com.ourpalm.hot.aactor.config.ActorSystemConfigure;

public interface ActorSystem {

	ActorRef start(Class<?> rootActor, Object... args);

	void stop();

	ActorRef findActor(Class<?> class1);

	ActorRef findActorById(String actorId);

	ActorRef getRootActorRef();

	ActorSystemConfigure getConfigure();

	ActorRef createActor(Class<?> clazz, Object... args);

	void detachActor(ActorRef ref);

	void register(String name, ActorRef ref);

	void unregister(String name);

	ActorRef whereis(String name);

	Map<String, ActorRef> registered();
}
