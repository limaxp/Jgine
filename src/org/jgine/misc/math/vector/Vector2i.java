package org.jgine.misc.math.vector;

public class Vector2i {

	public static final Vector2i NULL = new Vector2i(0);
	public static final Vector2i FULL = new Vector2i(1);
	public static final Vector2i UP = new Vector2i(0, 1);
	public static final Vector2i DOWN = new Vector2i(0, -1);
	public static final Vector2i LEFT = new Vector2i(-1, 0);
	public static final Vector2i RIGHT = new Vector2i(1, 0);
	public static final Vector2i X_AXIS = new Vector2i(1, 0);
	public static final Vector2i Y_AXIS = new Vector2i(0, 1);

	public final int x, y;

	public Vector2i(int d) {
		this.x = d;
		this.y = d;
	}

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2i(Vector2i vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	public Vector2i(Vector3i vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	public Vector2i(Vector4i vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Vector2i vec = (Vector2i) obj;
		return vec.x == x && vec.y == y;
	}
}
