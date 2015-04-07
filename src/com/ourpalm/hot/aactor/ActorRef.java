package com.ourpalm.hot.aactor;

public interface ActorRef {
	void sendMessage(String command, Object... arg);

	String getId();
}
