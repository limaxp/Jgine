package org.jgine.system.data;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.memory.MemoryHelper;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.lwjgl.system.MemoryUtil;

public abstract class ByteBufferSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends SystemScene<T1, T2> implements AutoCloseable {

	protected int objectSize;
	protected ByteBuffer buffer;
	protected int bufferSize;
	protected int size;

	public ByteBufferSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene);
//		objectSize = (int) MemoryHelper.sizeOf(Reflection.newInstance(clazz));
		bufferSize = ListSystemScene.GROW_SIZE;
		buffer = MemoryUtil.memAlloc(objectSize * bufferSize);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		if (size == bufferSize)
			ensureCapacity(size + 1);
		long address = MemoryUtil.memAddress(buffer) + (size++ * objectSize);
		MemoryHelper.copyArray(object, address, objectSize);
//		entity.system = new SystemObjectPointer<T2>(object);
		return object;
	}

	@Override
	@Nullable
	public T2 removeObject(T2 object) {
		int index = 0;
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		MemoryUtil.memSet(address, 0, objectSize);
		// TODO rearange list
		return object;
	}

	@Override
	public Collection<T2> getObjects() {
		return null;
	}

	public T2 getObject(int index) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
//		return new Pointer<T2>().address(address).data;
		return null;
	}

	protected void ensureCapacity(int minCapacity) {
		if (minCapacity > bufferSize)
			resize(Math.max(bufferSize * 2, minCapacity));
	}

	protected void resize(int size) {
		ByteBuffer newBuffer = MemoryUtil.memAlloc(objectSize * size);
		buffer = newBuffer.put(0, buffer, 0, objectSize * bufferSize);
		bufferSize = size;
	}
}
