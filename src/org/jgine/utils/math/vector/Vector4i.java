package org.jgine.utils.math.vector;

/**
 * An immutable Vector with 4 int values.
 */
public class Vector4i extends Vector3i {

	public static final Vector4f NULL = new Vector4f(0, 1);
	public static final Vector4f FULL = new Vector4f(1);
	public static final Vector4f UP = new Vector4f(0, 1, 0, 1);
	public static final Vector4f DOWN = new Vector4f(0, -1, 0, 1);
	public static final Vector4f SOUTH = new Vector4f(0, 0, 1, 1);
	public static final Vector4f NORTH = new Vector4f(0, 0, -1, 1);
	public static final Vector4f WEST = new Vector4f(-1, 0, 0, 1);
	public static final Vector4f EAST = new Vector4f(1, 0, 0, 1);
	public static final Vector4f X_AXIS = new Vector4f(1, 0, 0, 1);
	public static final Vector4f Y_AXIS = new Vector4f(0, 1, 0, 1);
	public static final Vector4f Z_AXIS = new Vector4f(0, 0, 1, 1);

	public final int w;

	public Vector4i(int d) {
		super(d);
		this.w = d;
	}

	public Vector4i(int d, int w) {
		super(d);
		this.w = w;
	}

	public Vector4i(int x, int y, int z, int w) {
		super(x, y, z);
		this.w = w;
	}

	public Vector4i(Vector4i vec) {
		super(vec);
		this.w = vec.w;
	}

	public Vector4i(Vector3i vec) {
		super(vec);
		this.w = 1;
	}

	public Vector4i(Vector3i vec, int w) {
		super(vec);
		this.w = w;
	}

	public Vector4i(Vector2i vec) {
		super(vec.x, vec.y, 0);
		this.w = 1;
	}

	public Vector4i(Vector2i vec, int z, int w) {
		super(vec.x, vec.y, z);
		this.w = w;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + ", " + w + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Vector4f vec = (Vector4f) obj;
		return vec.x == x && vec.y == y && vec.z == z && vec.w == w;
	}
}
