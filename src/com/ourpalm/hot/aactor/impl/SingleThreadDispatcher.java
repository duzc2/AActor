package com.ourpalm.hot.aactor.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ourpalm.hot.aactor.SelfRef;

public class SingleThreadDispatcher extends AbstractDispatcher {

	private LinkedBlockingDeque<Runnable> messageDeque = new LinkedBlockingDeque<>();
	private ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L,
			TimeUnit.MILLISECONDS, messageDeque, new ThreadFactory() {

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
	public void sendNormalMessage(SelfRef ar, Object a, String command,
			Object[] arg){

		queuedMessage.incrementAndGet();
		executor.submit(() -> ar.call(command, arg));
	}

	@Override
	protected void sendPriorMessage(SelfRef ar, Object a, String command,
			Object[] arg){
		queuedMessage.incrementAndGet();
		messageDeque.addLast(() -> ar.call(command, arg));
		executor.submit(() -> {
		});
	}

	@Override
	public void close() {
		this.executor.shutdownNow();
	}

}
