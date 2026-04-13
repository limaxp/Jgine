package jgine.utils.scheduler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Helper class to register and use {@link MethodHandle} and {@link VarHandle}
 * with a given identifier and a target {@link Object}.
 * <p>
 * Register, unregister functions are not thread save!
 */
public class Service {

	private static final Map<Object, MethodHandle> FUNCTIONS = new HashMap<>();
	private static final Map<Object, MethodHandle> GET_FUNCTIONS = new HashMap<>();
	private static final Map<Object, MethodHandle> SET_FUNCTIONS = new HashMap<>();
	private static final Queue<UpdateValue> UPDATED_PROPERTIES = new ConcurrentLinkedQueue<>();

	private static record UpdateValue(MethodHandle handle, Object value) {
	}

	static void update() {
		try {
			while (!UPDATED_PROPERTIES.isEmpty()) {
				UpdateValue value = UPDATED_PROPERTIES.poll();
				value.handle.invoke(value.value);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void register(Object identifier, Object target, MethodHandle handle) {
		FUNCTIONS.put(identifier, handle.bindTo(target));
	}

	public static <T> void register(Object identifier, Object target, VarHandle handle) {
		register(identifier, target, handle.toMethodHandle(VarHandle.AccessMode.GET),
				handle.toMethodHandle(VarHandle.AccessMode.SET));
	}

	public static <T> void register(Object identifier, Object target, MethodHandle getHandle, MethodHandle setHandle) {
		GET_FUNCTIONS.put(identifier, getHandle.bindTo(target));
		SET_FUNCTIONS.put(identifier, setHandle.bindTo(target));
	}

	public static void unregister(Object identifier) {
		FUNCTIONS.remove(identifier);
		GET_FUNCTIONS.remove(identifier);
		SET_FUNCTIONS.remove(identifier);
	}

	public static <T> T run(Object identifier, Object... args) throws Throwable {
		return (T) FUNCTIONS.get(identifier).invokeExact(args);
	}

	public static <T> T get(Object identifier) throws Throwable {
		return (T) GET_FUNCTIONS.get(identifier).invokeExact();
	}

	public static <T> void set(Object identifier, T value) throws Throwable {
		SET_FUNCTIONS.get(identifier).invokeExact(value);
	}

	public static <T> void setSynchron(Object identifier, T value) {
		UPDATED_PROPERTIES.add(new UpdateValue(SET_FUNCTIONS.get(identifier), value));
	}
}
