package org.jgine.system.data;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.memory.Allocator;
import org.jgine.misc.utils.memory.MemoryHelper;
import org.jgine.misc.utils.reflection.Reflection;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.lwjgl.system.MemoryUtil;

public abstract class ByteBufferSystemScene<T1 extends EngineSystem, T2 extends SystemObject>
		extends SystemScene<T1, T2> implements AutoCloseable {

	protected final ByteBuffer buffer;
	protected final int objectSize;

	public ByteBufferSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene);
		objectSize = MemoryHelper.sizeOf(Reflection.newInstance(clazz));
		buffer = MemoryUtil.memAlloc(objectSize * ListSystemScene.GROW_SIZE);
		MemoryUtil.memSet(buffer, 0);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public T2 addObject(Entity entity, T2 object) {
		long address = MemoryUtil.memAddress(buffer);
		MemoryHelper.copyMemory(object, address, objectSize);
		return object;
	}

	@Override
	@Nullable
	public T2 removeObject(T2 object) {
		return object;
	}

	@Override
	public Collection<T2> getObjects() {
		return null;
	}

	public T2 getObject(int index) {
		return MemoryUtil.memGlobalRefToObject(index);
	}
}
