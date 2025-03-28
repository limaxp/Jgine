package org.jgine.utils.memory;

import org.lwjgl.system.MemoryUtil;

public abstract class Struct extends Pointer implements NativeResource {

	protected Struct(int size) {
		super(MemoryUtil.nmemAlloc(size));
	}

	protected Struct(long address) {
		super(address);
	}

	@Override
	public void free() {
		MemoryUtil.nmemFree(address);
	}

	public abstract int sizeof();

	public Struct set(Struct src) {
		MemoryUtil.memCopy(src.address, address, sizeof());
		return this;
	}

	public void clear() {
		MemoryUtil.memSet(address, 0, sizeof());
	}

	public boolean isNull(int memberOffset) {
		return MemoryUtil.memGetAddress(address + memberOffset) == 0L;
	}
}
