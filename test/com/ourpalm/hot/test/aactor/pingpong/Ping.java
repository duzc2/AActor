package com.ourpalm.hot.test.aactor.pingpong;

import java.util.Arrays;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.messagehandler.Kill.Killable;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class Ping {
	private static final long TIME = 10000;
	private ActorRef pong;
	private ActorSystem system;
	private long startTime;
	private long startTimeMS;
	private SelfRef selfRef;

	public Ping() {
		this.startTime = System.nanoTime();
		this.startTimeMS = System.currentTimeMillis();
	}

	@Activate
	private void init(ActorSystem system, SelfRef selfRef) {
		selfRef.getContext().setErrorHandler((t, command, arg) -> {
			System.err.println(command + " : " + Arrays.toString(arg));
			t.printStackTrace();
			system.stop();
		});
		this.pong = system.createActorAndLink(selfRef, Pong.class, selfRef);
		// thisRef.link(pong);
		this.system = system;
		this.selfRef = selfRef;
	}

	@Mailbox
	public void tick(long time) {
		if (time < TIME) {
			System.out.println("ping " + time);
			pong.sendMessage("tick", time);
//			Killable killable = pong.asType(Killable.class);
//			killable.SYSTEM_MESSAGE_PROFIX_kill("kill test");
			// selfRef.demonitor(pong);
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
