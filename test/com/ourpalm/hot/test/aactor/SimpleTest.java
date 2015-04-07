package com.ourpalm.hot.test.aactor;

import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Mailbox;

@Actor
public class SimpleTest {

	public static void main(String[] args) {
		ActorSystem actorSystem = new ActorSystemBuilder().build();
		actorSystem.start(SimpleTest.class, null);
		ActorRef actor = actorSystem.findActor(SimpleTest.class);
		actor.sendMessage("onMessage", "a message");
	}

	private ActorRef thisRef;
	private ActorSystem actorSystem;
	private ActorRef anotherActor;

	@Activate
	public void init(ActorSystem system, ActorRef thisRef) {
		this.thisRef = thisRef;
		this.actorSystem = system;
		System.out.println("Instance of SimpleTest is created.");
	}

	@Mailbox
	public void onMessage(String message) {
		System.out.println("onMessage:" + message);
		this.anotherActor = actorSystem.createActor(AnotherActor.class,7);
		anotherActor.sendMessage("abc");
		thisRef.sendMessage("anotherHandler", 8);
	}

	@Mailbox("anotherHandler")
	public void anotherHandler(int i) {
		System.out.println(thisRef.toString() + " anotherHandler:" + i);
	}

	@Mailbox("error")
	public void onError(ActorRef caller, Throwable t) {
		System.out.println("onError:" + t.getLocalizedMessage());
	}

	@Mailbox("tick")
	public void onTick(long deltaTime) {
		System.out.println("onTick");
	}

	@Actor
	public static class AnotherActor {
		private ActorRef thisRef;
		private int a;

		public AnotherActor(int a) {
			this.a = a;
		}

		@Activate
		private void init(ActorSystem as, ActorRef thisRef) {
			this.thisRef = thisRef;
			System.out.println("Instance of AnotherActor is created with a="
					+ a);
		}

		@Mailbox
		private void abc() {
			System.out.println(thisRef.toString() + " a=" + a);
		}
	}
}
