package org.jgine.system.data;

import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.memory.NativeResource;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public abstract class StructSystemScene<T1 extends EngineSystem, T2 extends Struct & SystemObject>
		extends SystemScene<T1, T2> implements NativeResource {

	protected StructBuffer<T2, ?> buffer;
	protected int size;

	public StructSystemScene(T1 system, Scene scene, Class<T2> clazz) {
		super(system, scene);
	}

	@Override
	public final void close() {
		MemoryUtil.memFree(buffer);
	}

	@Override
	public int addObject(Entity entity, T2 object) {
		return -1;
	}

	@Override
	public T2 removeObject(int index) {
		return null;
	}

	@Override
	public void forEach(Consumer<T2> func) {
	}

	@Override
	public T2 getObject(int index) {
		return null;
	}
}
