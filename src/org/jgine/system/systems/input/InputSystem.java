package org.jgine.system.systems.input;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;

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
		InputHandler inputHandler;
		InputHandlerType<?> inputHandlerType;
		Object type = data.get("type");
		if (type != null && type instanceof String) {
			inputHandlerType = InputHandlerTypes.get((String) type);
			if (inputHandlerType == null)
				inputHandlerType = InputHandlerTypes.TRANSFORM;
		} else
			inputHandlerType = InputHandlerTypes.TRANSFORM;
		inputHandler = inputHandlerType.get();
		inputHandler.load(data);
		return inputHandler;
	}
}
