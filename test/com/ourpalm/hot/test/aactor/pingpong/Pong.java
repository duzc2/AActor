package com.ourpalm.hot.test.aactor.pingpong;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.messagehandler.Exit;
import com.ourpalm.hot.aactor.config.messagehandler.Exit.ExitNoticable;
import com.ourpalm.hot.aactor.config.messagehandler.Kill;
import com.ourpalm.hot.aactor.config.messagehandler.Kill.Killable;

@Actor
public class Pong {
	private ActorRef ping;
	private SelfRef self;

	private Pong(ActorRef ping) {
		this.ping = ping;
		// throw new RuntimeException("test");
	}

	@Activate
	private void init(ActorSystem system, SelfRef thisRef) {
		this.self = thisRef;
		// throw new RuntimeException("test");
	}

	@Mailbox
	public void tick(long time) {
		System.out.println("pong " + time);
		ping.sendMessage("tick", time + 1);
		if (time > 2) {
			// ExitNoticable exitNoticable = ping.asType(ExitNoticable.class);
			// exitNoticable.SYSTEM_MESSAGE_PROFIX_exit(self, "type test",
			// null);

		}
		// if (time > 5) {
		// // self.sendMessage(Kill.COMMAND, "test");
		// Killable killable = self.asType(Kill.Killable.class);
		// killable.SYSTEM_MESSAGE_PROFIX_kill("test");
		// }
	}
}
