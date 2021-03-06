package com.ourpalm.hot.aactor.config.messagehandler;

import java.util.HashSet;
import java.util.Set;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class Kill implements MessageHandler {
	public static interface Killable {
		public void SYSTEM_MESSAGE_PROFIX_kill(String reason);
	}

	public final static String COMMAND = NamedMessageHandler.SYSTEM_MESSAGE_PROFIX
			+ "kill";

	@Override
	public void handle(LocalSelfRef self, String command, Object[] arg) {
		String reason = (String) arg[0];
		ActorSystem actorSystem = self.getActorSystem();
		actorSystem.detachActor(self);
		Set<ActorRef> refs = new HashSet<>(self.getLinked());
		self.getLinked().clear();
		for (ActorRef ar : refs) {
			ar.sendMessage(Exit.COMMAND, self, reason);
		}
	}
}
