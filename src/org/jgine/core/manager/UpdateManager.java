package org.jgine.core.manager;

import java.util.Objects;
import java.util.function.BiConsumer;

import org.jgine.core.entity.Entity;
import org.jgine.system.EngineSystem;

/**
 * Helper class for updating values between {@link EngineSystem}<code>s</code>.
 */
public class UpdateManager {

	@SuppressWarnings("rawtypes")
	public static final BiConsumer NULL_FUNCTION = new BiConsumer() {

		@Override
		public void accept(Object t, Object u) {
		}
	};

	public static final UpdateFunction NULL_UPDATE_FUNCTION = new UpdateFunction() {

		@Override
		public void accept(Entity entity, float x, float y, float z) {
		}
	};

	private static UpdateFunction transformPosition = NULL_UPDATE_FUNCTION;
	private static UpdateFunction transformScale = NULL_UPDATE_FUNCTION;
	private static UpdateFunction physicPosition = NULL_UPDATE_FUNCTION;

	public static void addTransformPosition(UpdateFunction func) {
		transformPosition = addUpdate(transformPosition, func);
	}

	public static UpdateFunction getTransformPosition() {
		return transformPosition;
	}

	public static void addTransformScale(UpdateFunction func) {
		transformScale = addUpdate(transformScale, func);
	}

	public static UpdateFunction getTransformScale() {
		return transformScale;
	}

	public static void addPhysicPosition(UpdateFunction func) {
		physicPosition = addUpdate(physicPosition, func);
	}

	public static UpdateFunction getPhysicPosition() {
		return physicPosition;
	}

	public static <T> BiConsumer<Entity, T> addUpdate(BiConsumer<Entity, T> old, BiConsumer<Entity, T> func) {
		if (old != NULL_FUNCTION)
			return old.andThen(func);
		else
			return func;
	}

	public static UpdateFunction addUpdate(UpdateFunction old, UpdateFunction func) {
		if (old != NULL_UPDATE_FUNCTION)
			return old.andThen(func);
		else
			return func;
	}

	public static interface UpdateFunction {

		public void accept(Entity entity, float x, float y, float z);

		default UpdateFunction andThen(UpdateFunction after) {
			Objects.requireNonNull(after);

			return (e, x, y, z) -> {
				accept(e, x, y, z);
				after.accept(e, x, y, z);
			};
		}

	}
}
