package com.ourpalm.hot.aactor.impl;

import java.util.Map;

import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Context;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;

public class LocalSelfRef extends LocalActorRef implements SelfRef {

	private Object obj;
	private Context context;

	public LocalSelfRef(Object obj, String id,
			Map<String, LocalSelfRef> refMap, ActorSystem actorSystem) {
		super(id, refMap, actorSystem);
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Context getContext() {
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
}
