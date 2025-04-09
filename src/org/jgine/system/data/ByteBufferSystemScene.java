package org.jgine.system.data;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.memory.NativeResource;
import org.jgine.utils.memory.Struct;
import org.lwjgl.system.MemoryUtil;

public abstract class ByteBufferSystemScene<S extends EngineSystem<S, O>, O extends Struct & SystemObject>
		extends SystemScene<S, O> implements NativeResource {

	protected int objectSize; // in bytes
	protected int maxSize;
	protected int size;
	protected ByteBuffer buffer;
	protected long bufferAddress;
	protected Entity[] entities;
	protected LongFunction<O> factory;

	public ByteBufferSystemScene(S system, Scene scene, LongFunction<O> factory, int bytes, int size) {
		super(system, scene);
		objectSize = bytes;
		maxSize = size;
		buffer = MemoryUtil.memAlloc(bytes * size);
		bufferAddress = MemoryUtil.memAddress0(buffer);
		entities = new Entity[size];
		this.factory = factory;
	}

	@Override
	public final void free() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public int add(Entity entity, O object) {
		return add(entity, object.address);
	}

	public int add(Entity entity, long address) {
		if (size == maxSize)
			return -1;
		int index = size++;
		MemoryUtil.memCopy(address, address(index), objectSize);
		MemoryUtil.nmemFree(address);
		relink(index, entity);
		return index;
	}

	public void remove(O object) {
		remove(object.address);
	}

	public void remove(long address) {
		remove(index(address));
	}

	@Override
	public void remove(int index) {
		if (index != --size) {
			MemoryUtil.memCopy(address(size), address(index), objectSize);
			Entity lastEntity = getEntity(size);
			relink(index, lastEntity);
			lastEntity.setSystemId(this, size, index);
		}
	}

	@Override
	public void forEach(Consumer<O> func) {
		O o = factory.apply(0);
		for (int i = 0; i < size; i++) {
			o.address = address(i);
			func.accept(o);
		}
	}

	public void forEach(LongConsumer func) {
		for (int i = 0; i < size; i++)
			func.accept(address(i));
	}

	@Override
	public O get(int index) {
		return factory.apply(address(index));
	}

	public O get(int index, O target) {
		target.address = address(index);
		return target;
	}

	public long address(int index) {
		return bufferAddress + (index * objectSize);
	}

	public int index(long address) {
		return (int) ((address - bufferAddress) / objectSize);
	}

	@Override
	public Entity getEntity(int index) {
		return entities[index];
	}

	@Override
	public Transform getTransform(int index) {
		return entities[index].transform;
	}

	@Override
	public void relink(int index, Entity entity) {
		entities[index] = entity;
	}

	@Override
	public int getSize() {
		return size;
	}
}
