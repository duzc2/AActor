package com.ourpalm.hot.aactor.config;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;

public interface ActorBuilder {
	void init(ActorSystem as);

	ActorRef buildActorRef(Class<?> root, Object[] args);

	ActorRef buildActorRefById(String key);

	ActorRef findActor(Class<?> class1);

	ActorRef findActorById(String actorId);

	void detachActor(ActorRef ref);

	ActorRef buildActorRefWithLink(ActorRef self, Class<?> clazz, Object[] args);

}
