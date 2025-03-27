package org.jgine.system.data;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.memory.NativeResource;
import org.lwjgl.system.MemoryUtil;

public abstract class ByteBufferSystemScene<S extends EngineSystem<S, O>, O extends SystemObject>
		extends SystemScene<S, O> implements NativeResource {

	protected int objectSize;
	protected ByteBuffer buffer;
	protected int bufferSize;
	protected int size;

	public ByteBufferSystemScene(S system, Scene scene, Class<O> clazz) {
		super(system, scene);
//		objectSize = (int) MemoryHelper.sizeOf(Reflection.newInstance(clazz));
//		bufferSize = ListSystemScene.INITAL_SIZE;
		buffer = MemoryUtil.memAlloc(objectSize * bufferSize);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public int addObject(Entity entity, O object) {
		if (size == bufferSize)
			ensureCapacity(size + 1);
		int index = size++;
//		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
//		MemoryHelper.copyArray(object, address, objectSize);
//		entity.system = new SystemObjectPointer<T2>(object);
		return index;
	}

	@Override
	public O removeObject(int index) {
		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
		MemoryUtil.memSet(address, 0, objectSize);
		// TODO rearange list
		return null;
	}

	@Override
	public void forEach(Consumer<O> func) {
		// TODO Auto-generated method stub

	}

	@Override
	public O getObject(int index) {
//		long address = MemoryUtil.memAddress(buffer) + (index * objectSize);
//		return new Pointer<T2>().address(address).data;
		return null;
	}

	protected void ensureCapacity(int minCapacity) {
		if (minCapacity > bufferSize)
			resize(Math.max(bufferSize * 2, minCapacity));
	}

	protected void resize(int size) {
		ByteBuffer newBuffer = MemoryUtil.memAlloc(objectSize * size);
		newBuffer.put(0, buffer, 0, objectSize * bufferSize);
		MemoryUtil.memFree(buffer);
		buffer = newBuffer;
		bufferSize = size;
	}
}
