package jgine.utils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;

import jgine.utils.concurrent.ConcurrentIntRingBuffer;

/**
 * Lock-free Generator for 32-bit identifiers (0 - 16777215)
 * 
 * <pre>
 * index 24-bit
 * generation 8-bit
 * 
 *<strong>Alive until slot reuse!</strong>
 *<strong>Must be freed after use!</strong>
 * </pre>
 * 
 * Uses a {@link ConcurrentIntRingBuffer} for free IDs.
 */
public class IdGenerator {

	public static final int MAX_CAPACITY = 16777216;
	public final static byte INDEX_BITS = 24;
	public final static int INDEX_MASK = (1 << INDEX_BITS) - 1;
	public final static byte GENERATION_BITS = 8;
	public final static int GENERATION_MASK = (1 << GENERATION_BITS) - 1;

	private static final VarHandle SIZE_HANDLE;

	static {
		try {
			Lookup lookUp = MethodHandles.privateLookupIn(IdGenerator.class, MethodHandles.lookup());
			SIZE_HANDLE = lookUp.findVarHandle(IdGenerator.class, "size", int.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@SuppressWarnings("unused")
	private int size;
	private final ConcurrentIntRingBuffer freeIndices;
	private final byte[] generation;

	public IdGenerator(int capacity) {
		this(0, capacity);
	}

	public IdGenerator(int startId, int capacity) {
		this.size = startId;
		this.freeIndices = new ConcurrentIntRingBuffer(capacity);
		this.generation = new byte[capacity];
	}

	public int generate() {
		int index = freeIndices.poll();
		if (index != ConcurrentIntRingBuffer.EMPTY)
			return id(index, ++generation[index]);

		int s = (int) SIZE_HANDLE.getOpaque(this);
		for (;;) {
			if (s >= freeIndices.getCapacity())
				throw new IndexOutOfBoundsException(s);
			if (SIZE_HANDLE.weakCompareAndSetPlain(this, s, s + 1))
				break;
			else
				s = (int) SIZE_HANDLE.getOpaque(this);
		}
		return s; // == id(s, 0);
	}

	public int free(int id) {
		int index = index(id);
		freeIndices.add(index);
		return index;
	}

	public int getCapacity() {
		return freeIndices.getCapacity();
	}

	public boolean isAlive(int id) {
		return (generation[index(id)] & GENERATION_MASK) == generation(id);
	}

	public static int id(int index, int generation) {
		return generation << INDEX_BITS | index;
	}

	public static int index(int id) {
		return id & INDEX_MASK;
	}

	public static int generation(int id) {
		return (id >> INDEX_BITS) & GENERATION_MASK;
	}
}
