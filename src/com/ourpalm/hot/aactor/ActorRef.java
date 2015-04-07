package com.ourpalm.hot.aactor;

public interface ActorRef {
	void sendMessage(String command, Object... arg);

	<T> T asType(Class<T> clazz);
}
