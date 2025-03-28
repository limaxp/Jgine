package org.jgine.utils.memory;

import javax.annotation.Nullable;

public class Pointer {

	protected static final sun.misc.Unsafe UNSAFE = MemoryHelper.UNSAFE;

	public long address;

	public Pointer(long address) {
		this.address = address;
	}

	@Override
	public boolean equals(@Nullable Object o) {
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

	@Override
	public Pointer clone() {
		return new Pointer(address);
	}
}
