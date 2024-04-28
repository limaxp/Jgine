package org.jgine.utils;

import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.input.InputHandlerType;
import org.jgine.system.systems.light.LightType;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.utils.math.spacePartitioning.SpacePartitioningType;

import maxLibs.utils.registry.GenIdRegistry;
import maxLibs.utils.registry.IRegistry;

/**
 * Base class of a registry. A registry is a collection of types identified by
 * both an <code>int</code> id and a {@link String} name. They implement
 * {@link Iterable} and can be accessed by index of addition.
 * 
 * @param <T> the type of elements in this registry
 */
public abstract class Registry {

	public static final IRegistry<SpacePartitioningType<?>> SPACE_PARTITIONING_TYPES = new GenIdRegistry<SpacePartitioningType<?>>(
			"space_partitioning", 1000);

	public static final IRegistry<ColliderType<?>> COLLIDER_TYPES = new GenIdRegistry<ColliderType<?>>("collider",
			1000);

	public static final IRegistry<InputHandlerType<?>> INPUT_HANDLER_TYPES = new GenIdRegistry<InputHandlerType<?>>(
			"input_handler", 1000);

	public static final IRegistry<UIObjectType<?>> UI_OBJECTS_TYPES = new GenIdRegistry<UIObjectType<?>>("ui_objects",
			1000);

	public static final IRegistry<LightType<?>> LIGHT_TYPES = new GenIdRegistry<LightType<?>>("light", 1000);

	public static final IRegistry<AiGoalType<?>> AI_GOAL_TYPES = new GenIdRegistry<AiGoalType<?>>("ai_goals", 1000);

}
