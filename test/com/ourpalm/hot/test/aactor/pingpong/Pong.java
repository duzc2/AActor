package com.ourpalm.hot.test.aactor.pingpong;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.messagehandler.Kill;

@Actor
public class Pong {
	private ActorRef ping;
	private SelfRef self;

	public Pong(ActorRef ping) {
		this.ping = ping;
		//throw new RuntimeException("test");
	}

	@Activate
	private void init(ActorSystem system, SelfRef thisRef) {
		this.self = thisRef;
		throw new RuntimeException("test");
	}

	@Mailbox
	public void tick(long time) {
		// System.out.println("pong");
		ping.sendMessage("tick", time);
		// self.sendMessage(Kill.COMMAND, "test");
	}
}
