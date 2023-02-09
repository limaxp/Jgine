package org.jgine.misc.utils.registry;

import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.light.LightType;
import org.jgine.system.systems.script.ScriptType;
import org.jgine.system.systems.ui.UIObjectType;

public abstract class Registry<T> implements Iterable<T> {

	public static final Registry<ColliderType<?>> COLLIDER_TYPES = new GenIdRegistry<ColliderType<?>>("collider", 1000);

	public static final Registry<InputHandlerType<?>> INPUT_HANDLER_TYPES = new GenIdRegistry<InputHandlerType<?>>(
			"input_handler", 1000);

	public static final Registry<UIObjectType<?>> UI_OBJECTS_TYPES = new GenIdRegistry<UIObjectType<?>>("ui_objects",
			1000);

	public static final Registry<LightType<?>> LIGHT_TYPES = new GenIdRegistry<LightType<?>>("light", 1000);

	public static final Registry<AiGoalType<?>> AI_GOAL_TYPES = new GenIdRegistry<AiGoalType<?>>("ai_goals", 1000);

	public static final Registry<ScriptType<?>> SCRIPT_TYPES = new GenIdRegistry<ScriptType<?>>("scripts", 1000);

	public final String name;

	public Registry(String name) {
		this.name = name;
	}

	public abstract int register(String key, T value);

	public abstract boolean register(int id, String key, T value);

	public abstract T get(String key);

	public abstract T getOrDefault(String key, T defaultValue);

	public abstract T get(int id);

	public abstract T getOrDefault(int id, T defaultValue);

	public abstract T getByIndex(int index);

	public abstract int size();
}
