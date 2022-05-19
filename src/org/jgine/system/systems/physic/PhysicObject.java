package org.jgine.system.systems.physic;

import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

public class PhysicObject implements SystemObject {

	public boolean hasGravity = true;
	protected float x, y, z;
	protected float velX, velY, velZ;
	protected float motX, motY, motZ;

	final void setPosition(Vector3f vector) {
		x = vector.x;
		y = vector.y;
		z = vector.z;
	}

	public final Vector3f getPosition() {
		return new Vector3f(x, y, z);
	}

	public final void setVelocity(Vector3f vector) {
		velX = vector.x;
		velY = vector.y;
		velZ = vector.z;
		motX = 0;
		motY = 0;
		motZ = 0;
	}

	public final void setVelocity(Vector2f vector) {
		velX = vector.x;
		velY = vector.y;
		motX = 0;
		motY = 0;
	}

	public final void addVelocity(Vector3f vector) {
		motX += vector.x;
		motY += vector.y;
		motZ += vector.z;
	}

	public final void addVelocity(Vector2f vector) {
		motX += vector.x;
		motY += vector.y;
	}

	public final Vector3f getVelocity() {
		return new Vector3f(velX, velY, velZ);
	}

	public final Vector3f getAcceleration() {
		return new Vector3f(motX, motY, motZ);
	}
}
