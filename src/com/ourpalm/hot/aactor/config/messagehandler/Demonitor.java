package com.ourpalm.hot.aactor.config.messagehandler;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class Demonitor implements MessageHandler {
	public static interface Linkable {
		void SYSTEM_MESSAGE_PROFIX_unlink(ActorRef ar);
	}

	public final static String COMMAND = NamedMessageHandler.SYSTEM_MESSAGE_PROFIX
			+ "Demonitor";

	@Override
	public void handle(LocalSelfRef self, String command, Object[] arg) {
		ActorRef ar = (ActorRef) arg[0];
		self.removeLink(ar);
	}

}
