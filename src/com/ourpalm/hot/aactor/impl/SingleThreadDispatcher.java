package com.ourpalm.hot.aactor.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;

public class SingleThreadDispatcher implements MessageDispatcher {

	private ConcurrentHashMap<ActorRef, HashMap<String, Method>> mailboxMap = new ConcurrentHashMap<>();
	private ActorSystem as;
	private ExecutorService executor = Executors
			.newSingleThreadExecutor(new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "SingleThreadDispatcher");
					t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

						@Override
						public void uncaughtException(Thread t, Throwable e) {
							e.printStackTrace();
						}
					});
					t.setPriority(Thread.MAX_PRIORITY);
					return t;
				}
			});

	@Override
	public void sendMessage(SelfRef ar, Object a, String command, Object[] arg)
			throws Exception {

		executor.submit(new Runnable() {

			@Override
			public void run() {
				ar.call(command, arg);
			}
		});
	}

	@Override
	public void close() {
		this.executor.shutdown();
	}

	@Override
	public ActorRef createActor(Class<?> clazz, Object[] args) {
		ActorRef ar = as.getConfigure().getActorBuilder()
				.buildActorRef(clazz, args);
		HashMap<String, Method> mailboxCache = new HashMap<>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			Mailbox annotation = method.getAnnotation(Mailbox.class);
			if (annotation != null) {
				method.setAccessible(true);
				String name = annotation.value();
				if (name.equals("")) {
					name = method.getName();
				}
				if (mailboxCache.containsKey(name)) {
					throw new ActorException("Duplicated Mailbox " + name);
				}
				mailboxCache.put(name, method);
			}
		}

		mailboxMap.put(ar, mailboxCache);
		return ar;
	}

	@Override
	public void detachActor(ActorRef ref) {
		as.getConfigure().getActorBuilder().detachActor(ref);
		mailboxMap.remove(ref);
	}

	@Override
	public void init(ActorSystem as) {
		this.as = as;
	}

	@Override
	public ActorRef findActor(Class<?> class1) {
		return as.getConfigure().getActorBuilder().findActor(class1);
	}

	@Override
	public ActorRef findActorById(String actorId) {
		return as.getConfigure().getActorBuilder().findActorById(actorId);
	}

}
