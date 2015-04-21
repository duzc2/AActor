package com.ourpalm.hot.test.aactor.pingpong;

import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.Mailbox;

@Actor
public class Pong {
	private ActorRef ping;

	public Pong(ActorRef ping) {
		this.ping = ping;
	}

	@Mailbox
	public void tick(long time) {
		// System.out.println("pong");
		ping.sendMessage("tick", time);
		throw new RuntimeException("aa");
	}
}
