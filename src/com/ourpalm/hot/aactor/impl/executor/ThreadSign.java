/**
 * 
 */
package com.ourpalm.hot.aactor.impl.executor;

/**
 * ���ڱ�ע��ǰ�߳��Ƿ�OrderedExecutor�߳�
 * 
 * @author ����΢
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
