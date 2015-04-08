package com.ourpalm.hot.aactor;

public interface ErrorHandler {

	void onError(Throwable t, String command, Object[] arg);

}
