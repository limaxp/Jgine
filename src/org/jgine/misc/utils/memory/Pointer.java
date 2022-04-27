package org.jgine.misc.utils.memory;

import org.jgine.misc.utils.logger.Logger;

@SuppressWarnings("restriction")
public class Pointer<T> {

	public static final long POINTER_OFFSET;

	static {
		long offset = 0;
		try {
			offset = MemoryHelper.UNSAFE.objectFieldOffset(Pointer.class.getDeclaredField("data"));
		} catch (NoSuchFieldException | SecurityException e) {
			Logger.err("Pointer: Error on pointerOffset reflection!", e);
		}
		POINTER_OFFSET = offset;
	}

	public T data;

	public Pointer(T data) {
		this.data = data;
	}

	public void address(long address) {
		MemoryHelper.UNSAFE.putLong(this, POINTER_OFFSET, address);
	}

	public long address() {
		return MemoryHelper.UNSAFE.getLong(this, POINTER_OFFSET);
	}
}
