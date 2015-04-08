package com.ourpalm.hot.aactor.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;

public class SingleThreadDispatcher implements MessageDispatcher {

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
		return ar;
	}

	@Override
	public void detachActor(ActorRef ref) {
		as.getConfigure().getActorBuilder().detachActor(ref);
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
