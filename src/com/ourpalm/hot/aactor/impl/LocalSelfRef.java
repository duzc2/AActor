package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.ourpalm.hot.aactor.ActorContext;
import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Command;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;

public class LocalSelfRef extends LocalActorRef implements SelfRef {

	private Object obj;
	private ActorContext context;
	private HashMap<String, Method> mailboxCache;

	public LocalSelfRef(Object obj, String id,
			Map<String, LocalSelfRef> refMap, ActorSystem actorSystem) {
		super(id, refMap, actorSystem);
		this.obj = obj;
		this.mailboxCache = new HashMap<>();
		Method[] methods = obj.getClass().getDeclaredMethods();
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
				Class<?> returnType = method.getReturnType();
				if (returnType == Void.class || returnType == void.class) {
					mailboxCache.put(name, method);
				} else {
					throw new ActorException(
							"Mailbox return type must be void or Future:"
									+ name);
				}
			}
		}
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public void setContext(ActorContext context) {
		this.context = context;
	}

	@Override
	public ActorContext getContext() {
		return context;
	}

	public boolean haveContext() {
		return context != null;
	}

	@Override
	public void sendMessage(String command, Object... arg) {
		try {
			sendMessage_(command, arg);
		} catch (Throwable t) {
			error(t, command, arg);
		}
	}

	@Override
	public void error(Throwable t, String command, Object[] arg) {
		if (haveContext() && getContext().getErrorHandler() != null) {
			getContext().getErrorHandler().onError(t, command, arg);
		} else {
			throw new RuntimeException(t);
		}
	}

	private void sendMessage_(String command, Object[] arg) throws Exception {
		MessageDispatcher dispatcher = actorSystem.getConfigure()
				.getDispatcher();
		dispatcher.sendMessage(this, getObj(), command, arg);
	}

	@Override
	public void call(String command, Object... arg) {
		Method m = mailboxCache.get(command);
		try {
			if (m == null) {
				if (haveContext()
						&& getContext().getDefaultMessageHandler() != null) {
					getContext().getDefaultMessageHandler().onMessage(command,
							arg);
				} else {
					throw new ActorException("can't find mailbox \"" + command
							+ "\" on Actor:" + toString() + " from class:"
							+ getObj().getClass());
				}
			} else {
				// 如果消息不被接受，则加入列队
				if (haveContext() && getContext().getMessageFilter() != null) {
					if (!getContext().getMessageFilter().testMessage(command,
							arg)) {
						getContext().getMessageQueue().add(
								new Command(command, arg));
						return;
					}
				}
				try {
					m.invoke(getObj(), arg);
				} catch (IllegalArgumentException e) {
					error(e, command, arg);
				}
				reDeliver();
			}
		} catch (Exception e) {
			error(new ActorException("error on invoke method " + m.toString()
					+ " on actor:" + toString(), e), command, arg);
		}
	}

	private void reDeliver() {
		if (haveContext()) {
			LinkedList<Command> messageQueue = getContext().getMessageQueue();
			ArrayList<Command> newMessageQueue = new ArrayList<>(messageQueue);
			messageQueue.clear();
			for (Command command : newMessageQueue) {
				call(command.getCommand(), command.getArgs());
			}
		}
	}
}
