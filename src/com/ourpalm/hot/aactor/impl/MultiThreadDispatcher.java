package com.ourpalm.hot.aactor.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutor;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutors;

public class MultiThreadDispatcher implements MessageDispatcher {
	private OrderedExecutor excutor;
	// private ConcurrentHashMap<Object, HashMap<String, Method>> mailboxMap =
	// new ConcurrentHashMap<>();
	private ActorSystem as;

	public MultiThreadDispatcher() {
	}

	@Override
	public void sendMessage(SelfRef ar, Object a, String command, Object[] arg)
			throws Exception {
		excutor.execute(a, new Runnable() {

			@Override
			public void run() {
				ar.call(command, arg);
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
		// mailboxMap.put(ar, mailboxCache);
		return ar;
	}

	@Override
	public void detachActor(ActorRef ref) {
		as.getConfigure().getActorBuilder().detachActor(ref);
		// mailboxMap.remove(ref);
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
