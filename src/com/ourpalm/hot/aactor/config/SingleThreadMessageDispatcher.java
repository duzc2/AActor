package com.ourpalm.hot.aactor.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.WeakHashMap;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.Mailbox;

public class SingleThreadMessageDispatcher implements MessageDispatcher {

	private WeakHashMap<Object, HashMap<String, Method>> mailboxMap = new WeakHashMap<>();

	@Override
	public void sendMessage(ActorRef ar, Object a, String command, Object[] arg)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HashMap<String, Method> mailboxCache = mailboxMap.get(a);
		if (mailboxCache == null) {
			mailboxCache = new HashMap<>();
			Method[] methods = a.getClass().getDeclaredMethods();
			for (Method method : methods) {
				Mailbox annotation = method.getAnnotation(Mailbox.class);
				if (annotation != null) {
					method.setAccessible(true);
					String name = annotation.value();
					if (name.equals("")) {
						name = method.getName();
					}
					if (mailboxCache.containsKey(name)) {
						throw new ActorException("Duplicated Mailbox " + name);
					}
					mailboxCache.put(name, method);
				}
			}
		}
		Method m = mailboxCache.get(command);
		if (m == null) {
			throw new ActorException("can't find mailbox " + command
					+ " on Actor:" + ar.getId() + " from class:" + a.getClass());
		}
		m.invoke(a, arg);
	}

}
