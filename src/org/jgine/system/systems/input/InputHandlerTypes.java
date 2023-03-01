package org.jgine.system.systems.input;

import java.util.function.Supplier;

import org.jgine.system.systems.input.handler.CameraInputHandler;
import org.jgine.system.systems.input.handler.TransformInputHandler;
import org.jgine.system.systems.input.handler.TransformInputHandler2D;
import org.jgine.utils.registry.Registry;

public class InputHandlerTypes {

	public static final InputHandlerType<InputHandler> UNKNOWN = a("unknown", InputHandler::new);

	public static final InputHandlerType<TransformInputHandler> TRANSFORM = a("transform", TransformInputHandler::new);

	public static final InputHandlerType<CameraInputHandler> CAMERA = a("camera", CameraInputHandler::new);

	public static final InputHandlerType<TransformInputHandler2D> TRANSFORM_2D = a("transform2d",
			TransformInputHandler2D::new);

	public static <T extends InputHandler> InputHandlerType<T> a(String name, Supplier<T> supplier) {
		InputHandlerType<T> type = new InputHandlerType<T>(name, supplier);
		type.setId(Registry.INPUT_HANDLER_TYPES.register(name, type));
		return type;
	}

	public static InputHandlerType<?> get(String name) {
		return Registry.INPUT_HANDLER_TYPES.get(name);
	}

	public static InputHandlerType<?> get(int id) {
		return Registry.INPUT_HANDLER_TYPES.get(id);
	}
}
