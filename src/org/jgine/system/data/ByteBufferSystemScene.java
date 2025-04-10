package org.jgine.system.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public abstract class ByteBufferSystemScene<S extends EngineSystem<S, O>, O extends Struct & SystemObject>
		extends SystemScene<S, O> implements NativeResource {

	private int objectSize; // in bytes
	private int maxSize;
	private int size;
	private final ByteBuffer buffer;
	private long bufferAddress;
	private final Entity[] entities;
	private LongFunction<O> factory;
	private final O pointer;

	public ByteBufferSystemScene(S system, Scene scene, LongFunction<O> factory, int bytes, int size) {
		super(system, scene);
		objectSize = bytes;
		maxSize = size;
		buffer = MemoryUtil.memAlloc(bytes * size);
		bufferAddress = MemoryUtil.memAddress0(buffer);
		entities = new Entity[size];
		this.factory = factory;
		pointer = factory.apply(0);
	}

	@Override
	public final void free() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public final int add(Entity entity, O object) {
		int id = add(entity, object.address);
		onAdd(entity, object);
		return id;
	}

	private final int add(Entity entity, long address) {
		if (size == maxSize)
			return -1;
		int index = size++;
		MemoryUtil.memCopy(address, address(index), objectSize);
		relink(index, entity);
		return index;
	}

	public final void remove(O object) {
		remove(object.address);
	}

	private final void remove(long address) {
		remove(index(address));
	}

	@Override
	public final void remove(int index) {
		long address = address(index);
		pointer.address = address;
		onRemove(getEntity(index), pointer);
		if (index != --size) {
			MemoryUtil.memCopy(address(size), address, objectSize);
			Entity lastEntity = getEntity(size);
			relink(index, lastEntity);
			lastEntity.setSystemId(this, size, index);
		}
	}

	@Override
	public final void forEach(Consumer<O> func) {
		O o = factory.apply(0);
		for (int i = 0; i < size; i++) {
			o.address = address(i);
			func.accept(o);
		}
	}

	public final void forEach(LongConsumer func) {
		for (int i = 0; i < size; i++)
			func.accept(address(i));
	}

	@Override
	public final O get(int index) {
		return factory.apply(address(index));
	}

	public final O get(int index, O target) {
		target.address = address(index);
		return target;
	}

	public final long address(int index) {
		return bufferAddress + (index * objectSize);
	}

	public final int index(long address) {
		return (int) ((address - bufferAddress) / objectSize);
	}

	@Override
	public final Entity getEntity(int index) {
		return entities[index];
	}

	@Override
	public final Transform getTransform(int index) {
		return entities[index].transform;
	}

	@Override
	public final void relink(int index, Entity entity) {
		entities[index] = entity;
	}

	@Override
	public final int size() {
		return size;
	}

	protected final void swap(int index1, int index2) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			long tmp = stack.nmalloc(objectSize);
			long address1 = address(index1);
			long address2 = address(index2);
			MemoryUtil.memCopy(address1, tmp, objectSize);
			MemoryUtil.memCopy(address2, address1, objectSize);
			MemoryUtil.memCopy(tmp, address2, objectSize);
		}

		Entity first = getEntity(index1);
		Entity second = getEntity(index2);
		relink(index1, second);
		relink(index2, first);
		first.setSystemId(this, index1, index2);
		second.setSystemId(this, index2, index1);
	}

	@Override
	public final void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			saveData(address(i), out);
	}

	@Override
	public final void load(DataInput in) throws IOException {
		size = in.readInt();
		for (int i = 0; i < size; i++)
			loadData(address(i), in);
	}

	protected abstract void saveData(long address, DataOutput out) throws IOException;

	protected abstract void loadData(long address, DataInput in) throws IOException;
}
