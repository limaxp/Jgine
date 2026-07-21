package jgine.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import jgine.core.Registry;
import jgine.core.Scene;

/**
 * The base engine system class. All systems must override this class and will
 * get registered in the {@link Registry} automatically.
 * <p>
 * A system consist of an {@link EngineSystem} and a {@link SystemScene}
 * implementation.
 */
public abstract class EngineSystem<S extends EngineSystem<S, O>, O extends SystemObject> {

	public final String name;
	public final int id;

	public EngineSystem(String name) {
		this.name = name;
		this.id = Registry.SYSTEM.register(name, this);
	}

	public abstract SystemScene<S, O> createScene(Scene scene);

	public abstract O load(Map<String, Object> data);

	public <T extends SystemScene<?, ?>> List<T> getScenes() {
		List<T> result = new ArrayList<>();
		for (Scene scene : Scene.values())
			result.add(scene.getSystem(id));
		return result;
	}

	public <T extends SystemScene<?, ?>> void forScenes(Consumer<T> func) {
		for (Scene scene : Scene.values())
			func.accept(scene.getSystem(id));
	}
}
