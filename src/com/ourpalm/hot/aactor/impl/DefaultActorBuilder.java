package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.Deactivate;
import com.ourpalm.hot.aactor.config.ActorBuilder;
import com.ourpalm.hot.aactor.config.messagehandler.Exit;
import com.ourpalm.hot.aactor.config.messagehandler.Monitor;

/**
 * 
 * @author g
 *
 */
public class DefaultActorBuilder implements ActorBuilder {

	private AtomicLong idgen = new AtomicLong();
	private ConcurrentHashMap<String, LocalSelfRef> refMap = new ConcurrentHashMap<>();
	private ActorSystem actorSystem;

	@Override
	public ActorRef buildActorRefWithLink(ActorRef linker, Class<?> root,
			Object[] args) {
		LocalSelfRef self = null;
		try {
			self = makeSelfRef(root, args);
			linker.sendMessage(Monitor.COMMAND, self);
			self.link(linker);
			initActor(self);
		} catch (Throwable t) {
			ActorException e = new ActorException("Can't initialize actor:"
					+ t.getMessage(), t);
			linker.sendMessage(Exit.COMMAND, self, e.getMessage(), e);
			throw e;
		}
		refMap.put(self.toString(), self);
		return self;
	}

	@Override
	public ActorRef buildActorRef(Class<?> root, Object[] args) {
		LocalSelfRef self = makeSelfRef(root, args);
		try {
			initActor(self);
		} catch (Throwable t) {
			throw new ActorException(
					"Can't initialize actor:" + t.getMessage(), t);
		}
		refMap.put(self.toString(), self);
		return self;
	}

	private LocalSelfRef makeSelfRef(Class<?> root, Object[] args) {
		Constructor<?>[] declaredConstructors = root.getDeclaredConstructors();
		Constructor<?> constructor = null;
		if (args == null) {
			try {
				constructor = root.getConstructor(new Class<?>[0]);
			} catch (Exception e) {
				throw new ActorException("No constructor for class "
						+ root.getCanonicalName()
						+ " with specified arguments.", e);
			}
		}
		L1: for (Constructor<?> cons : declaredConstructors) {
			Class<?>[] parameterTypes = cons.getParameterTypes();
			for (int i = 0; i < parameterTypes.length - 1; i++) {
				if (i >= args.length || !parameterTypes[i].isInstance(args[i])) {
					continue L1;
				}
			}
			constructor = cons;
			break L1;
		}
		Object a;
		try {
			a = constructor.newInstance(args);
		} catch (Exception e) {
			throw new ActorException("Can't instance class "
					+ root.getCanonicalName() + " with specified arguments.", e);
		}
		String id = "LocalActor-" + idgen.incrementAndGet() + "["
				+ root.getCanonicalName() + "]";
		LocalSelfRef af = new LocalSelfRef(a, id, refMap, actorSystem);
		return af;
	}

	public void initActor(LocalSelfRef af) throws Exception {
		Class<?> ac = af.getObj().getClass();
		for (Method method : ac.getDeclaredMethods()) {
			Activate init = method.getAnnotation(Activate.class);
			if (init != null) {
				method.setAccessible(true);
				method.invoke(af.getObj(), actorSystem, af);
				break;
			}
		}
	}

	@Override
	public ActorRef buildActorRefById(String key) {
		return (ActorRef) refMap.get(key);
	}

	@Override
	public ActorRef findActor(Class<?> class1) {
		for (Entry<String, LocalSelfRef> e : refMap.entrySet()) {
			if (class1.isInstance(e.getValue().getObj())) {
				return new LocalActorRef(e.getKey(), refMap, actorSystem);
			}
		}
		return null;
	}

	@Override
	public ActorRef findActorById(String actorId) {
		return new LocalActorRef(actorId, refMap, actorSystem);
	}

	@Override
	public void init(ActorSystem as) {
		this.actorSystem = as;
	}

	@Override
	public void detachActor(ActorRef ref) {
		LocalSelfRef self = this.refMap.remove(ref.toString());
		if (self == null) {
			return;
		}
		self.setActive(false);
		Object obj = self.getObj();
		Method[] declaredMethods = obj.getClass().getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.isAnnotationPresent(Deactivate.class)) {
				method.setAccessible(true);
				try {
					method.invoke(obj);
				} catch (Exception e) {
					self.error(e, "system.detach", null);
				}
			}
		}
	}

}
