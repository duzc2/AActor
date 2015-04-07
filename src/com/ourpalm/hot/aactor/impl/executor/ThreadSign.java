/**
 * 
 */
package com.ourpalm.hot.aactor.impl.executor;

/**
 * 用于标注当前线程是否OrderedExecutor线程
 * 
 * @author 杜天微
 * 
 */
class ThreadSign extends ThreadLocal<Object> {
	static ThreadSign instance = new ThreadSign();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ThreadLocal#initialValue()
	 */
	@Override
	protected Object initialValue() {
		return null;
	}

	public static Object getCurrentOrderedExecuteObject() {
		return instance.get();
	}
}
