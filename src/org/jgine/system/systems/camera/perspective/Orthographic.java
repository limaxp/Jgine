package org.jgine.system.systems.camera.perspective;

import java.util.Map;

public class Orthographic {

	public static final float LEFT = -1;
	public static final float RIGHT = 1;
	public static final float BOTTOM = -1;
	public static final float TOP = 1;
	public static final float Z_NEAR = -1;
	public static final float Z_FAR = 1;
	public static final Orthographic DEFAULT = new Orthographic(LEFT, RIGHT, BOTTOM, TOP, Z_NEAR, Z_FAR);

	public final float left;
	public final float right;
	public final float bottom;
	public final float top;
	public final float zNear;
	public final float zFar;

	public Orthographic(float left, float right, float bottom, float top, float zNear, float zFar) {
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	public Orthographic(Map<String, Object> data) {
		Object left = data.get("left");
		if (left != null && left instanceof Number)
			this.left = ((Number) left).floatValue();
		else
			this.left = LEFT;
		Object right = data.get("right");
		if (right != null && right instanceof Number)
			this.right = ((Number) right).floatValue();
		else
			this.right = RIGHT;
		Object bottom = data.get("bottom");
		if (bottom != null && bottom instanceof Number)
			this.bottom = ((Number) bottom).floatValue();
		else
			this.bottom = BOTTOM;
		Object top = data.get("top");
		if (top != null && top instanceof Number)
			this.top = ((Number) top).floatValue();
		else
			this.top = TOP;
		Object zNear = data.get("zNear");
		if (zNear != null && zNear instanceof Number)
			this.zNear = ((Number) zNear).floatValue();
		else
			this.zNear = Z_NEAR;
		Object zFar = data.get("zFar");
		if (zFar != null && zFar instanceof Number)
			this.zFar = ((Number) zFar).floatValue();
		else
			this.zFar = Z_FAR;
	}

	public final float getLeft() {
		return left;
	}

	public final float getRight() {
		return right;
	}

	public final float getBottom() {
		return bottom;
	}

	public final float getTop() {
		return top;
	}

	public final float getZNear() {
		return zNear;
	}

	public final float getZFar() {
		return zFar;
	}
}
