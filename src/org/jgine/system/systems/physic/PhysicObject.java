package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
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

	final void initPosition(Vector3f vector) {
		initPosition(vector.x, vector.y, vector.z);
	}

	final void initPosition(float x, float y, float z) {
		this.x = oldX = x;
		this.y = oldY = y;
		this.z = oldZ = z;
	}

	final void setPosition(Vector3f vector) {
		setPosition(vector.x, vector.y, vector.z);
	}

	final void setPosition(float x, float y, float z) {
		float deltaX = x - this.x;
		float deltaY = y - this.y;
		float deltaZ = z - this.z;
		this.x += deltaX;
		this.y += deltaY;
		this.z += deltaZ;
		this.oldX += deltaX;
		this.oldY += deltaY;
		this.oldZ += deltaZ;

//		alternative
//		float deltaX = this.x - oldX;
//		float deltaY = this.y - oldY;
//		float deltaZ = this.z - oldZ;
//		this.x = oldX = x;
//		this.y = oldY = y;
//		this.z = oldZ = z;
//		this.oldX -= deltaX;
//		this.oldY -= deltaY;
//		this.oldZ -= deltaZ;
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

	public void load(Map<String, Object> data) {
		hasGravity = YamlHelper.toBoolean(data.get("hasGravity"), true);
		stiffness = YamlHelper.toFloat(data.get("stiffness"), 0.5f);
		accelerate(YamlHelper.toVector3f(data.get("acceleration")));
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
}
