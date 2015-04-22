package com.ourpalm.hot.aactor.config.messagehandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ourpalm.hot.aactor.DefaultMessageHandler;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class NamedMessageHandler implements DefaultMessageHandler {
	public static final String SYSTEM_MESSAGE_PROFIX = "SYSTEM_MESSAGE_PROFIX_";
	public static Map<String, MessageHandler> staticHandlers;
	static {
		HashMap<String, MessageHandler> _handlers = new HashMap<>();
		_handlers.put(Monitor.COMMAND, new Monitor());
		_handlers.put(Demonitor.COMMAND, new Demonitor());
		_handlers.put(Exit.COMMAND, new Exit());
		_handlers.put(Kill.COMMAND, new Kill());
		staticHandlers = Collections.unmodifiableMap(_handlers);
	}

	private LocalSelfRef self;
	private HashMap<String, MessageHandler> handlers;

	public NamedMessageHandler(LocalSelfRef self) {
		this.handlers = new HashMap<>(staticHandlers);
		this.self = self;
	}

	@Override
	public void onMessage(String command, Object[] arg) {
		MessageHandler messageHandler = handlers.get(command);
		if (messageHandler != null) {
			messageHandler.handle(self, command, arg);
		}
	}

	public HashMap<String, MessageHandler> getHandlers() {
		return handlers;
	}
}
