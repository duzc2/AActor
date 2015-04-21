package com.ourpalm.hot.aactor.config.messagehandler;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class Exit implements MessageHandler {
	public final static String COMMAND = SystemMessageHandler.SYSTEM_MESSAGE_PROFIX
			+ "exit";

	@Override
	public void handle(LocalSelfRef self, String command, Object[] arg) {
		throw new ActorException(arg[0] + " exited.");
	}

}
