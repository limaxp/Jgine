package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.system.SystemObject;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

public class PhysicObject implements SystemObject {

	private static final float SLOP_VALUE = 0.05f;

	public boolean hasGravity = true;
	public float stiffness = 0.5f;
	public float x, y, z;
	public float motX, motY, motZ;
	private float oldX, oldY, oldZ;
	private boolean isMoving;

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
			isMoving = true;
			return true;
		}
		isMoving = false;
		return false;
	}

	final void initPosition(float x, float y, float z) {
		this.x = oldX = x;
		this.y = oldY = y;
		this.z = oldZ = z;
	}

	final void movePosition(float dx, float dy, float dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
		this.oldX += dx;
		this.oldY += dy;
		this.oldZ += dz;
	}

	public final Vector3f getPosition() {
		return new Vector3f(x, y, z);
	}

	public final Vector3f getOldPosition() {
		return new Vector3f(oldX, oldY, oldZ);
	}

	public float getOldX() {
		return oldX;
	}

	public float getOldY() {
		return oldY;
	}

	public float getOldZ() {
		return oldZ;
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

	public boolean isMoving() {
		return isMoving;
	}

	public void load(Map<String, Object> data) {
		Object hasGravityData = data.get("hasGravity");
		if (hasGravityData != null)
			hasGravity = YamlHelper.toBoolean(hasGravityData, true);
		Object stiffnessData = data.get("stiffness");
		if (stiffnessData != null)
			stiffness = YamlHelper.toFloat(stiffnessData, 0.5f);
		Object accelerationData = data.get("acceleration");
		if (accelerationData != null)
			accelerate(YamlHelper.toVector3f(accelerationData));
	}

	public void load(DataInput in) throws IOException {
		hasGravity = in.readBoolean();
		stiffness = in.readFloat();
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		oldX = in.readFloat();
		oldY = in.readFloat();
		oldZ = in.readFloat();
	}

	public void save(DataOutput out) throws IOException {
		out.writeBoolean(hasGravity);
		out.writeFloat(stiffness);
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(oldX);
		out.writeFloat(oldY);
		out.writeFloat(oldZ);
	}

	@Override
	public String toString() {
		return super.toString() + " [pos: " + x + "," + y + "," + z + " | oldPos: " + oldX + "," + oldY + "," + oldZ
				+ " | mot: " + motX + "," + motY + "," + motZ + " | hasGravity: " + hasGravity + " | stiffness: "
				+ stiffness + " | isMoving: " + isMoving + "]";
	}

	@Override
	public PhysicObject clone() {
		try {
			return (PhysicObject) super.clone();
		} catch (CloneNotSupportedException e) {
			Logger.err("PhysicObject: Error on clone!", e);
			return null;
		}
	}
}
