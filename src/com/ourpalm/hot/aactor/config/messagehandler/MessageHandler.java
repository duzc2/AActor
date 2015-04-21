package com.ourpalm.hot.aactor.config.messagehandler;

import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public interface MessageHandler {

	void handle(LocalSelfRef self, String command, Object[] arg);

}
