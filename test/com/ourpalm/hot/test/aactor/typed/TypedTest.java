package com.ourpalm.hot.test.aactor.typed;

import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.ActorSystemBuilder;
import com.ourpalm.hot.aactor.Mailbox;
import com.ourpalm.hot.aactor.impl.MultiThreadDispatcher;

@Actor
public class TypedTest implements ITypedTest {

	public static void main(String[] args) throws InterruptedException {
		ActorSystem actorSystem = new ActorSystemBuilder()
				.setMessageDispatcher(new MultiThreadDispatcher()).build();
		actorSystem.start(TypedTest.class);
		ActorRef actor = actorSystem.findActor(TypedTest.class);
		ITypedTest typedTest = actor.asType(ITypedTest.class);
		typedTest.foo("call from main");
		Thread.sleep(2000);
		actorSystem.stop();
	}

	@Override
	@Mailbox
	public void foo(String string) {
		System.out.println("foo:" + string);
	}
}
