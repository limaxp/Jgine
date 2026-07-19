package jgine.utils.concurrent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

/**
 * Lock-free bounded MPMC ring buffer
 * <p>
 * <strong>EMPTY = Integer.MIN_VALUE so Integer.MIN_VALUE values
 * prohibited!</strong>
 *
 * <pre>
 *Producer:
 *   - reserves a ticket by CAS'ing write
 *   - writes the value
 *   - publishes it by sequence = ticket + 1 (release)
 *   
 *Consumer:
 *   - reserves a ticket by CAS'ing read
 *   - waits until sequence == ticket + 1 (acquire)
 *   - reads the value
 *   - frees the cell by sequence = ticket + capacity (release)
 *   
 *Sequence difference:
 *  diff == 0: cell belongs to this operation
 *  diff < 0: queue full (producer) / empty (consumer)
 *  diff > 0: stale ticket, retry
 * </pre>
 **/
public final class ConcurrentIntRingBuffer {

	public static final int EMPTY = Integer.MIN_VALUE;

	private static final VarHandle SEQUENCE_HANDLE = MethodHandles.arrayElementVarHandle(long[].class);
	private static final VarHandle WRITE_HANDLE;
	private static final VarHandle READ_HANDLE;

	static {
		try {
			Lookup lookUp = MethodHandles.privateLookupIn(ConcurrentIntRingBuffer.class, MethodHandles.lookup());
			WRITE_HANDLE = lookUp.findVarHandle(ConcurrentIntRingBuffer.class, "write", long.class);
			READ_HANDLE = lookUp.findVarHandle(ConcurrentIntRingBuffer.class, "read", long.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final int mask;
	private final int[] value;
	private final long[] sequence;
	@SuppressWarnings("unused")
	private long write;
	@SuppressWarnings("unused")
	private long read;

	public ConcurrentIntRingBuffer(int capacity) {
		if ((capacity & (capacity - 1)) != 0)
			throw new IllegalArgumentException("Capacity must be a power of 2!");
		this.mask = capacity - 1;
		this.value = new int[capacity];
		this.sequence = new long[capacity];
		for (int i = 0; i < capacity; i++)
			sequence[i] = i;
	}

	public boolean add(int e) {
		return offer(e);
	}

	public boolean offer(int e) {
		long w = (long) WRITE_HANDLE.getOpaque(this);
		int index;
		for (;;) {
			index = getIndex(w);
			long seq = ((long) SEQUENCE_HANDLE.getAcquire(sequence, index));
			long diff = seq - w;
			if (diff == 0) {
				if (WRITE_HANDLE.weakCompareAndSetPlain(this, w, w + 1))
					break;
			} else if (diff < 0)
				return false; // full
			else
				w = (long) WRITE_HANDLE.getOpaque(this);
		}
		value[index] = e;
		SEQUENCE_HANDLE.setRelease(sequence, index, w + 1);
		return true;
	}

	public int poll() {
		long r = (long) READ_HANDLE.getOpaque(this);
		int index;
		for (;;) {
			index = getIndex(r);
			long seq = ((long) SEQUENCE_HANDLE.getAcquire(sequence, index));
			long diff = seq - (r + 1);
			if (diff == 0) {
				if (READ_HANDLE.weakCompareAndSetPlain(this, r, r + 1))
					break;
			} else if (diff < 0)
				return EMPTY; // empty
			else
				r = (long) READ_HANDLE.getOpaque(this);
		}
		int result = value[index];
		SEQUENCE_HANDLE.setRelease(sequence, index, r + mask + 1);
		return result;
	}

	private int getIndex(long ticket) {
		return (int) (ticket & mask);
	}

	public int getCapacity() {
		return mask + 1;
	}
}