package com.ourpalm.hot.aactor;

import com.ourpalm.hot.aactor.config.ActorSystemConfigure;

public interface ActorSystem {

	void start(Class<?> rootActor, Object... args);

	void stop();

	ActorRef findActor(Class<?> class1);

	ActorRef findActorById(String actorId);

	ActorRef getRootActorRef();

	ActorSystemConfigure getConfigure();

	ActorRef createActor(Class<?> clazz, Object... args);

	void detachActor(ActorRef ref);
}
