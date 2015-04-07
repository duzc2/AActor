package com.ourpalm.hot.aactor.config;

import com.ourpalm.hot.aactor.impl.SyncActorBuilder;

public class ConfigureLoader {

	public static ActorSystemConfigure loadConfigure() {
		ActorSystemConfigure c = new ActorSystemConfigure(
				new SyncActorBuilder(), new SingleThreadMessageDispatcher());
		return c;
	}

}
