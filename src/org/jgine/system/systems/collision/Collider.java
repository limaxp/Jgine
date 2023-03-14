package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.system.SystemObject;
import org.jgine.utils.math.vector.Vector3f;

/**
 * The default Collider class.
 */
public abstract class Collider implements SystemObject, Cloneable {

	public boolean noResolve = false;

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
}