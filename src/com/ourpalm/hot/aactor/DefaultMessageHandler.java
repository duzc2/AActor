package com.ourpalm.hot.aactor;

@FunctionalInterface
public interface DefaultMessageHandler {

	void onMessage(String command, Object[] arg);

}
