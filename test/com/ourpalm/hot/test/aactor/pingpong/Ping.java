package com.ourpalm.hot.test.aactor.pingpong;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class Ping {
	private static final long TIME = 10000;
	private ActorRef pong;
	private ActorSystem system;
	private long startTime;
	private long startTimeMS;

	public Ping() {
		this.startTime = System.nanoTime();
		this.startTimeMS = System.currentTimeMillis();
	}

	@Activate
	private void init(ActorSystem system, SelfRef thisRef) {
		this.pong = system.createActor(Pong.class, thisRef);
		thisRef.link(pong);
		this.system = system;
	}

	@Mailbox
	public void tick(long time) {
		if (time < TIME) {
			// System.out.println("ping");
			pong.sendMessage("tick", time + 1);
		} else {
			system.stop();
			System.out.println(TIME + " Finished."
					+ (System.currentTimeMillis() - startTimeMS) + " "
					+ (System.nanoTime() - startTime));
		}
	}

	public static void main(String[] args) {
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		ActorRef actor = actorSystem.start(Ping.class);
		actor.sendMessage("tick", 0);
	}
}
