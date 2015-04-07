package com.ourpalm.hot.aactor.impl.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class OrderedExecutors {
	public static OrderedExecutor getOrderedExecutor(int corePoolSize,
			int maximumPoolSize, long keepAliveTime, ThreadFactory threadFactory) {
		return new OrderedExecutorImpl(corePoolSize, maximumPoolSize,
				keepAliveTime, threadFactory);
	}

	private static OrderedExecutor defaultExcutor = new OrderedExecutorImpl(0,
			80, 1000, new ThreadFactory() {
				AtomicLong id = new AtomicLong();

				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setDaemon(true);
					t.setName("Default-OrderedExcutor-" + id.getAndIncrement());
					return t;
				}
			}) {
		@Override
		public <T> Future<T> executeAllSync(final Callable<T> command) {
			throw new UnsupportedOperationException("Ĭ������ִ�����в���ʹ��ȫ��ͬ��������");
		}
	};

	public static OrderedExecutor getDefaultExcutor() {
		return defaultExcutor;
	}
}
