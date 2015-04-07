package com.ourpalm.hot.aactor.impl.executor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 针对特定对象，有序的指令执行器
 * 
 * 在调用此实现的 shutdown 方法之后再执行的所有execute系列方法将抛出IllegalStateException异常。
 * 
 * @author 杜天微
 * 
 */
public interface OrderedExecutor {
	/**
	 * 异步执行指令
	 * 
	 * 在执行指令时，此执行器对obj的指令顺序。
	 * 
	 * @param obj
	 * @param command
	 */
	public void execute(Object obj, Runnable command);

	/**
	 * 相对于所有对象执行单线程指令
	 * 
	 * @param command
	 */
	public <T> Future<T> executeAllSync(Callable<T> command);

	/**
	 * 
	 * @param obj
	 * @param query
	 * @return
	 */
	public <T> Future<T> execute(Object obj, Callable<T> query);

	/**
	 * 启动最大线程
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor.prestartAllCoreThreads()
	 * @return
	 */
	public int prestartAllCoreThreads();

	/**
	 * 启动最少线程
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor.prestartCoreThread()
	 * @return
	 */
	public boolean prestartCoreThread();

	/**
	 * 关闭执行器
	 * 
	 * 此方法触发执行器的关闭过程，并尽快返回，执行器将在执行完所有已经添加的任务之后关闭。
	 * 
	 * 在执行此方法之后调用execute系列方法将抛出IllegalStateException异常。
	 */
	public void shutdown();

	/**
	 * 立即停止此执行器
	 * 
	 * 此方法触发执行器的关闭过程，并取消所有未执行的计划。
	 * 
	 * 在执行此方法之后调用execute系列方法将抛出IllegalStateException异常。
	 * 
	 * @return
	 */
	public List<Runnable> shutdownNow();
}
