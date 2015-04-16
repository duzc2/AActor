package com.ourpalm.hot.test.aactor.register;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class RegisterTest {

	public static void main(String[] args) throws InterruptedException {
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		actorSystem.start(RegisterTest.class);
		ActorRef ar = actorSystem.whereis("test");
		ar.sendMessage("a");
		ar.sendMessage("a");
		ar = actorSystem.whereis("test");
		System.out.println(ar == null);
		Thread.sleep(5000);
		actorSystem.stop();
	}

	private ActorSystem actorSystem;

	@Activate
	public void init(ActorSystem system, SelfRef thisRef) {
		this.actorSystem = system;
		actorSystem.register("test", thisRef);
	}

	@Mailbox
	public void a() {
		System.out.println("a");
		actorSystem.unregister("test");
	}
}
