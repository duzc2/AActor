package com.ourpalm.hot.aactor.config;

import java.lang.reflect.InvocationTargetException;

import com.ourpalm.hot.aactor.ActorRef;

public interface MessageDispatcher {

	void sendMessage(ActorRef ar, Object a, String command, Object[] arg)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException;

}
