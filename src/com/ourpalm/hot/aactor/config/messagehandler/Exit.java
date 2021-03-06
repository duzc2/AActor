package com.ourpalm.hot.aactor.config.messagehandler;

import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.impl.LocalSelfRef;

public class Exit implements MessageHandler {
	public static interface ExitNoticable {
		public String NORMAL = "normal";
		public String SHUTDOWN = "shutdown";

		public void SYSTEM_MESSAGE_PROFIX_exit(ActorRef who, String why,
				Throwable t);
	}

	public final static String COMMAND = NamedMessageHandler.SYSTEM_MESSAGE_PROFIX
			+ "exit";

	/**
	 * arg: [0] ActorRef who [1] String why [2] Throwable
	 */
	@Override
	public void handle(LocalSelfRef self, String command, Object[] arg) {
		if (arg.length == 0) {
			throw new ActorException("Some one actor exited.");
		} else {
			try {
				self.unlink((ActorRef) arg[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (arg.length == 1) {
			throw new ActorException(arg[0] + " exited.");
		} else if (arg.length == 2) {
			if (!ExitNoticable.NORMAL.equals(arg[1])
					&& !ExitNoticable.SHUTDOWN.equals(arg[1])) {
				throw new ActorException(arg[0] + " exited,Because of \""
						+ arg[1] + "\"");
			}
		} else if (arg.length == 3) {
			throw new ActorException(arg[0] + " exited,Because of \"" + arg[1]
					+ "\"", (Throwable) arg[2]);
		}
	}

}
