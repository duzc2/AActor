package com.ourpalm.hot.aactor.impl.executor;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

public class OrderedExecutorImpl /* extends ThreadPoolExecutor */implements
		OrderedExecutor {

	public class TasksQueue {
		/** A queue of ordered event waiting to be processed */
		private final LinkedBlockingDeque<Runnable> tasksQueue = new LinkedBlockingDeque<Runnable>();

		/** 对key的引用，防止由于GC导致任务丢失 */
		private Object key;

		/**
		 * 
		 */
		public TasksQueue(Object key) {
			this.setKey(key);
		}

		/**
		 * @return the key
		 */
		public Object getKey() {
			return key;
		}

		/**
		 * @param key
		 *            the key to set
		 */
		public void setKey(Object key) {
			this.key = key;
		}

		// /** The current task state */
		private boolean processingCompleted = true;
	}

	private class Worker implements Runnable {

		private volatile long completedTaskCount;

		// private Thread thread;

		private Object fetchTask() {
			Object session = null;
			long currentTime = System.currentTimeMillis();
			long deadline = currentTime + getKeepAliveTime();
			for (;;) {
				try {
					long waitTime = deadline - currentTime;
					if (waitTime <= 0) {
						break;
					}

					try {
						session = waitings
								.poll(waitTime, TimeUnit.MILLISECONDS);
						break;
					} finally {
						if (session == null) {
							currentTime = System.currentTimeMillis();
						}
					}
				} catch (InterruptedException e) {
					// Ignore.
					continue;
				}
			}
			return session;
		}

		@Override
		public void run() {
			// thread = Thread.currentThread();
			try {
				for (;;) {
					Object t = fetchTask();

					idleWorkers.decrementAndGet();
					if (terminate) {
						break;
					}
					if (t == null) {
						synchronized (workers) {
							if (workers.size() > getCorePoolSize()) {
								// Remove now to prevent duplicate exit.
								workers.remove(this);
								break;
							}
						}
					}

					if (t == EXIT_SIGNAL) {
						// Remove now to prevent duplicate exit.
						workers.remove(this);
						break;
					}

					try {
						if (t != null) {

							ThreadSign.instance.set(t);
							runTasks(getTaskQueue(t));

						}
					} finally {
						ThreadSign.instance.set(null);
						idleWorkers.incrementAndGet();
					}
				}
			} finally {
				synchronized (workers) {
					workers.remove(this);
					OrderedExecutorImpl.this.completedTaskCount
							.addAndGet(completedTaskCount);
					workers.notifyAll();
				}
				ThreadSign.instance.set(false);
			}
		}

		private void runTask(Runnable task) {
			// beforeExecute(thread, task);
			// boolean ran = false;
			try {
				task.run();
				// ran = true;
				// afterExecute(task, null);
				completedTaskCount++;
			} catch (Throwable e) {
				throw new RuntimeException("顺序执行器计划异常", e);
			}
		}

		private void runTasks(TasksQueue tasksQueue) {
			for (;;) {
				Runnable task;
				Queue<Runnable> queue = tasksQueue.tasksQueue;

				synchronized (tasksQueue) {
					task = queue.poll();
					if (task == null) {
						tasksQueue.processingCompleted = true;
						break;
					}
				}

				// eventQueueHandler.polled(OrderedThreadPoolExecutor.this,
				// (IoEvent) task);

				runTask(task);
			}
		}
	}

	private static final Object EXIT_SIGNAL = new Object();

	private Map<Object, TasksQueue> queues = Collections
			.synchronizedMap(new WeakHashMap<Object, TasksQueue>());

	/** 用于存储作用者的列队 */
	private final BlockingQueue<Object> waitings = new LinkedTransferQueue<Object>();

	private final Set<Worker> workers = new HashSet<Worker>();
	private final AtomicInteger idleWorkers = new AtomicInteger();

	/**
	 * 统计完成计划次数
	 */
	private AtomicLong completedTaskCount = new AtomicLong();

	private ExecutorService singleExecutorService;

	private int corePoolSize;

	private int maximumPoolSize;

	private long keepAliveTime;

	private ThreadFactory threadFactory;

	private volatile Exception shutdownStack;

	private volatile boolean terminate = false;

	public OrderedExecutorImpl(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, ThreadFactory threadFactory) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveTime = keepAliveTime;
		this.threadFactory = threadFactory;
		singleExecutorService = Executors
				.newSingleThreadExecutor(threadFactory);
	}

	/**
	 * Add a new thread to execute a task, if needed and possible. It depends on
	 * the current pool size. If it's full, we do nothing.
	 */
	private void addWorker() {
		checkShutdown();
		synchronized (workers) {
			if (workers.size() >= getMaximumPoolSize()) {
				return;
			}

			// Create a new worker, and add it to the thread pool
			Worker worker = new Worker();
			Thread thread = getThreadFactory().newThread(worker);

			// As we have added a new thread, it's considered as idle.
			idleWorkers.incrementAndGet();

			// Now, we can start it.
			thread.start();
			workers.add(worker);
		}
	}

	private void addWorkerIfNecessary() {
		if (idleWorkers.get() == 0) {
			synchronized (workers) {
				if (workers.isEmpty() || (idleWorkers.get() == 0)) {
					addWorker();
				}
			}
		}
	}

	private void checkShutdown() {
		if (shutdownStack != null) {
			throw new IllegalStateException("Executor已经关闭！", shutdownStack);
		}
	}

	@Override
	public <T> Future<T> execute(Object obj, Callable<T> query) {
		return execute(obj, query, true);
	}

	public <T> Future<T> execute(Object obj, Callable<T> query, boolean first) {
		Object currentOrderedExecuteObject = ThreadSign
				.getCurrentOrderedExecuteObject();
		if (currentOrderedExecuteObject == obj
				|| obj.equals(currentOrderedExecuteObject)) {
			RunnableFuture<T> command = newTaskFor(query);
			command.run();
			return command;
		}
		checkShutdown();
		RunnableFuture<T> command = newTaskFor(query);
		TasksQueue tasksQueue = getTaskQueue(obj);
		boolean offerObj = true;
		synchronized (tasksQueue) {
			// Inject the event into the executor taskQueue
			if (first) {
				tasksQueue.tasksQueue.offerLast(command);
			} else {
				tasksQueue.tasksQueue.offerFirst(command);
			}
			if (tasksQueue.processingCompleted) {
				tasksQueue.processingCompleted = false;
			} else {
				offerObj = false;
			}
		}
		if (offerObj) {
			waitings.offer(obj);
		}
		addWorkerIfNecessary();
		return command;
	}

	@Override
	public void execute(Object obj, Runnable command) {
		execute(obj, command, true);
	}

	public void execute(Object obj, Runnable command, boolean first) {
		Object currentOrderedExecuteObject = ThreadSign
				.getCurrentOrderedExecuteObject();
		if (currentOrderedExecuteObject == obj
				|| obj.equals(currentOrderedExecuteObject)) {
			command.run();
			return;
		}
		checkShutdown();
		TasksQueue tasksQueue = getTaskQueue(obj);
		boolean offerObj = true;
		synchronized (tasksQueue) {
			// Inject the event into the executor taskQueue
			if (first) {
				tasksQueue.tasksQueue.offerLast(command);
			} else {
				tasksQueue.tasksQueue.offerFirst(command);
			}

			if (tasksQueue.processingCompleted) {
				tasksQueue.processingCompleted = false;
			} else {
				offerObj = false;
			}
		}
		if (offerObj) {
			waitings.offer(obj);
		}
		addWorkerIfNecessary();
	}

	@Override
	public <T> Future<T> executeAllSync(final Callable<T> command) {
		checkShutdown();
		Future<T> future = this.singleExecutorService.submit(new Callable<T>() {

			@Override
			public T call() throws Exception {
				checkShutdown();
				synchronized (queues) {
					for (TasksQueue queue : queues.values()) {
						while (!queue.processingCompleted) {
							LockSupport.parkNanos(1);
						}
					}
					return command.call();
				}
			}
		});
		return future;
	}

	public long getCompletedTaskCount() {
		synchronized (workers) {
			long answer = completedTaskCount.get();
			for (Worker w : workers) {
				answer += w.completedTaskCount;
			}

			return answer;
		}
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public AtomicInteger getIdleWorkers() {
		return idleWorkers;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	private TasksQueue getTaskQueue(Object obj) {
		checkShutdown();
		synchronized (queues) {
			TasksQueue queue = queues.get(obj);
			if (queue != null) {
				return queue;
			}
			if (queue == null) {
				queue = new TasksQueue(obj);
				TasksQueue oldQueue = queues.put(obj, queue);
				if (oldQueue != null) {
					throw new RuntimeException("列队已经存在，旧列队可能会丢失！obj:" + obj
							+ " oldQueue:" + oldQueue + " newQueue:" + queue);
				}
			}
			return queue;
		}
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public Set<Worker> getWorkers() {
		return workers;
	}

	/**
	 * Returns a <tt>RunnableFuture</tt> for the given callable task.
	 * 
	 * @param callable
	 *            the callable task being wrapped
	 * @return a <tt>RunnableFuture</tt> which when run will call the underlying
	 *         callable and which, as a <tt>Future</tt>, will yield the
	 *         callable's result as its result and provide for cancellation of
	 *         the underlying task.
	 * @since 1.6
	 */
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new FutureTask<T>(callable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int prestartAllCoreThreads() {
		int answer = 0;
		synchronized (workers) {
			for (int i = getCorePoolSize() - workers.size(); i > 0; i--) {
				addWorker();
				answer++;
			}
		}
		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean prestartCoreThread() {
		synchronized (workers) {
			if (workers.size() < getCorePoolSize()) {
				addWorker();
				return true;
			} else {
				return false;
			}
		}
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	@Override
	public void shutdown() {
		if (shutdownStack != null) {
			return;
		}
		this.shutdownStack = new Exception();
		synchronized (workers) {
			for (int i = workers.size(); i > 0; i--) {
				waitings.offer(EXIT_SIGNAL);
			}
		}
		singleExecutorService.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		this.terminate = true;
		if (shutdownStack == null) {
			this.shutdownStack = new Exception();
		}
		synchronized (workers) {
			for (int i = workers.size(); i > 0; i--) {
				waitings.offer(EXIT_SIGNAL);
			}
		}
		if (!singleExecutorService.isShutdown()) {
			singleExecutorService.shutdown();
		}
		List<Runnable> ret = new LinkedList<>();
		for (TasksQueue queue : queues.values()) {
			ret.addAll(queue.tasksQueue);
			queue.tasksQueue.clear();
		}
		return ret;
	}

	@Override
	public void executePreferentially(Object obj, Runnable command) {
		execute(obj, command, false);
	}

	@Override
	public <T> Future<T> executePreferentially(Object obj, Callable<T> query) {
		return execute(obj, query, false);
	}
}
