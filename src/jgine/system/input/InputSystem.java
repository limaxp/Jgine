package jgine.system.input;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;

public class InputSystem extends EngineSystem<InputSystem, InputHandler> {

	public InputSystem() {
		super("input");
	}

	@Override
	public InputScene createScene(Scene scene) {
		return new InputScene(this, scene);
	}

	@Override
	public InputHandler load(Map<String, Object> data) {
		Object type = data.get("type");
		if (!(type instanceof String))
			return null;
		InputHandler inputHandler = InputHandler.get((String) type);
		inputHandler.load(data);
		return inputHandler;
	}
}
