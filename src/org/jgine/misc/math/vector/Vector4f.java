package org.jgine.misc.math.vector;

import org.jgine.misc.math.FastMath;

public class Vector4f extends Vector3f {

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

	public final float w;

	public Vector4f(float d) {
		super(d);
		this.w = d;
	}

	public Vector4f(float d, float w) {
		super(d);
		this.w = w;
	}

	public Vector4f(float x, float y, float z, float w) {
		super(x, y, z);
		this.w = w;
	}

	public Vector4f(Vector4f vec) {
		super(vec);
		this.w = vec.w;
	}

	public Vector4f(Vector3f vec) {
		super(vec);
		this.w = 1;
	}

	public Vector4f(Vector3f vec, float w) {
		super(vec);
		this.w = w;
	}

	public Vector4f(Vector2f vec) {
		super(vec.x, vec.y, 0);
		this.w = 1;
	}

	public Vector4f(Vector2f vec, float z, float w) {
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

	public static Vector4f add(Vector4f vec1, Vector4f vec2) {
		return new Vector4f(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z, vec1.w + vec2.w);
	}

	public static Vector4f add(Vector4f vec, float d) {
		return new Vector4f(vec.x + d, vec.y + d, vec.z + d, vec.w + d);
	}

	public static Vector4f add(float x, float y, float z, float w, float d) {
		return new Vector4f(x + d, y + d, z + d, w + d);
	}

	public static Vector4f add(Vector4f vec, float x, float y, float z, float w) {
		return new Vector4f(vec.x + x, vec.y + y, vec.z + z, vec.w + w);
	}

	public static Vector4f add(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return new Vector4f(x1 + x2, y1 + y2, z1 + z2, w1 + w2);
	}

	public static Vector4f sub(Vector4f vec1, Vector4f vec2) {
		return new Vector4f(vec1.x - vec2.x, vec1.y - vec2.y, vec1.z - vec2.z, vec1.w - vec2.w);
	}

	public static Vector4f sub(Vector4f vec, float d) {
		return new Vector4f(vec.x - d, vec.y - d, vec.z - d, vec.w - d);
	}

	public static Vector4f sub(float x, float y, float z, float w, float d) {
		return new Vector4f(x - d, y - d, z - d, w - d);
	}

	public static Vector4f sub(Vector4f vec, float x, float y, float z, float w) {
		return new Vector4f(vec.x - x, vec.y - y, vec.z - z, vec.w - w);
	}

	public static Vector4f sub(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return new Vector4f(x1 - x2, y1 - y2, z1 - z2, w1 - w2);
	}

	public static Vector4f mult(Vector4f vec, float d) {
		return new Vector4f(vec.x * d, vec.y * d, vec.z * d, vec.w * d);
	}

	public static Vector4f mult(float x, float y, float z, float w, float d) {
		return new Vector4f(x * d, y * d, z * d, w * d);
	}

	public static Vector4f div(Vector4f vec, float d) {
		return new Vector4f(vec.x / d, vec.y / d, vec.z / d, vec.w / d);
	}

	public static Vector4f div(float x, float y, float z, float w, float d) {
		return new Vector4f(x / d, y / d, z / d, w / d);
	}

	public static float dot(Vector4f vec1, Vector4f vec2) {
		return FastMath.fma(vec1.x, vec2.x, FastMath.fma(vec1.y, vec2.y, FastMath.fma(vec1.z, vec2.z, vec1.w
				* vec2.w)));
	}

	public static float dot(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return FastMath.fma(x1, x2, FastMath.fma(y1, y2, FastMath.fma(z1, z2, w1 * w2)));
	}

	public static Vector4f cross(Vector4f vec1, Vector4f vec2) {
		float rx = FastMath.fma(vec1.y, vec2.z, -vec1.z * vec2.y);
		float ry = FastMath.fma(vec1.z, vec2.x, -vec1.x * vec2.z);
		float rz = FastMath.fma(vec1.x, vec2.y, -vec1.y * vec2.x);
		return new Vector4f(rx, ry, rz, Math.max(vec1.w, vec2.w));
	}

	public static Vector4f cross(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		float rx = FastMath.fma(y1, z2, -z1 * y2);
		float ry = FastMath.fma(z1, x2, -x1 * z2);
		float rz = FastMath.fma(x1, y2, -y1 * x2);
		return new Vector4f(rx, ry, rz, Math.max(w1, w2));
	}

	public static double sqrt(Vector4f vec) {
		return FastMath.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z + vec.w * vec.w);
	}

	public static double sqrt(float x, float y, float z, float w) {
		return FastMath.sqrt(x * x + y * y + z * z + w * w);
	}

	public static float distance(Vector4f vec1, Vector4f vec2) {
		return FastMath.sqrt(distanceSquared(vec1, vec2));
	}

	public static float distance(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return FastMath.sqrt(distanceSquared(x1, y1, z1, w1, x2, y2, z2, w2));
	}

	public static float distanceSquared(Vector4f vec1, Vector4f vec2) {
		float dx = vec1.x - vec2.x;
		float dy = vec1.y - vec2.y;
		float dz = vec1.z - vec2.z;
		float dw = vec1.w - vec2.w;
		return FastMath.fma(dx, dx, FastMath.fma(dy, dy, FastMath.fma(dz, dz, dw * dw)));
	}

	public static float distanceSquared(float x1, float y1, float z1, float w1, float x2, float y2, float z2,
			float w2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
		float dw = w1 - w2;
		return FastMath.fma(dx, dx, FastMath.fma(dy, dy, FastMath.fma(dz, dz, dw * dw)));
	}

	public static float length(Vector4f vec) {
		return FastMath.sqrt(lengthSquared(vec));
	}

	public static float length(float x, float y, float z, float w) {
		return FastMath.sqrt(lengthSquared(x, y, z, w));
	}

	public static float lengthSquared(Vector4f vec) {
		return FastMath.fma(vec.x, vec.x, FastMath.fma(vec.y, vec.y, FastMath.fma(vec.z, vec.z, vec.w * vec.w)));
	}

	public static float lengthSquared(float x, float y, float z, float w) {
		return FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w)));
	}

	public static Vector4f normalize(Vector4f vec) {
		float scalar = FastMath.invsqrt(FastMath.fma(vec.x, vec.x, FastMath.fma(vec.y, vec.y, FastMath.fma(vec.z, vec.z,
				vec.w * vec.w))));
		return new Vector4f(vec.x * scalar, vec.y * scalar, vec.z * scalar, vec.w * scalar);
	}

	public static Vector4f normalize(float x, float y, float z, float w) {
		float scalar = FastMath.invsqrt(FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w))));
		return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
	}

	public static Vector4f rotate(Vector4f vec, float angle, Vector4f rotVec) {
		return rotate(vec, angle, rotVec.x, rotVec.y, rotVec.z);
	}

	public static Vector4f rotate(Vector4f vec, float angle, float x, float y, float z) {
		if (y == 0.0f && z == 0.0f && FastMath.absEqualsOne(x))
			return rotateX(vec, x * angle);
		else if (x == 0.0f && z == 0.0f && FastMath.absEqualsOne(y))
			return rotateY(vec, y * angle);
		else if (x == 0.0f && y == 0.0f && FastMath.absEqualsOne(z))
			return rotateZ(vec, z * angle);
		return rotateAxisInternal(vec, angle, x, y, z);
	}

	private static Vector4f rotateAxisInternal(Vector4f vec, float angle, float aX, float aY, float aZ) {
		float hangle = angle * 0.5f;
		float sinAngle = FastMath.sin(hangle);
		float qx = aX * sinAngle, qy = aY * sinAngle, qz = aZ * sinAngle;
		float qw = FastMath.cosFromSin(sinAngle, hangle);
		float w2 = qw * qw, x2 = qx * qx, y2 = qy * qy, z2 = qz * qz, zw = qz * qw;
		float xy = qx * qy, xz = qx * qz, yw = qy * qw, yz = qy * qz, xw = qx * qw;
		float x = vec.x, y = vec.y, z = vec.z;
		float vecX = (w2 + x2 - z2 - y2) * x + (-zw + xy - zw + xy) * y + (yw + xz + xz + yw) * z;
		float vecY = (xy + zw + zw + xy) * x + (y2 - z2 + w2 - x2) * y + (yz + yz - xw - xw) * z;
		float vecZ = (xz - yw + xz - yw) * x + (yz + yz + xw + xw) * y + (z2 - y2 - x2 + w2) * z;
		return new Vector4f(vecX, vecY, vecZ, vec.w);
	}

	public static Vector4f rotateX(Vector4f vec, float angle) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float y = vec.y * cos - vec.z * sin;
		float z = vec.y * sin + vec.z * cos;
		return new Vector4f(vec.x, y, z, vec.w);
	}

	public static Vector4f rotateY(Vector4f vec, float angle) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float x = vec.x * cos + vec.z * sin;
		float z = -vec.x * sin + vec.z * cos;
		return new Vector4f(x, vec.y, z, vec.w);
	}

	public static Vector4f rotateZ(Vector4f vec, float angle) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float x = vec.x * cos - vec.y * sin;
		float y = vec.x * sin + vec.y * cos;
		return new Vector4f(x, y, vec.z, vec.w);
	}

	public static final Vector4f getLeft(Vector4f vec) {
		return Vector4f.cross(vec, Vector4f.UP);
	}

	public static final Vector4f getRight(Vector4f vec) {
		return Vector4f.cross(Vector4f.UP, vec);
	}
}
