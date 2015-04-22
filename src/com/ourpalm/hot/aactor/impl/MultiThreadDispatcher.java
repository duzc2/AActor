package com.ourpalm.hot.aactor.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.SelfRef;
import com.ourpalm.hot.aactor.config.MessageDispatcher;
import com.ourpalm.hot.aactor.config.messagehandler.Link;
import com.ourpalm.hot.aactor.config.messagehandler.Unlink;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutor;
import com.ourpalm.hot.aactor.impl.executor.OrderedExecutors;

public class MultiThreadDispatcher implements MessageDispatcher {
	private OrderedExecutor excutor;
	private ActorSystem as;
	private AtomicLong queuedMessage = new AtomicLong();

	public MultiThreadDispatcher() {
	}

	@Override
	public void sendMessage(SelfRef ar, Object a, String command, Object[] arg)
			throws Exception {
		queuedMessage.incrementAndGet();
		excutor.execute(a, () -> ar.call(command, arg));
	}

	@Override
	public void close() {
		excutor.shutdownNow();
	}

	@Override
	public ActorRef createActor(Class<?> clazz, Object[] args) {
		ActorRef ar = as.getConfigure().getActorBuilder()
				.buildActorRef(clazz, args);
		return ar;
	}

	@Override
	public void detachActor(ActorRef ref) {
		as.getConfigure().getActorBuilder().detachActor(ref);
	}

	@Override
	public void init(ActorSystem as) {
		int processors = Runtime.getRuntime().availableProcessors();
		this.excutor = OrderedExecutors.getOrderedExecutor(1, processors,
				60 * 1000, new ThreadFactory() {
					AtomicLong id = new AtomicLong();

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("MultiThreadDispatcher-"
								+ id.getAndIncrement());
						return t;
					}
				});
		excutor.prestartCoreThread();
		this.as = as;
	}

	@Override
	public ActorRef findActor(Class<?> class1) {
		return as.getConfigure().getActorBuilder().findActor(class1);
	}

	@Override
	public ActorRef findActorById(String actorId) {
		return as.getConfigure().getActorBuilder().findActorById(actorId);
	}

	@Override
	public long queuedMessage() {
		return queuedMessage.get();
	}

	@Override
	public void decrementQueuedMessage() {
		queuedMessage.decrementAndGet();
	}

	@Override
	public void link(SelfRef self, ActorRef ar) {
		self.sendMessage(Link.COMMAND, ar);
		ar.sendMessage(Link.COMMAND, self);
	}

	@Override
	public void unlink(SelfRef self, ActorRef ar) {
		if (self == null || ar == null) {
			return;
		}
		self.sendMessage(Unlink.COMMAND, ar);
		ar.sendMessage(Unlink.COMMAND, self);
	}

	@Override
	public void monitor(SelfRef localSelfRef, ActorRef ar) {
		ar.sendMessage(Link.COMMAND, localSelfRef);
	}

	@Override
	public void unmonitor(SelfRef self, ActorRef ar) {
		if (self == null || ar == null) {
			return;
		}
		ar.sendMessage(Unlink.COMMAND, self);
	}

	@Override
	public ActorRef createActorAndLink(ActorRef self, Class<?> clazz,
			Object[] args) {
		ActorRef ar = as.getConfigure().getActorBuilder()
				.buildActorRefWithLink(self, clazz, args);
		return ar;
	}
}
