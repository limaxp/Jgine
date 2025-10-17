package org.jgine.system.systems.collision;

import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

public class Collision {

	private float axisX;
	private float axisY;
	private float axisZ;
	public final float positionX;
	public final float positionY;
	public final float positionZ;
	public final float overlapX;
	public final float overlapY;
	public final float overlapZ;

	public Collision(Vector2f collisionAxis, Vector2f collisionPosition, Vector2f overlap) {
		this(collisionAxis.x, collisionAxis.y, 0, collisionPosition.x, collisionPosition.y, 0, overlap.x, overlap.y, 0);
	}

	public Collision(Vector3f collisionAxis, Vector3f collisionPosition, Vector3f overlap) {
		this(collisionAxis.x, collisionAxis.y, collisionAxis.z, collisionPosition.x, collisionPosition.y,
				collisionPosition.z, overlap.x, overlap.y, overlap.z);
	}

	public Collision(float axisX, float axisY, float positionX, float positionY, float overlapX, float overlapY) {
		this(axisX, axisY, 0, positionX, positionY, 0, overlapX, overlapY, 0);
	}

	public Collision(float axisX, float axisY, float axisZ, float positionX, float positionY, float positionZ,
			float overlapX, float overlapY, float overlapZ) {
		this.axisX = axisX;
		this.axisY = axisY;
		this.axisZ = axisZ;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.overlapX = overlapX;
		this.overlapY = overlapY;
		this.overlapZ = overlapZ;
	}

	Collision reverse() {
		axisX = -axisX;
		axisY = -axisY;
		axisZ = -axisZ;
		return this;
	}

	public float getAxisX() {
		return axisX;
	}

	public float getAxisY() {
		return axisY;
	}

	public float getAxisZ() {
		return axisZ;
	}
}
