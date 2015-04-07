package com.ourpalm.hot.aactor.config;

import com.ourpalm.hot.aactor.impl.SingleThreadDispatcher;
import com.ourpalm.hot.aactor.impl.DefaultActorBuilder;

public class ConfigureLoader {

	public static ActorSystemConfigure loadConfigure() {
		ActorSystemConfigure c = new ActorSystemConfigure(
				new DefaultActorBuilder(), new SingleThreadDispatcher());
		return c;
	}

}
