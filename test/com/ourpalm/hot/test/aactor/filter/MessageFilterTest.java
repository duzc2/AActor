package com.ourpalm.hot.test.aactor.filter;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.MessageFilter;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class MessageFilterTest {

	public static void main(String[] args) throws InterruptedException {
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		actorSystem.start(MessageFilterTest.class);
		ActorRef actor = actorSystem.findActor(MessageFilterTest.class);
		Thread.sleep(5000);
		actorSystem.stop();
	}

	private SelfRef selfRef;
	private ActorSystem actorSystem;
	private ActorRef anotherActor;

	@Activate
	public void init(ActorSystem system, SelfRef thisRef) {
		this.selfRef = thisRef;
		this.actorSystem = system;
		anotherActor = actorSystem.createActor(AnotherActor.class);
		anotherActor.sendMessage("a", 1);
		anotherActor.sendMessage("b", 1);
		anotherActor.sendMessage("stopA");
		anotherActor.sendMessage("a", 2);
		anotherActor.sendMessage("b", 2);
		anotherActor.sendMessage("openA");
		anotherActor.sendMessage("a", 3);
		anotherActor.sendMessage("b", 3);
	}

	@Actor
	public static class AnotherActor {
		private SelfRef thisRef;

		public AnotherActor() {
		}

		@Activate
		private void init(ActorSystem as, SelfRef thisRef) {
			this.thisRef = thisRef;
		}

		@Mailbox
		public void b(int i) {
			System.out.println("b:" + i);
		}
		@Mailbox
		public void a(int i) {
			System.out.println("a:" + i);
		}

		@Mailbox
		public void stopA() {
			System.out.println("stopA");
			thisRef.getContext().setMessageFilter(new MessageFilter() {

				@Override
				public boolean testMessage(String title, Object[] message) {
					return !"a".equals(title);
				}
			});
		}

		@Mailbox
		public void openA() {
			System.out.println("openA");
			thisRef.getContext().setMessageFilter(null);
		}

	}
}
