package jgine.system.graphic;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;
import jgine.utils.loader.ResourceManager;

public class GraphicSystem extends EngineSystem<GraphicSystem, GraphicObject> {

	public GraphicSystem() {
		super("graphic");
	}

	@Override
	public GraphicScene createScene(Scene scene) {
		return new GraphicScene(this, scene);
	}

	@Override
	public GraphicObject load(Map<String, Object> data) {
		GraphicObject object = new GraphicObject();
		Object model = data.get("model");
		if (model instanceof String)
			object.model = ResourceManager.getModel((String) model);
		return object;
	}
}