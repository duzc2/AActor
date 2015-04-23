package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;

public class LocalActorRef implements ActorRef {

	protected final String id;
	protected final Map<String, LocalSelfRef> refMap;
	protected final ActorSystem actorSystem;

	public LocalActorRef(String id, Map<String, LocalSelfRef> refMap,
			ActorSystem actorSystem) {
		this.id = id;
		this.refMap = refMap;
		this.actorSystem = actorSystem;
	}

	@Override
	public void sendMessage(String command, Object... arg) {
		if (command == null) {
			throw new NullPointerException("command is null.");
		}
		LocalSelfRef a = refMap.get(id);
		if (a == null) {
			throw new ActorException("can't find actor with id:" + id);
		}
		a.sendMessage(command, arg);

	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LocalActorRef)) {
			return false;
		}
		LocalActorRef other = (LocalActorRef) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T asType(Class<T> clazz) {
		try {
			InvocationHandler handler = new ActorInvocationHandler(this);
			Class<?> proxyClass = Proxy.getProxyClass(clazz.getClassLoader(),
					clazz);
			Object newInstance = proxyClass.getConstructor(
					InvocationHandler.class).newInstance(handler);
			return (T) newInstance;
		} catch (Exception e) {
			throw new ActorException("can't make type proxy for type "
					+ clazz.getCanonicalName(), e);
		}
	}

	public ActorSystem getActorSystem() {
		return actorSystem;
	}
}