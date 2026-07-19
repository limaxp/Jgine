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
import jgine.utils.scheduler.Scheduler;

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
	private static final VarHandle SIZE_HANDLE;
//	public final static byte ID_BITS = 24;
//	public final static int ID_MASK = (1 << ID_BITS) - 1;
//	public final static byte SYSTEM_BITS = 8;
//	public final static int SYSTEM_MASK = (1 << SYSTEM_BITS) - 1;

	static {
		try {
			SIZE_HANDLE = MethodHandles.lookup().findVarHandle(SystemMap.class, "size", int.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

//	public static int toData(int system, int id) {
//		return 0x00000000 | system << ID_BITS | id;
//	}
//
//	public static int toSystem(int data) {
//		return data & ID_MASK;
//	}
//
//	public static int toId(int data) {
//		return (data >> ID_BITS) & SYSTEM_MASK;
//	}

	public final Scene scene;
	private volatile SystemObject[] systems;
	private volatile int[] types;
	private volatile int[] ids;
	private volatile long typeMask;
	private volatile int size;
	private int tombstones;
	private final Object growLock = new Object();

	public SystemMap(Scene scene) {
		this.scene = scene;
		this.systems = NULL_OBJECTS;
		this.types = NULL_DATA;
		this.ids = NULL_DATA;
	}

	private void grow() {
		synchronized (growLock) {
			if (size < systems.length)
				return;

			int newSize = Math.max(INITAL_SIZE, systems.length * 2);
			types = Arrays.copyOf(types, newSize);
			ids = Arrays.copyOf(ids, newSize);
			systems = Arrays.copyOf(systems, newSize);
		}
	}

	private void tombstone(int index) {
		synchronized (growLock) {
			if (systems[index] == null)
				return;

			types[index] = -1;
			ids[index] = -1;
			systems[index] = null;
			tombstones++;
			if (tombstones == MAX_TOMBSTONES)
				Scheduler.runTask(this::compact); // TODO don't use Scheduler!
		}
	}

	public int map(int system, SystemObject value) {
		for (;;) {
			int n = size;
			if (n >= systems.length) {
				grow();
				continue;
			}

			if (SIZE_HANDLE.compareAndSet(this, n, n + 1)) {
				types[n] = system;
				typeMask |= (1L << system);
				systems[n] = value;
				return n;
			}
		}
	}

	public int unmap(SystemObject value) {
		int n = size;
		SystemObject[] arr = systems;
		for (int i = 0; i < n; i++) {
			if (arr[i] == value) {
				tombstone(i);
				return ids[i];
			}
		}
		return -1;
	}

	public boolean unmap(int id) {
		int n = size;
		int[] idsArr = ids;
		for (int i = 0; i < n; i++) {
			if (idsArr[i] == id) {
				tombstone(i);
				return true;
			}
		}
		return false;
	}

	public boolean unmap(int system, IntConsumer consumer) {
		boolean changed = false;
		if ((typeMask & (1L << system)) == 0)
			return changed;

		int n = size;
		int[] typesArr = types;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system) {
				tombstone(i);
				changed = true;
				consumer.accept(ids[i]);
			}
		}
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
		int n = size;
		int[] idsArr = ids;
		int[] typesArr = types;
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

		int n = size;
		SystemObject[] arr = systems;
		int[] typesArr = types;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system)
				return (T) arr[i];
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

		int n = size;
		SystemObject[] arr = systems;
		int[] typesArr = types;
		int found = 0;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system) {
				if (found++ == index)
					return (T) arr[i];
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
		int n = size;
		SystemObject[] arr = systems;
		for (int i = 0; i < n; i++) {
			SystemObject s = arr[i];
			if (s != null)
				consumer.accept(s);
		}
	}

	public void forEach(SystemMapConsumer consumer) {
		int n = size;
		SystemObject[] arr = systems;
		int[] typesArr = types;
		int[] idsArr = ids;
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

		int n = size;
		SystemObject[] arr = systems;
		int[] typesArr = types;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system)
				consumer.accept((T) arr[i]);
		}
	}

	public void forEach(SystemScene<?, ?> system, SystemMapConsumer func) {
		forEach(system.id, func);
	}

	public void forEach(int system, SystemMapConsumer consumer) {
		if ((typeMask & (1L << system)) == 0)
			return;

		int n = size;
		SystemObject[] arr = systems;
		int[] typesArr = types;
		int[] idsArr = ids;
		for (int i = 0; i < n; i++) {
			if (typesArr[i] == system)
				consumer.accept(system, idsArr[i], arr[i]);
		}
	}

	public void save(DataOutput out) throws IOException {
		int n = size;
		SystemObject[] arr = systems;
		int[] typesArr = types;
		int[] idsArr = ids;

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
