package org.jgine.misc.math.vector;

/**
 * An immutable Vector with 3 int values.
 */
public class Vector3i extends Vector2i {

	public static final Vector3f NULL = new Vector3f(0);
	public static final Vector3f FULL = new Vector3f(1);
	public static final Vector3f UP = new Vector3f(0, 1, 0);
	public static final Vector3f DOWN = new Vector3f(0, -1, 0);
	public static final Vector3f SOUTH = new Vector3f(0, 0, 1);
	public static final Vector3f NORTH = new Vector3f(0, 0, -1);
	public static final Vector3f WEST = new Vector3f(-1, 0, 0);
	public static final Vector3f EAST = new Vector3f(1, 0, 0);
	public static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	public static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	public static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	public final int z;

	public Vector3i(int d) {
		super(d);
		this.z = d;
	}

	public Vector3i(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}

	public Vector3i(Vector3i vec) {
		super(vec);
		this.z = vec.z;
	}

	public Vector3i(Vector4i vec) {
		super(vec);
		this.z = vec.z;
	}

	public Vector3i(Vector2i vec) {
		super(vec);
		this.z = 0;
	}

	public Vector3i(Vector2i vec, int z) {
		super(vec);
		this.z = z;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Vector3f vec = (Vector3f) obj;
		return vec.x == x && vec.y == y && vec.z == z;
	}
}
