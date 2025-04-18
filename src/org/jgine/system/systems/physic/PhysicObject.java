package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.system.SystemObject;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

public class PhysicObject implements SystemObject {

	public float velX, velY, velZ;
	public float motX, motY, motZ;
	private float gravity = 1.0f;
	private float stiffness = 0.5f;
	private boolean isMoving;

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
		return new Vector3f(velX, velY, velZ);
	}

	final void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public final boolean isMoving() {
		return isMoving;
	}

	public final void setGravity(boolean hasGravity) {
		this.gravity = hasGravity ? 1.0f : 0.0f;
	}

	public final void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public final float inverseGravity() {
		return gravity *= -1;
	}

	public final boolean hasGravity() {
		return gravity != 0.0f;
	}

	public final float getGravity() {
		return gravity;
	}

	public final void setMoveable(boolean isMoveable) {
		this.stiffness = isMoveable ? 0.5f : 0.0f;
	}

	public final boolean isMoveable() {
		return stiffness != 0.0f;
	}

	public final void setStiffness(float stiffness) {
		this.stiffness = stiffness;
	}

	public final float getStiffness() {
		return stiffness;
	}

	public final void load(Map<String, Object> data) {
		Object accelerationData = data.get("acceleration");
		if (accelerationData != null)
			accelerate(YamlHelper.toVector3f(accelerationData));
		Object hasGravityData = data.get("hasGravity");
		if (hasGravityData != null)
			setGravity(YamlHelper.toBoolean(hasGravityData, true));
		Object gravityData = data.get("gravity");
		if (gravityData != null)
			gravity = YamlHelper.toFloat(gravityData, gravity);
		Object moveableData = data.get("moveable");
		if (moveableData != null)
			setMoveable(YamlHelper.toBoolean(moveableData, true));
		Object stiffnessData = data.get("stiffness");
		if (stiffnessData != null)
			stiffness = YamlHelper.toFloat(stiffnessData, stiffness);
	}

	public final void load(DataInput in) throws IOException {
		velX = in.readFloat();
		velY = in.readFloat();
		velZ = in.readFloat();
		gravity = in.readFloat();
		stiffness = in.readFloat();
	}

	public final void save(DataOutput out) throws IOException {
		out.writeFloat(velX);
		out.writeFloat(velY);
		out.writeFloat(velZ);
		out.writeFloat(gravity);
		out.writeFloat(stiffness);
	}

	@Override
	public String toString() {
		return super.toString() + " [velocity: " + velX + "," + velY + "," + velZ + " | acceleration: " + motX + ","
				+ motY + "," + motZ + " | gravity: " + gravity + " | stiffness: " + stiffness + " | isMoving: "
				+ isMoving + "]";
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
