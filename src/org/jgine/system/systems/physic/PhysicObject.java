package org.jgine.system.systems.physic;

import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

public class PhysicObject implements SystemObject {

	private static final float SLOP_VALUE = 0.01f;

	public boolean hasGravity = true;
	public float stiffness = 0.5f;
	float x, y, z;
	private float oldX, oldY, oldZ;
	private float motX, motY, motZ;

	final boolean updatePosition(float dt, float gravity, float airResistanceFactor) {
		float velX = x - oldX + motX * dt * dt;
		float velY = y - oldY + (motY + (hasGravity ? gravity : 0)) * dt * dt;
		float velZ = z - oldZ + motZ * dt * dt;

		oldX = x;
		oldY = y;
		oldZ = z;

		motX = 0;
		motY = 0;
		motZ = 0;

		if (FastMath.abs(velX) > SLOP_VALUE || FastMath.abs(velY) > SLOP_VALUE || FastMath.abs(velZ) > SLOP_VALUE) {
			x += velX * airResistanceFactor;
			y += velY * airResistanceFactor;
			z += velZ * airResistanceFactor;
			return true;
		}
		return false;
	}

	final void setPosition(Vector3f vector) {
		setPosition(vector.x, vector.y, vector.z);
	}

	final void setPosition(float x, float y, float z) {
		this.x = oldX = x;
		this.y = oldY = y;
		this.z = oldZ = z;
	}

	public final Vector3f getPosition() {
		return new Vector3f(x, y, z);
	}

	public final void accelerate(Vector3f vector) {
		accelerate(vector.x, vector.y, vector.z);
	}

	public final void accelerate(float x, float y, float z) {
		motX += x;
		motY += y;
		motZ += z;
	}

	public final void accelerate(Vector2f vector) {
		accelerate(vector.x, vector.y);
	}

	public final void accelerate(float x, float y) {
		motX += x;
		motY += y;
	}

	public final Vector3f getAcceleration() {
		return new Vector3f(motX, motY, motZ);
	}

	public final Vector3f getVelocity() {
		return new Vector3f(x - oldX, y - oldY, z - oldZ);
	}
}
