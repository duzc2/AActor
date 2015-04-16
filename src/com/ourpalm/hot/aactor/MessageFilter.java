package com.ourpalm.hot.aactor;

@FunctionalInterface
public interface MessageFilter {
	MessageFilter defaultMessageFilter = new MessageFilter() {

		@Override
		public boolean testMessage(String title, Object[] message) {
			return true;
		}
	};

	boolean testMessage(String title, Object[] message);
}
