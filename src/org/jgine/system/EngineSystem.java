package org.jgine.system;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.SystemManager;

/**
 * The base engine system class. All systems must override this class and will
 * get registered in the {@link SystemManager} automatically.
 * <p>
 * A system consists of an {@link EngineSystem} and a {@link SystemScene}
 * implementation.
 */
public abstract class EngineSystem<S extends EngineSystem<S, O>, O extends SystemObject> {

	public final String name;
	public final int id;

	public EngineSystem(String name) {
		this.name = name;
		this.id = SystemManager.register(this);
	}

	public abstract SystemScene<S, O> createScene(Scene scene);

	public abstract O load(Map<String, Object> data);

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
