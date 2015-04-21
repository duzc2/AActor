package com.ourpalm.hot.aactor.config.messagehandler;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class Link implements MessageHandler {
	public final static String COMMAND = SystemMessageHandler.SYSTEM_MESSAGE_PROFIX
			+ "link";

	@Override
	public void handle(LocalSelfRef self, String command, Object[] arg) {
		ActorRef ar = (ActorRef) arg[0];
		self.addLink(ar);
	}

}
