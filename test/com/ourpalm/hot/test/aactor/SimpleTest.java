package com.ourpalm.hot.test.aactor;

import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Context;
import com.ourpalm.hot.aactor.DefaultMessageHandler;
import com.ourpalm.hot.aactor.ErrorHandler;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.SelfRef;
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

	private SelfRef selfRef;
	private ActorSystem actorSystem;
	private ActorRef anotherActor;

	@Activate
	public void init(ActorSystem system, SelfRef thisRef) {
		this.selfRef = thisRef;
		this.actorSystem = system;
		System.out.println("Instance of SimpleTest is created.");
	}

	@Mailbox
	public void onMessage(String message) {
		System.out.println("onMessage:" + message);
		this.anotherActor = actorSystem.createActor(AnotherActor.class, 7);
		anotherActor.sendMessage("abc");
		selfRef.sendMessage("anotherHandler", 8);
		anotherActor.sendMessage("error");
		LockSupport.parkNanos(1000/* s */* 1000/* m */* 1000 /* n */);
		anotherActor.sendMessage("print", "a message");
		anotherActor.sendMessage("noMethod", "a default message handler");
	}

	@Mailbox("anotherHandler")
	public void anotherHandler(int i) {
		System.out.println(selfRef.toString() + " anotherHandler:" + i);
	}

	@Mailbox("tick")
	public void onTick(long deltaTime) {
		System.out.println("onTick");
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
			System.out.println("Instance of AnotherActor is created with a="
					+ a);
			Context context = new Context();
			context.setErrorHandler(new ErrorHandler() {

				@Override
				public void onError(Throwable t, String command, Object[] arg) {
					t.printStackTrace(System.out);
					System.out.println("Got a exception.");
				}
			});
			context.setDefaultMessageHandler(new DefaultMessageHandler() {

				@Override
				public void onMessage(String command, Object[] arg) {
					System.out.println("default message hander:" + command
							+ " :" + Arrays.toString(arg));
				}
			});
			thisRef.setContext(context);
		}

		@Mailbox("error")
		public void error() {
			System.out.println("error");
			throw new NullPointerException("test error");
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
