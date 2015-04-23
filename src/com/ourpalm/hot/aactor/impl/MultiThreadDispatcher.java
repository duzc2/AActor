package com.ourpalm.hot.aactor.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutor;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutors;

public class MultiThreadDispatcher extends AbstractDispatcher {
	private OrderedExecutor excutor;

	public MultiThreadDispatcher() {
	}

	@Override
	public void sendNormalMessage(SelfRef ar, Object a, String command,
			Object[] arg) throws Exception {
		queuedMessage.incrementAndGet();
		excutor.execute(a, () -> ar.call(command, arg));
	}

	@Override
	protected void sendPriorMessage(SelfRef ar, Object a, String command,
			Object[] arg) throws Exception {
		queuedMessage.incrementAndGet();
		excutor.executePreferentially(a, () -> ar.call(command, arg));
	}

	@Override
	public void close() {
		excutor.shutdownNow();
	}

	@Override
	public void init(ActorSystem as) {
		super.init(as);
		int processors = Runtime.getRuntime().availableProcessors();
		this.excutor = OrderedExecutors.getOrderedExecutor(1, processors,
				60 * 1000, new ThreadFactory() {
					AtomicLong id = new AtomicLong();

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

							@Override
							public void uncaughtException(Thread t, Throwable e) {
								e.printStackTrace();
							}
						});
						t.setName("MultiThreadDispatcher-"
								+ id.getAndIncrement());
						return t;
					}
				});
		excutor.prestartCoreThread();
	}

}
