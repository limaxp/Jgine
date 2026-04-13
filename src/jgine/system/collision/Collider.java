package jgine.system.collision;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Engine;
import jgine.system.SystemObject;

/**
 * The default Collider class.
 */
public abstract class Collider implements SystemObject {

	public boolean noResolve = false;

	public abstract void set(float x, float y, float z);

	public abstract void move(float x, float y, float z);

	public abstract void scale(float x, float y, float z);

	public abstract boolean containsPoint(float x, float y, float z);

	public abstract boolean checkCollision(Collider other);

	@Nullable
	public abstract Collision resolveCollision(Collider other);

	public abstract ColliderType<? extends Collider> getType();

	public abstract void render();

	public abstract float getX();

	public abstract float getY();

	public abstract float getZ();

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract float getDepth();

	@Override
	public int system() {
		return Engine.COLLISION;
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