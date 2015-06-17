package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ourpalm.hot.aactor.ActorRef;

public class ActorInvocationHandler implements InvocationHandler {

	private ActorRef actorRef;

	public ActorInvocationHandler(ActorRef actorRef) {
		this.actorRef = actorRef;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args){
		actorRef.sendMessage(method.getName(), args);
		return null;
	}

}
