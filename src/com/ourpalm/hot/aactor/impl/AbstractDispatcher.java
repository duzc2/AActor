package com.ourpalm.hot.aactor.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;
import com.ourpalm.hot.aactor.config.messagehandler.Demonitor;
import com.ourpalm.hot.aactor.config.messagehandler.Monitor;
import com.ourpalm.hot.aactor.config.messagehandler.NamedMessageHandler;

public abstract class AbstractDispatcher implements MessageDispatcher {
	protected final AtomicLong queuedMessage = new AtomicLong();
	protected ActorSystem as;

	@Override
	public ActorRef createActorAndLink(ActorRef self, Class<?> clazz,
			Object[] args) {
		ActorRef ar = as.getConfigure().getActorBuilder()
				.buildActorRefWithLink(self, clazz, args);
		return ar;
	}

	@Override
	public ActorRef createActor(Class<?> clazz, Object[] args) {
		ActorRef ar = as.getConfigure().getActorBuilder()
				.buildActorRef(clazz, args);
		return ar;
	}

	@Override
	public void detachActor(ActorRef ref) {
		as.getConfigure().getActorBuilder().detachActor(ref);
	}

	@Override
	public long queuedMessage() {
		return queuedMessage.get();
	}

	@Override
	public void decrementQueuedMessage() {
		queuedMessage.decrementAndGet();
	}

	@Override
	public void link(SelfRef self, ActorRef ar) {
		self.sendMessage(Monitor.COMMAND, ar);
		ar.sendMessage(Monitor.COMMAND, self);
	}

	@Override
	public void unlink(SelfRef self, ActorRef ar) {
		if (self == null || ar == null) {
			return;
		}
		self.sendMessage(Demonitor.COMMAND, ar);
		ar.sendMessage(Demonitor.COMMAND, self);
	}

	@Override
	public void monitor(SelfRef localSelfRef, ActorRef ar) {
		ar.sendMessage(Monitor.COMMAND, localSelfRef);
	}

	@Override
	public void demonitor(SelfRef self, ActorRef ar) {
		if (self == null || ar == null) {
			return;
		}
		ar.sendMessage(Demonitor.COMMAND, self);
	}

	@Override
	public ActorRef findActor(Class<?> class1) {
		return as.getConfigure().getActorBuilder().findActor(class1);
	}

	@Override
	public ActorRef findActorById(String actorId) {
		return as.getConfigure().getActorBuilder().findActorById(actorId);
	}

	@Override
	public void init(ActorSystem as) {
		this.as = as;
	}

	public void sendMessage(SelfRef ar, Object a, String command, Object[] arg)
			throws Exception {
		if (isPrior(command)) {
			sendPriorMessage(ar, a, command, arg);
		} else {
			sendNormalMessage(ar, a, command, arg);
		}
	}

	private boolean isPrior(String command) {
		return NamedMessageHandler.staticHandlers.containsKey(command);
	}

	protected abstract void sendPriorMessage(SelfRef ar, Object a,
			String command, Object[] arg) throws Exception;

	protected abstract void sendNormalMessage(SelfRef ar, Object a,
			String command, Object[] arg) throws Exception;
}
