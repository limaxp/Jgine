package jgine.utils.concurrent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles.Lookup;

/**
 * Lock-free bounded MPMC ring buffer
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
public final class ConcurrentRingBuffer<E> {

	private static final VarHandle SEQUENCE_HANDLE = MethodHandles.arrayElementVarHandle(long[].class);
	private static final VarHandle WRITE_HANDLE;
	private static final VarHandle READ_HANDLE;

	static {
		try {
			Lookup lookUp = MethodHandles.privateLookupIn(ConcurrentRingBuffer.class, MethodHandles.lookup());
			WRITE_HANDLE = lookUp.findVarHandle(ConcurrentRingBuffer.class, "write", long.class);
			READ_HANDLE = lookUp.findVarHandle(ConcurrentRingBuffer.class, "read", long.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final int mask;
	private final Object[] value;
	private final long[] sequence;
	@SuppressWarnings("unused")
	private long write;
	@SuppressWarnings("unused")
	private long read;

	public ConcurrentRingBuffer(int capacity) {
		if ((capacity & (capacity - 1)) != 0)
			throw new IllegalArgumentException("Capacity must be a power of 2!");
		this.mask = capacity - 1;
		this.value = new Object[capacity];
		this.sequence = new long[capacity];
		for (int i = 0; i < capacity; i++)
			sequence[i] = i;
	}

	public boolean add(E e) {
		return offer(e);
	}

	public boolean offer(E e) {
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

	public E poll() {
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
				return null; // empty
			else
				r = (long) READ_HANDLE.getOpaque(this);
		}
		@SuppressWarnings("unchecked")
		E result = (E) value[index];
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
