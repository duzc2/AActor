package com.ourpalm.hot.aactor.config;

import java.lang.reflect.InvocationTargetException;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;

public interface MessageDispatcher {

	void sendMessage(ActorRef ar, Object a, String command, Object[] arg)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException;

	void close();

	ActorRef createActor(Class<?> clazz, Object[] args);

	void detachActor(ActorRef ref);

	void init(ActorSystem as);

}
