package com.ourpalm.hot.aactor.impl;

import java.util.Map;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.config.MessageDispatcher;

public class LocalActorRef implements ActorRef {

	private String id;
	private Map<String, Object> refMap;
	private ActorSystem actorSystem;

	public LocalActorRef(String id, Map<String, Object> refMap,
			ActorSystem actorSystem) {
		this.id = id;
		this.refMap = refMap;
		this.actorSystem = actorSystem;
	}

	@Override
	public void sendMessage(String command, Object... arg) {
		Object a = refMap.get(id);
		if (a == null) {
			throw new NullPointerException("can't find actor with id:" + id);
		}
		try {
			sendMessage_(a, command, arg);
		} catch (Throwable t) {
			onError(a, t, command, arg);
		}

	}

	private void onError(Object a, Throwable t, String command, Object[] arg) {
		throw new RuntimeException(t);
	}

	private void sendMessage_(Object a, String command, Object[] arg)
			throws Exception {
		MessageDispatcher dispatcher = actorSystem.getConfigure()
				.getDispatcher();
		dispatcher.sendMessage(this, a, command, arg);
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
		if (getClass() != obj.getClass()) {
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
}