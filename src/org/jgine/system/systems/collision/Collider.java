package org.jgine.system.systems.collision;

import java.util.Map;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.transform.Transform;

/**
 * Represents the default Collider
 * 
 * @author Maximilian Paar
 */
public abstract class Collider implements SystemObject, Cloneable {

	public static final Collider NULL = new Collider() {

		@Override
		public boolean containsPoint(Vector3f point) {
			return false;
		}

		@Override
		public boolean checkCollision(Collider collider) {
			return false;
		}

		@Override
		public void render() {}

		@Override
		public ColliderType<?> getType() {
			return ColliderTypes.NONE;
		}
	};

	protected Transform transform;

	protected Collider() {}

	protected Collider(Transform transform) {
		this.transform = transform;
	}

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

	public abstract boolean containsPoint(Vector3f point);

	public abstract boolean checkCollision(Collider collider);

	public void load(Map<String, Object> data) {}

	public abstract ColliderType<?> getType();

	public abstract void render();

	public Transform getTransform() {
		return transform;
	}
}