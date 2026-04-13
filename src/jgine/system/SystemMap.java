package jgine.system;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import jgine.core.Entity;
import jgine.core.Scene;

/**
 * Data structure used internally by {@link Entity} class to store their
 * {@link EngineSystem}<code>s</code>. map() and get() are thread save and lock
 * free! unmap() is not thread save!
 */
public class SystemMap {

	public static final int INITAL_SIZE = 4;
	public static final int MAX_TOMBSTONES = 8;
	private static final SystemObject[] NULL_OBJECTS = new SystemObject[0];
	private static final int[] NULL_DATA = new int[0];
	private static final VarHandle SYSTEMS_HANDLE;
	private static final VarHandle SIZE_HANDLE;

	static {
		try {
			SYSTEMS_HANDLE = MethodHandles.lookup().findVarHandle(SystemMap.class, "systems", SystemObject[].class);
			SIZE_HANDLE = MethodHandles.lookup().findVarHandle(SystemMap.class, "size", int.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public final Scene scene;
	private volatile SystemObject[] systems;
	private volatile int[] types;
	private volatile int[] ids;
	private volatile long typeMask;
	private volatile int size;
	private int tombstones;

	public SystemMap(Scene scene) {
		this.scene = scene;
		clearMap();
	}

	/**
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public void clearMap() {
		this.systems = NULL_OBJECTS;
		this.types = NULL_DATA;
		this.ids = NULL_DATA;
	}

	public int map(int system, SystemObject value) {
		for (;;) {
			int n = size;
			SystemObject[] arr = systems;
			if (n < arr.length) {
				if (SIZE_HANDLE.compareAndSet(this, n, n + 1)) {
					SystemObject[] cur = systems; // re-read
					cur[n] = value;
					types[n] = system;
					typeMask |= (1L << system);
					return n;
				}

			} else {
				int newSize = Math.max(INITAL_SIZE, arr.length * 2);
				Object[] newSystems = Arrays.copyOf(arr, newSize);
				int[] newTypes = Arrays.copyOf(types, newSize);
				int[] newIds = Arrays.copyOf(ids, newSize);

				types = newTypes;
				ids = newIds;
				SYSTEMS_HANDLE.compareAndSet(this, arr, newSystems);
			}
		}
	}

	/**
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public int unmap(SystemObject value) {
		SystemObject[] arr = systems;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (arr[i] == value) {
				arr[i] = null;
				tombstones++;
				if (tombstones >= MAX_TOMBSTONES)
					compact();
				return ids[i];
			}
		}
		return -1;
	}

	/**
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public boolean unmap(int id) {
		SystemObject[] arr = systems;
		int[] idsArr = ids;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (idsArr[i] == id && arr[i] != null) {
				arr[i] = null;
				tombstones++;
				if (tombstones >= MAX_TOMBSTONES)
					compact();
				return true;
			}
		}
		return false;
	}

	/**
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public boolean unmap(int system, IntConsumer consumer) {
		boolean changed = false;
		if ((typeMask & (1L << system)) == 0)
			return changed;

		SystemObject[] arr = systems;
		int[] typesArr = types;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system && arr[i] != null) {
				arr[i] = null;
				tombstones++;
				changed = true;
				consumer.accept(ids[i]);
			}
		}
		if (tombstones >= MAX_TOMBSTONES)
			compact();
		return changed;
	}

	/**
	 * Updates the internal id of a system.
	 * 
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public void intId(int index, int newId) {
		this.ids[index] = newId;
	}

	/**
	 * Updates the internal id of a system.
	 * 
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public boolean setId(int system, int oldValue, int newValue) {
		int[] idsArr = ids;
		int[] typesArr = types;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (idsArr[i] == oldValue && typesArr[i] == system) {
				idsArr[i] = newValue;
				return true;
			}
		}
		return false;
	}

	/**
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	void compact() {
		SystemObject[] oldSystems = systems;
		int[] oldTypes = types;
		int[] oldIds = ids;
		int oldSize = size;

		SystemObject[] newSystems = new SystemObject[oldSize];
		int[] newTypes = new int[oldSize];
		int[] newIds = new int[oldSize];
		int write = 0;
		long newMask = 0L;
		for (int read = 0; read < oldSize; read++) {
			SystemObject s = oldSystems[read];
			if (s != null) {
				newSystems[write] = s;
				int type = oldTypes[read];
				newTypes[write] = type;
				newIds[write] = oldIds[read];
				newMask |= (1L << type);
				write++;
			}
		}

		systems = newSystems;
		types = newTypes;
		ids = newIds;
		typeMask = newMask;
		size = write;
		tombstones = 0;
	}

	public <T extends SystemObject> T get(SystemScene<?, T> system) {
		return get(system.id);
	}

	@SuppressWarnings("unchecked")
	public <T extends SystemObject> T get(int system) {
		if ((typeMask & (1L << system)) == 0)
			return null;

		SystemObject[] arr = systems;
		int[] typesArr = types;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system) {
				SystemObject s = arr[i];
				if (s != null)
					return (T) s;
			}
		}
		return null;
	}

	public <T extends SystemObject> T get(SystemScene<?, T> system, int index) {
		return get(system.id, index);
	}

	@SuppressWarnings("unchecked")
	public <T extends SystemObject> T get(int system, int index) {
		if ((typeMask & (1L << system)) == 0)
			return null;

		SystemObject[] arr = systems;
		int[] typesArr = types;
		int n = size;
		int found = 0;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system) {
				SystemObject s = arr[i];
				if (s != null && found++ == index)
					return (T) s;
			}
		}
		return null;
	}

	public <T extends SystemObject> List<T> getAll(SystemScene<?, T> system) {
		return getAll(system.id);
	}

	public <T extends SystemObject> List<T> getAll(int system) {
		List<T> result = new ArrayList<T>();
		this.<T>forEach(system, result::add);
		return result;
	}

	public void forEach(Consumer<SystemObject> consumer) {
		SystemObject[] arr = systems;
		int n = size;
		for (int i = 0; i < n; i++) {
			SystemObject s = arr[i];
			if (s != null)
				consumer.accept(s);
		}
	}

	public void forEach(SystemMapConsumer consumer) {
		SystemObject[] arr = systems;
		int[] typesArr = types;
		int[] idsArr = ids;
		int n = size;
		for (int i = 0; i < n; i++) {
			SystemObject s = arr[i];
			if (s != null)
				consumer.accept(typesArr[i], idsArr[i], s);
		}
	}

	public <T extends SystemObject> void forEach(SystemScene<?, T> system, Consumer<T> func) {
		forEach(system.id, func);
	}

	@SuppressWarnings("unchecked")
	public <T extends SystemObject> void forEach(int system, Consumer<T> consumer) {
		if ((typeMask & (1L << system)) == 0)
			return;

		SystemObject[] arr = systems;
		int[] typesArr = types;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system) {
				SystemObject s = arr[i];
				if (s != null)
					consumer.accept((T) s);
			}
		}
	}

	public void forEach(SystemScene<?, ?> system, SystemMapConsumer func) {
		forEach(system.id, func);
	}

	public void forEach(int system, SystemMapConsumer consumer) {
		if ((typeMask & (1L << system)) == 0)
			return;

		SystemObject[] arr = systems;
		int[] typesArr = types;
		int[] idsArr = ids;
		int n = size;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system) {
				SystemObject s = arr[i];
				if (s != null)
					consumer.accept(system, idsArr[i], s);
			}
		}
	}

	public void save(DataOutput out) throws IOException {
		SystemObject[] arr = systems;
		int[] typesArr = types;
		int[] idsArr = ids;
		int n = size;

		out.writeLong(typeMask);
		out.writeInt(n - tombstones);
		for (int i = 0; i < n; i++) {
			SystemObject s = arr[i];
			if (s != null) {
				out.writeInt(typesArr[i]);
				out.writeInt(idsArr[i]);
			}
		}
	}

	/**
	 * MUST be called only when there are no concurrent readers or writers.
	 * Violating this invariant breaks snapshot consistency.
	 */
	public void load(DataInput in) throws IOException {
		long newMask = in.readLong();
		int n = in.readInt();
		SystemObject[] newSystems = new SystemObject[n];
		int[] newTypes = new int[n];
		int[] newIds = new int[n];

		for (int i = 0; i < n; i++) {
			int system = in.readInt();
			int id = in.readInt();
			newSystems[i] = scene.getSystem(system).get(id);
			newTypes[i] = system;
			newIds[i] = id;
		}

		systems = newSystems;
		types = newTypes;
		ids = newIds;
		typeMask = newMask;
		size = n;
		tombstones = 0;
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (size != 0) {
			forEach((system, id, _) -> {
				sb.append(scene.getSystem(system).name);
				sb.append(':');
				sb.append(id);
				sb.append(',');
				sb.append(' ');
			});
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(']');
		return sb.toString();
	}

	@FunctionalInterface
	public static interface SystemMapConsumer {

		public void accept(int system, int id, SystemObject value);
	}
}
