package org.jgine.system.systems.collision;

public class Collision {

	private float axisX;
	private float axisY;
	private float axisZ;
	public final float positionX;
	public final float positionY;
	public final float positionZ;
	public final float deltaX;
	public final float deltaY;
	public final float deltaZ;

	public Collision(float axisX, float axisY, float positionX, float positionY, float deltaX, float deltaY) {
		this(axisX, axisY, 0, positionX, positionY, 0, deltaX, deltaY, 0);
	}

	public Collision(float axisX, float axisY, float axisZ, float positionX, float positionY, float positionZ,
			float deltaX, float deltaY, float deltaZ) {
		this.axisX = axisX;
		this.axisY = axisY;
		this.axisZ = axisZ;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.deltaZ = deltaZ;
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
