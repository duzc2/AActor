package com.ourpalm.hot.test.aactor;

import java.util.concurrent.locks.LockSupport;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class SimpleTest {

	public static void main(String[] args) throws InterruptedException {
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		actorSystem.start(SimpleTest.class);
		ActorRef actor = actorSystem.findActor(SimpleTest.class);
		actor.sendMessage("onMessage", "a message");
		Thread.sleep(2000);
		actorSystem.stop();
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
		this.anotherActor = actorSystem.createActor(AnotherActor.class, 7);
		anotherActor.sendMessage("abc");
		thisRef.sendMessage("anotherHandler", 8);
		LockSupport.parkNanos(1000/* s */ * 1000/* m */ * 1000 /* n */);
		anotherActor.sendMessage("print","a message");
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

		@Mailbox
		private void print(String msg) {
			System.out.println(thisRef.toString() + " print:" + msg);
		}
	}
}
