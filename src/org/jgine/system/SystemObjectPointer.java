package org.jgine.system;

import org.jgine.misc.utils.memory.Pointer;

public class SystemObjectPointer<T extends SystemObject> extends Pointer<T> implements SystemObject {

	public SystemObjectPointer() {
	}

	public SystemObjectPointer(T data) {
		super(data);
	}
}
