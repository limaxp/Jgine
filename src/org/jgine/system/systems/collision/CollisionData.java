package org.jgine.system.systems.collision;

import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

public class CollisionData {

	private float axisX;
	private float axisY;
	private float axisZ;
	public final float positionX;
	public final float positionY;
	public final float positionZ;
	public final float overlapX;
	public final float overlapY;
	public final float overlapZ;
	public final boolean strictResolve;

	public CollisionData(Vector2f collisionAxis, Vector2f collisionPosition, Vector2f overlap, boolean strictResolve) {
		this(collisionAxis.x, collisionAxis.y, 0, collisionPosition.x, collisionPosition.y, 0, overlap.x, overlap.y, 0,
				strictResolve);
	}

	public CollisionData(Vector3f collisionAxis, Vector3f collisionPosition, Vector3f overlap, boolean strictResolve) {
		this(collisionAxis.x, collisionAxis.y, collisionAxis.z, collisionPosition.x, collisionPosition.y,
				collisionPosition.z, overlap.x, overlap.y, overlap.z, strictResolve);
	}

	public CollisionData(float axisX, float axisY, float positionX, float positionY, float overlapX, float overlapY,
			boolean strictResolve) {
		this(axisX, axisY, 0, positionX, positionY, 0, overlapX, overlapY, 0, strictResolve);
	}

	public CollisionData(float axisX, float axisY, float axisZ, float positionX, float positionY, float positionZ,
			float overlapX, float overlapY, float overlapZ, boolean strictResolve) {
		this.axisX = axisX;
		this.axisY = axisY;
		this.axisZ = axisZ;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.overlapX = overlapX;
		this.overlapY = overlapY;
		this.overlapZ = overlapZ;
		this.strictResolve = strictResolve;
	}

	CollisionData reverse() {
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

	public final Vector3f getCollisionAxis3d() {
		return new Vector3f(axisX, axisY, axisZ);
	}

	public final Vector3f getCollisionPosition3d() {
		return new Vector3f(positionX, positionY, positionZ);
	}

	public final Vector3f getOverlap3d() {
		return new Vector3f(overlapX, overlapY, overlapZ);
	}

	public final Vector2f getCollisionAxis2d() {
		return new Vector2f(axisX, axisY);
	}

	public final Vector2f getCollisionPosition2d() {
		return new Vector2f(positionX, positionY);
	}

	public final Vector2f getOverlap2d() {
		return new Vector2f(overlapX, overlapY);
	}
}
