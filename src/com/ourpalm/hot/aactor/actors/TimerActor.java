package com.ourpalm.hot.aactor.actors;

import com.ourpalm.hot.aactor.Actor;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.Mailbox;

@Actor
public interface TimerActor {
	@Mailbox
	public void timeout(ActorRef act, long delay, String callbackCommand);
}
