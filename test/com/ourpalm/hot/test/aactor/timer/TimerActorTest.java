package com.ourpalm.hot.test.aactor.timer;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.actors.TimerActor;
import com.ourpalm.hot.aactor.impl.SimpleTimerActor;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class TimerActorTest {

	public static void main(String[] args){
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		ActorRef actor = actorSystem.start(TimerActorTest.class);
	}

	private ActorSystem system;
	private TimerActor t1;
	private SelfRef selfRef;

	@Activate
	private void init(ActorSystem system, SelfRef selfRef) {
		this.system = system;
		this.selfRef = selfRef;
		this.t1 = system.createActorAndLink(selfRef, SimpleTimerActor.class,
				"test").asType(TimerActor.class);
		t1.timeout(selfRef, 10, "repeat","WWWWW");
		selfRef.timeout(1000, "timeout");
	}

	@Mailbox
	private void timeout() {
		System.out.println("timeout");
		system.stop();
	}

	@Mailbox
	private void repeat(String w) {
		System.out.println("repeat" + w);
		t1.timeout(selfRef, 1000, "repeat",w);
	}
}
