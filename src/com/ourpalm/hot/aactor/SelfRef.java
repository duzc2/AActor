package com.ourpalm.hot.aactor;

public interface SelfRef extends ActorRef {

	ActorSystem getActorSystem();

	ActorContext getContext();

	void error(Throwable t, String command, Object[] arg);

	void call(String command, Object... arg);

	void link(ActorRef ar);

	void unlink(ActorRef ar);

	void monitor(ActorRef ar);

	void demonitor(ActorRef ar);
}
