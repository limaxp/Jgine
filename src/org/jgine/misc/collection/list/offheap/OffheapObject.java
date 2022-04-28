package org.jgine.misc.collection.list.offheap;

import java.nio.ByteBuffer;

public abstract class OffheapObject {

	long address;

	public long getAddress() {
		return address;
	}

	public abstract void save(ByteBuffer buffer);
}
