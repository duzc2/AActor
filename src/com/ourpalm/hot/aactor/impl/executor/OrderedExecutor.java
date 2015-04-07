package com.ourpalm.hot.aactor.impl.executor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * ����ض����������ָ��ִ����
 * 
 * �ڵ��ô�ʵ�ֵ� shutdown ����֮����ִ�е�����executeϵ�з������׳�IllegalStateException�쳣��
 * 
 * @author ����΢
 * 
 */
public interface OrderedExecutor {
	/**
	 * �첽ִ��ָ��
	 * 
	 * ��ִ��ָ��ʱ����ִ������obj��ָ��˳��
	 * 
	 * @param obj
	 * @param command
	 */
	public void execute(Object obj, Runnable command);

	/**
	 * ��������ж���ִ�е��߳�ָ��
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
	 * ��������߳�
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor.prestartAllCoreThreads()
	 * @return
	 */
	public int prestartAllCoreThreads();

	/**
	 * ���������߳�
	 * 
	 * @see java.util.concurrent.ThreadPoolExecutor.prestartCoreThread()
	 * @return
	 */
	public boolean prestartCoreThread();

	/**
	 * �ر�ִ����
	 * 
	 * �˷�������ִ�����Ĺرչ��̣������췵�أ�ִ��������ִ���������Ѿ���ӵ�����֮��رա�
	 * 
	 * ��ִ�д˷���֮�����executeϵ�з������׳�IllegalStateException�쳣��
	 */
	public void shutdown();

	/**
	 * ����ֹͣ��ִ����
	 * 
	 * �˷�������ִ�����Ĺرչ��̣���ȡ������δִ�еļƻ���
	 * 
	 * ��ִ�д˷���֮�����executeϵ�з������׳�IllegalStateException�쳣��
	 * 
	 * @return
	 */
	public List<Runnable> shutdownNow();
}
