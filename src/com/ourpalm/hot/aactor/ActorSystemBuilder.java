package com.ourpalm.hot.aactor;

import com.ourpalm.hot.aactor.impl.DefaultActorSystem;

public class ActorSystemBuilder {
	public ActorSystem build(){
		return new DefaultActorSystem();
	}
}
