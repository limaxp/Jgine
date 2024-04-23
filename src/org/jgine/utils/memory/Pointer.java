package org.jgine.utils.memory;

import static org.lwjgl.system.MemoryUtil.NULL;

import javax.annotation.Nullable;

public class Pointer {

	protected static final sun.misc.Unsafe UNSAFE;

	static {
		UNSAFE = MemoryHelper.UNSAFE;
	}

	public long address;

	public Pointer(long address) {
		if (address == NULL)
			throw new NullPointerException();
		this.address = address;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Pointer))
			return false;
		return address == ((Pointer) o).address;
	}

	@Override
	public int hashCode() {
		return (int) (address ^ (address >>> 32));
	}

	@Override
	public String toString() {
		return String.format("%s pointer [0x%X]", getClass().getSimpleName(), address);
	}
}
