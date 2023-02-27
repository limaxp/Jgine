package org.jgine.core.manager;

import java.util.function.BiConsumer;

import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector3f;

/**
 * Helper class for updating values between system.
 */
public class UpdateManager {

	@SuppressWarnings("rawtypes")
	public static final BiConsumer NULL_FUNCTION = new BiConsumer() {

		@Override
		public void accept(Object t, Object u) {
		}
	};

	@SuppressWarnings("unchecked")
	private static BiConsumer<Entity, Vector3f> transformPosition = NULL_FUNCTION;
	@SuppressWarnings("unchecked")
	private static BiConsumer<Entity, Vector3f> transformScale = NULL_FUNCTION;
	@SuppressWarnings("unchecked")
	private static BiConsumer<Entity, Vector3f> physicPosition = NULL_FUNCTION;

	public static void addTransformPosition(BiConsumer<Entity, Vector3f> func) {
		transformPosition = addUpdate(transformPosition, func);
	}

	public static BiConsumer<Entity, Vector3f> getTransformPosition() {
		return transformPosition;
	}

	public static void addTransformScale(BiConsumer<Entity, Vector3f> func) {
		transformScale = addUpdate(transformScale, func);
	}

	public static BiConsumer<Entity, Vector3f> getTransformScale() {
		return transformScale;
	}

	public static void addPhysicPosition(BiConsumer<Entity, Vector3f> func) {
		physicPosition = addUpdate(physicPosition, func);
	}

	public static BiConsumer<Entity, Vector3f> getPhysicPosition() {
		return physicPosition;
	}

	public static <T> BiConsumer<Entity, T> addUpdate(BiConsumer<Entity, T> old, BiConsumer<Entity, T> func) {
		if (old != NULL_FUNCTION)
			return old.andThen(func);
		else
			return func;
	}
}
