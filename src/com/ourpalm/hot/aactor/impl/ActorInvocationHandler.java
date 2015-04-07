package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ourpalm.hot.aactor.ActorRef;

public class ActorInvocationHandler implements InvocationHandler {

	private ActorRef actorRef;
	//private Class<?> clazz;

	public ActorInvocationHandler(ActorRef actorRef/*, Class<?> clazz*/) {
		this.actorRef = actorRef;
		//this.clazz = clazz;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		actorRef.sendMessage(method.getName(), args);
		return null;
	}

}
