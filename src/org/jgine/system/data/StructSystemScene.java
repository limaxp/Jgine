package org.jgine.system.data;

import java.util.function.Consumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.memory.NativeResource;

public abstract class StructSystemScene<S extends EngineSystem<S, O>, O extends /** Struct & **/
SystemObject> extends SystemScene<S, O> implements NativeResource {

//	protected StructBuffer<O, ?> buffer;
	protected int size;

	public StructSystemScene(S system, Scene scene, Class<O> clazz) {
		super(system, scene);
	}

	@Override
	public final void close() {
//		MemoryUtil.memFree(buffer);
	}

	@Override
	public int addObject(Entity entity, O object) {
		return -1;
	}

	@Override
	public O removeObject(int index) {
		return null;
	}

	@Override
	public void forEach(Consumer<O> func) {
	}

	@Override
	public O getObject(int index) {
		return null;
	}
}
