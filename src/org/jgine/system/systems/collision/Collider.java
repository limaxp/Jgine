package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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
		public void scale(Vector3f scale) {
		}

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
		public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
			return null;
		}

		@Override
		public void load(Map<String, Object> data) {
		}

		@Override
		public void load(DataInput in) {
		}

		@Override
		public void save(DataOutput out) {
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

	public void scale(float scae) {
		scale(new Vector3f(scae));
	}

	public void scale(float x, float y, float z) {
		scale(new Vector3f(x, y, z));
	}

	public abstract void scale(Vector3f scale);

	public abstract boolean containsPoint(Vector3f pos, Vector3f point);

	public abstract boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos);

	@Nullable
	public abstract CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos);

	public abstract void load(Map<String, Object> data);

	public abstract void load(DataInput in) throws IOException;

	public abstract void save(DataOutput out) throws IOException;

	public abstract ColliderType<? extends Collider> getType();

	public abstract void render(Vector3f pos);
}