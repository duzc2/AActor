package com.ourpalm.hot.aactor.config.messagehandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ourpalm.hot.aactor.DefaultMessageHandler;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class SystemMessageHandler implements DefaultMessageHandler {
	public static final String SYSTEM_MESSAGE_PROFIX = "SYSTEM_MESSAGE_PROFIX_";
	public static Map<String, MessageHandler> handlers;
	static {
		HashMap<String, MessageHandler> _handlers = new HashMap<>();
		_handlers.put(Link.COMMAND, new Link());
		_handlers.put(Unlink.COMMAND, new Unlink());
		_handlers.put(Exit.COMMAND, new Exit());
		_handlers.put(Kill.COMMAND, new Kill());
		handlers = Collections.unmodifiableMap(_handlers);
	}

	private LocalSelfRef self;

	public SystemMessageHandler(LocalSelfRef self) {
		this.self = self;
	}

	@Override
	public void onMessage(String command, Object[] arg) {
		MessageHandler messageHandler = handlers.get(command);
		if (messageHandler != null) {
			messageHandler.handle(self, command, arg);
		}
	}

}
