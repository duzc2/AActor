package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.config.MessageDispatcher;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutor;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutors;

public class MultiThreadDispatcher implements MessageDispatcher {
	private OrderedExecutor excutor;
	private ConcurrentHashMap<Object, HashMap<String, Method>> mailboxMap = new ConcurrentHashMap<>();
	private ActorSystem as;

	public MultiThreadDispatcher() {
	}

	@Override
	public void sendMessage(ActorRef ar, Object a, String command, Object[] arg)
			throws Exception {
		excutor.execute(a, new Runnable() {

			@Override
			public void run() {
				HashMap<String, Method> mailboxCache = mailboxMap.get(ar);
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
		excutor.shutdown();
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
				Class<?> returnType = method.getReturnType();
				if (returnType == Void.class || returnType == void.class) {
					mailboxCache.put(name, method);
				} else {
					throw new ActorException(
							"Mailbox return type must be void or Future:"
									+ name);
				}
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
		int processors = Runtime.getRuntime().availableProcessors();
		this.excutor = OrderedExecutors.getOrderedExecutor(1, processors,
				60 * 1000, new ThreadFactory() {
					AtomicLong id = new AtomicLong();

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("MultiThreadDispatcher-"
								+ id.getAndIncrement());
						return t;
					}
				});
		excutor.prestartCoreThread();
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
