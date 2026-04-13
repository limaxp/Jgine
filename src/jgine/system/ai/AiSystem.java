package jgine.system.ai;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;

/**
 * 
 */
public class AiSystem extends EngineSystem<AiSystem, AiObject> {

	public AiSystem() {
		super("ai");
	}

	@Override
	public AiScene createScene(Scene scene) {
		return new AiScene(this, scene);
	}

	@Override
	public AiObject load(Map<String, Object> data) {
		AiObject object = new AiObject();
		object.load(data);
		return object;
	}
}
