package com.ourpalm.hot.aactor;
@FunctionalInterface
public interface ErrorHandler {

	void onError(Throwable t, String command, Object[] arg);

}
