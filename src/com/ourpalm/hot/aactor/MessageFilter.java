package com.ourpalm.hot.aactor;

public interface MessageFilter {
	boolean testMessage(String title, Object[] message);
}
