package org.jgine.system.systems.ai;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public class AiSystem extends EngineSystem<AiSystem, AiObject> {

	public AiSystem() {
		super("ai");
	}

	@Override
	public SystemScene<AiSystem, AiObject> createScene(Scene scene) {
		return new AiScene(this, scene);
	}

	@Override
	public AiObject load(Map<String, Object> data) {
		AiObject object = new AiObject();
		object.load(data);
		return object;
	}
}
