package org.jgine.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jgine.utils.collection.function.Property;
import org.jgine.utils.collection.list.UnorderedIdentityArrayList;
import org.jgine.utils.logger.Logger;

/**
 * Manager to register and run services and to register, set and get properties
 * with a given identifier.
 * <p>
 * Services expose a system functionality while properties expose a system value
 * by identifier.
 */
public class Service {

	private static final int MAX_UPDATES = 64;

	private static final Map<Object, ServiceFunction> SERVICES = new ConcurrentHashMap<Object, ServiceFunction>();
	@SuppressWarnings("rawtypes")
	private static final Map<Object, ServiceProperty> PROPERTIES = new ConcurrentHashMap<Object, ServiceProperty>();
	@SuppressWarnings("rawtypes")
	private static final Queue<ServiceProperty> UPDATE_VALUES = new ConcurrentLinkedQueue<>();

	public static void register(Object identifier, ServiceFunction service) {
		SERVICES.put(identifier, service);
	}

	public static void run(Object identifier, Object... args) {
		SERVICES.get(identifier).invoke(args);
	}

	public static <T> void register(Object identifier, Property<T> property) {
		@SuppressWarnings("unchecked")
		ServiceProperty<T> serviceProperty = PROPERTIES.get(identifier);
		if (serviceProperty == null)
			PROPERTIES.put(identifier, serviceProperty = new ServiceProperty<>());
		serviceProperty.addProperty(property);
	}

	@SuppressWarnings("unchecked")
	public static void set(Object identifier, Object value) {
		ServiceProperty<Object> property = PROPERTIES.get(identifier);
		if (!property.hasChanged())
			UPDATE_VALUES.add(property);
		property.setValue(value);
	}

	public static Object get(Object identifier) {
		return PROPERTIES.get(identifier).getValue();
	}

	public static void unregister(Object identifier) {
		SERVICES.remove(identifier);
		PROPERTIES.remove(identifier);
	}

	public static void distributeChanges() {
		int i = 0;
		while (!UPDATE_VALUES.isEmpty()) {
			if (i++ >= MAX_UPDATES)
				break;
			UPDATE_VALUES.poll().update();
		}
	}

	public static class ServiceProperty<T> {

		private T value;
		private final List<Property<T>> properties = new UnorderedIdentityArrayList<Property<T>>();

		public final void setValue(T obj) {
			value = obj;
		}

		public final T getValue() {
			return properties.get(0).getValue();
		}

		public final void addProperty(Property<T> property) {
			properties.add(property);
		}

		public final void update() {
			for (Property<T> property : properties)
				property.setValue(value);
			value = null;
		}

		public final boolean hasChanged() {
			return value != null;
		}
	}

	public static class ServiceFunction {

		private Object provider;
		private Method method;

		public ServiceFunction(Object provider, String methodName, Class<?>... parameterTypes) {
			Method method;
			try {
				method = provider.getClass().getMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException | SecurityException e) {
				Logger.err(
						"Service: Error on reflection of method '" + methodName + "' for provider '" + provider + "'",
						e);
				method = null;
			}
			this.provider = provider;
			this.method = method;
		}

		public ServiceFunction(Object provider, Method method) {
			this.provider = provider;
			this.method = method;
		}

		public final void invoke(Object... args) {
			try {
				method.invoke(provider, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				Logger.err(
						"Service: Error on running method '" + method.getName() + "' for provider '" + provider + "'",
						e);
			}
		}
	}
}
