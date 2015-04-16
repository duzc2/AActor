package com.ourpalm.hot.aactor;
@FunctionalInterface
public interface MessageFilter {
	boolean testMessage(String title, Object[] message);
}
