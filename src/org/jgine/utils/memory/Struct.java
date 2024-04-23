package org.jgine.utils.memory;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memSet;
import static org.lwjgl.system.MemoryUtil.nmemFree;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

public abstract class Struct extends Pointer {

	@SuppressWarnings({ "unused" })
	private ByteBuffer container; // so Java Object does not get freed!

	protected Struct(long address, @Nullable ByteBuffer container) {
		super(address);
		this.container = container;
	}

	public void free() {
		nmemFree(address);
	}

	public abstract int sizeof();

	public void clear() {
		memSet(address, 0, sizeof());
	}

	public boolean isNull(int memberOffset) {
		return memGetAddress(address + memberOffset) == NULL;
	}
}
