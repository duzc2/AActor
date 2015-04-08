package com.ourpalm.hot.aactor;


public interface SelfRef extends ActorRef {
	void setContext(Context context);

	Context getContext();

	void error(Throwable t, String command, Object[] arg);
	
	void call(String command, Object[] arg);
}
