package org.jgine.system.systems.collision;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

/**
 * Represents the default Collider
 * 
 * @author Maximilian Paar
 */
public abstract class Collider implements SystemObject, Cloneable {

	public static final Collider NULL = new Collider() {

		@Override
		public boolean containsPoint(Vector3f pos, Vector3f point) {
			return false;
		}

		@Override
		public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
			return false;
		}

		@Nullable
		@Override
		public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
			return null;
		}

		@Override
		public void load(Map<String, Object> data) {
		}

		@Override
		public ColliderType<?> getType() {
			return ColliderTypes.NONE;
		}

		@Override
		public void render(Vector3f pos) {
		}
	};

	public boolean noResolve = false;

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public Collider clone() {
		try {
			return (Collider) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public abstract boolean containsPoint(Vector3f pos, Vector3f point);

	public abstract boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos);

	@Nullable
	public abstract Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos);

	public abstract void load(Map<String, Object> data);

	public abstract ColliderType<?> getType();

	public abstract void render(Vector3f pos);
}