package com.ourpalm.hot.aactor.impl;

import java.util.Timer;
import java.util.TimerTask;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Deactivate;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.actors.TimerActor;

public class DefaultTimerActor implements TimerActor {

	private final Timer timer;
	@SuppressWarnings("unused")
	private SelfRef selfRef;
	@SuppressWarnings("unused")
	private ActorSystem system;

	private DefaultTimerActor(String name) {
		timer = new Timer(name, false);
	}

	private DefaultTimerActor() {
		timer = new Timer("System", false);
	}

	@Activate
	private void init(ActorSystem system, SelfRef selfRef) {
		this.system = system;
		this.selfRef = selfRef;
	}

	@Override
	public void timeout(ActorRef act, long delay, String callbackCommand) {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				act.sendMessage(callbackCommand);
			}
		}, delay);
	}

	@Deactivate
	private void deactivate() {
		timer.cancel();
	}

}
