package com.ourpalm.hot.aactor.impl;

import java.util.Timer;
import java.util.TimerTask;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Deactivate;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.actors.TimerActor;

public class SimpleTimerActor implements TimerActor {

	private final Timer timer;
	@SuppressWarnings("unused")
	private SelfRef selfRef;
	@SuppressWarnings("unused")
	private ActorSystem system;

	private SimpleTimerActor(String name) {
		timer = new Timer(name, false);
	}

	private SimpleTimerActor() {
		timer = new Timer("System", false);
	}

	@Activate
	private void init(ActorSystem system, SelfRef selfRef) {
		this.system = system;
		this.selfRef = selfRef;
	}

	@Override
	public void timeout(ActorRef act, long delay, String callbackCommand,Object ... args) {
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				act.sendMessage(callbackCommand,args);
			}
		}, delay);
	}

	@Deactivate
	private void deactivate() {
		timer.cancel();
	}

}
