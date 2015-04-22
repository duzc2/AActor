package com.ourpalm.hot.aactor.config.messagehandler;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class Monitor implements MessageHandler {
	public static interface Linkable {
		void SYSTEM_MESSAGE_PROFIX_link(ActorRef ar);
	}

	public final static String COMMAND = NamedMessageHandler.SYSTEM_MESSAGE_PROFIX
			+ "Monitor";

	@Override
	public void handle(LocalSelfRef self, String command, Object[] arg) {
		ActorRef ar = (ActorRef) arg[0];
		self.addLink(ar);
	}

}
