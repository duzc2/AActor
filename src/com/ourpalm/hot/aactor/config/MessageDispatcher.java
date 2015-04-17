package com.ourpalm.hot.aactor.config;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;

public interface MessageDispatcher {

	void sendMessage(SelfRef ar, Object a, String command, Object[] arg)
			throws Exception;

	void close();

	ActorRef createActor(Class<?> clazz, Object[] args);

	void detachActor(ActorRef ref);

	void init(ActorSystem as);

	ActorRef findActor(Class<?> class1);

	ActorRef findActorById(String actorId);

	long queuedMessage();

	void decrementQueuedMessage();
}
