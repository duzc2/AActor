package com.ourpalm.hot.aactor.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.ourpalm.hot.aactor.Activate;
import com.ourpalm.hot.aactor.ActorException;
import com.ourpalm.hot.aactor.ActorRef;
import com.ourpalm.hot.aactor.ActorSystem;
import com.ourpalm.hot.aactor.config.ActorBuilder;

/**
 * MessageContext ÎÞÐ§
 * 
 * @author g
 *
 */
public class DefaultActorBuilder implements ActorBuilder {

	private AtomicLong idgen = new AtomicLong();
	private ConcurrentHashMap<String, Object> refMap = new ConcurrentHashMap<>();
	private ActorSystem actorSystem;

	@Override
	public ActorRef buildActorRef(Class<?> root, Object[] args) {
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
		String id = "LocalActor-" + idgen.incrementAndGet();
		ActorRef af = new LocalActorRef(id, refMap, actorSystem);
		try {
			initActor(a, af);
		} catch (Throwable t) {
			throw new ActorException("Can't initialize actor:", t);
		}
		refMap.put(af.toString(), a);
		return af;
	}

	private void initActor(Object a, ActorRef af)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Class<?> ac = a.getClass();
		for (Method method : ac.getDeclaredMethods()) {
			Activate init = method.getAnnotation(Activate.class);
			if (init != null) {
				method.setAccessible(true);
				method.invoke(a, actorSystem, af);
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
		for (Entry<String, Object> e : refMap.entrySet()) {
			if (class1.isInstance(e.getValue())) {
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
		this.refMap.remove(ref.toString());
	}

}
