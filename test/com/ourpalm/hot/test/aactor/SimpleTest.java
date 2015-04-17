package com.ourpalm.hot.test.aactor;

import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorContext;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Deactivate;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class SimpleTest {

	public static void main(String[] args) throws InterruptedException {
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		ActorRef actor = actorSystem.start(SimpleTest.class);
		actor.sendMessage("onMessage", "a message");
		actorSystem.detachActor(actor);
		Thread.sleep(2000);
		Thread.sleep(2000);
		actorSystem.stop();
	}

	private SelfRef selfRef;
	private ActorSystem actorSystem;
	private ActorRef anotherActor;

	@Activate
	public void init(ActorSystem system, SelfRef thisRef) {
		this.selfRef = thisRef;
		this.actorSystem = system;
		System.out.println("Instance of SimpleTest is created.");
	}

	@Deactivate
	private void destory() {
		actorSystem.detachActor(anotherActor);
		System.out.println(selfRef.toString() + " destory");
	}

	@Mailbox
	public void onMessage(String message) {
		System.err.println("onMessage:" + message);
		this.anotherActor = actorSystem.createActor(AnotherActor.class, 7);
		anotherActor.sendMessage("abc");
		anotherActor.sendMessage("abc");
		anotherActor.sendMessage("abc");
		anotherActor.sendMessage("abc");
		anotherActor.sendMessage("abc");
		selfRef.sendMessage("anotherHandler", 8);
		selfRef.sendMessage("anotherHandler", 8);
		selfRef.sendMessage("anotherHandler", 8);
		selfRef.sendMessage("anotherHandler", 8);
		anotherActor.sendMessage("error", selfRef);
		LockSupport.parkNanos(1000/* s */* 1000/* m */* 1000 /* n */);
		anotherActor.sendMessage("print", "a message");
		anotherActor.sendMessage("noMethod", "a default message handler");
	}

	@Mailbox("anotherHandler")
	public void anotherHandler(int i) {
		System.err.println(selfRef.toString() + " anotherHandler:" + i);
	}

	@Actor
	public static class AnotherActor {
		private SelfRef thisRef;
		private int a;

		public AnotherActor(int a) {
			this.a = a;
		}

		@Activate
		private void init(ActorSystem as, SelfRef thisRef) {
			this.thisRef = thisRef;
			System.err.println("Instance of AnotherActor is created with a="
					+ a);
			ActorContext context = thisRef.getContext();
			context.setErrorHandler((t, command, arg) -> {
				t.printStackTrace(System.out);
				System.err.println("Got a exception.");
			});
			context.setDefaultMessageHandler((command, arg) -> {
				System.err.println("default message hander:" + command + " :"
						+ Arrays.toString(arg));
			});
		}

		@Deactivate
		private void destory() {
			System.out.println(thisRef.toString() + " destory");
		}

		@Mailbox("error")
		public void error(ActorRef aref) {
			System.out.println("error from " + aref);
			aref.sendMessage("anotherHandler", 7);
			// /aref.sendMessage("anotherHandler");
			throw new NullPointerException("test error");
		}

		@Mailbox
		private void abc() {
			System.err.println(thisRef.toString() + " a=" + a);
		}

		@Mailbox
		private void print(String msg) {
			System.err.println(thisRef.toString() + " print:" + msg);
		}
	}
}
