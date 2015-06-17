package com.ourpalm.hot.aactor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.actors.TimerActor;
import com.ourpalm.hot.aactor.config.ActorBuilder;
import com.ourpalm.hot.aactor.config.ActorSystemConfigure;
import com.ourpalm.hot.aactor.config.ConfigureLoader;
import com.ourpalm.hot.aactor.config.messagehandler.Kill.Killable;

public class DefaultActorSystem implements ActorSystem {
	private ActorSystemConfigure config;
	private ActorRef rootActor;
	private ConcurrentHashMap<String, ActorRef> registerMap = new ConcurrentHashMap<>();
	private volatile ActorRef timerActorRef = null;
	private volatile boolean started = false;

	public DefaultActorSystem(ActorSystemConfigure config) {
		config = config != null ? config : ConfigureLoader.loadConfigure();
		this.config = config;
	}

	@Override
	public synchronized ActorRef start(Class<?> root, Object... args) {
		if (started) {
			throw new IllegalStateException("ActorSystem have been started.");
		}
		if (root == null) {
			throw new NullPointerException("root actor class is null");
		}
		Actor annotation = root.getAnnotation(Actor.class);
		if (annotation == null) {
			throw new java.lang.IllegalArgumentException(
					"Specified root actor class is not a Actor");
		}
		ActorBuilder actorBuilder = config.getActorBuilder();
		actorBuilder.init(this);
		config.getDispatcher().init(this);
		this.rootActor = createActor(root, args);
		started = true;
		return rootActor;
	}

	@Override
	public synchronized void stop() {
		if (!started) {
			throw new IllegalStateException("ActorSystem have been started.");
		}
		started = false;
		if (timerActorRef != null) {
			timerActorRef.asType(Killable.class).SYSTEM_MESSAGE_PROFIX_kill(
					"shutdown");
		}
		rootActor.asType(Killable.class).SYSTEM_MESSAGE_PROFIX_kill("shutdown");
		config.getDispatcher().close();
	}

	@Override
	public ActorRef findActor(Class<?> class1) {
		return config.getDispatcher().findActor(class1);
	}

	@Override
	public ActorRef getRootActorRef() {
		return rootActor;
	}

	@Override
	public ActorSystemConfigure getConfigure() {
		return this.config;
	}

	@Override
	public ActorRef createActor(Class<?> clazz, Object... args) {
		return config.getDispatcher().createActor(clazz, args);
	}

	@Override
	public void detachActor(ActorRef ref) {
		if (ref == null) {
			return;
		}
		config.getDispatcher().detachActor(ref);
	}

	@Override
	public void register(String name, ActorRef ref) {
		this.registerMap.put(name, ref);
	}

	@Override
	public void unregister(String name) {
		this.registerMap.remove(name);
	}

	@Override
	public ActorRef whereis(String name) {
		return registerMap.get(name);
	}

	@Override
	public Map<String, ActorRef> registered() {
		return new HashMap<>(registerMap);
	}

	@Override
	public long queuedMessage() {
		return getConfigure().getDispatcher().queuedMessage();
	}

	@Override
	public ActorRef createActorAndLink(SelfRef self, Class<?> clazz,
			Object... args) {
		return config.getDispatcher().createActorAndLink(self, clazz, args);
	}

	@Override
	public TimerActor getTimerActor() {
		if (timerActorRef != null) {
			return timerActorRef.asType(TimerActor.class);
		}
		synchronized (this) {
			if (timerActorRef == null) {
				timerActorRef = createActor(SimpleTimerActor.class);
			}
		}
		return timerActorRef.asType(TimerActor.class);
	}

	@Override
	public boolean isStarted() {
		return started;
	}

}
