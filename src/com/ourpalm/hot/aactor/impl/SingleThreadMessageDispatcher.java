package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.config.MessageDispatcher;

public class SingleThreadMessageDispatcher implements MessageDispatcher {

	private WeakHashMap<Object, HashMap<String, Method>> mailboxMap = new WeakHashMap<>();
	private ActorSystem as;
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	@Override
	public void sendMessage(ActorRef ar, Object a, String command, Object[] arg)
			throws Exception {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				HashMap<String, Method> mailboxCache = mailboxMap.get(a);
				if (mailboxCache == null) {
					mailboxCache = new HashMap<>();
					Method[] methods = a.getClass().getDeclaredMethods();
					for (Method method : methods) {
						Mailbox annotation = method
								.getAnnotation(Mailbox.class);
						if (annotation != null) {
							method.setAccessible(true);
							String name = annotation.value();
							if (name.equals("")) {
								name = method.getName();
							}
							if (mailboxCache.containsKey(name)) {
								throw new ActorException("Duplicated Mailbox "
										+ name);
							}
							mailboxCache.put(name, method);
						}
					}
				}
				Method m = mailboxCache.get(command);
				if (m == null) {
					throw new ActorException("can't find mailbox " + command
							+ " on Actor:" + ar.toString() + " from class:"
							+ a.getClass());
				}
				try {
					m.invoke(a, arg);
				} catch (Exception e) {
					throw new ActorException("Invoke fail. mailbox:" + command,
							e);
				}
			}
		});
	}

	@Override
	public void close() {
		this.executor.shutdown();
	}

	@Override
	public ActorRef createActor(Class<?> clazz, Object[] args) {
		return as.getConfigure().getActorBuilder().buildActorRef(clazz, args);
	}

	@Override
	public void detachActor(ActorRef ref) {
		as.getConfigure().getActorBuilder().detachActor(ref);
	}

	@Override
	public void init(ActorSystem as) {
		this.as = as;
	}

}
