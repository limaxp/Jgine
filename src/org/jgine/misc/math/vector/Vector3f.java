package org.jgine.misc.math.vector;

import org.jgine.misc.math.FastMath;

/*
 * The MIT License
 *
 * Copyright (c) 2015-2021 Richard Greenlees
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Represents an immutable Vector with float precision.
 * 
 * @author Maximilian Paar
 */
public class Vector3f extends Vector2f {

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

	public final float z;

	public Vector3f(float d) {
		super(d);
		this.z = d;
	}

	public Vector3f(float x, float y, float z) {
		super(x, y);
		this.z = z;
	}

	public Vector3f(Vector3f vec) {
		super(vec);
		this.z = vec.z;
	}

	public Vector3f(Vector4f vec) {
		super(vec);
		this.z = vec.z;
	}

	public Vector3f(Vector2f vec) {
		super(vec);
		this.z = 0;
	}

	public Vector3f(Vector2f vec, float z) {
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

	public static Vector3f add(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
	}

	public static Vector3f add(Vector3f vec, float d) {
		return new Vector3f(vec.x + d, vec.y + d, vec.z + d);
	}

	public static Vector3f add(float x, float y, float z, float d) {
		return new Vector3f(x + d, y + d, z + d);
	}

	public static Vector3f add(Vector3f vec, float x, float y, float z) {
		return new Vector3f(vec.x + x, vec.y + y, vec.z + z);
	}

	public static Vector3f add(float x1, float y1, float z1, float x2, float y2, float z2) {
		return new Vector3f(x1 + x2, y1 + y2, z1 + z2);
	}

	public static Vector3f sub(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x - vec2.x, vec1.y - vec2.y, vec1.z - vec2.z);
	}

	public static Vector3f sub(Vector3f vec, float d) {
		return new Vector3f(vec.x - d, vec.y - d, vec.z - d);
	}

	public static Vector3f sub(float x, float y, float z, float d) {
		return new Vector3f(x - d, y - d, z - d);
	}

	public static Vector3f sub(Vector3f vec, float x, float y, float z) {
		return new Vector3f(vec.x - x, vec.y - y, vec.z - z);
	}

	public static Vector3f sub(float x1, float y1, float z1, float x2, float y2, float z2) {
		return new Vector3f(x1 - x2, y1 - y2, z1 - z2);
	}

	public static Vector3f mult(Vector3f vec, float d) {
		return new Vector3f(vec.x * d, vec.y * d, vec.z * d);
	}

	public static Vector3f mult(float x, float y, float z, float d) {
		return new Vector3f(x * d, y * d, z * d);
	}

	public static Vector3f div(Vector3f vec, float d) {
		return new Vector3f(vec.x / d, vec.y / d, vec.z / d);
	}

	public static Vector3f div(float x, float y, float z, float d) {
		return new Vector3f(x / d, y / d, z / d);
	}

	public static float dot(Vector3f vec1, Vector3f vec2) {
		return FastMath.fma(vec1.x, vec2.x, FastMath.fma(vec1.y, vec2.y, vec1.z * vec2.z));
	}

	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return FastMath.fma(x1, x2, FastMath.fma(y1, y2, z1 * z2));
	}

	public static Vector3f cross(Vector3f vec1, Vector3f vec2) {
		float rx = FastMath.fma(vec1.y, vec2.z, -vec1.z * vec2.y);
		float ry = FastMath.fma(vec1.z, vec2.x, -vec1.x * vec2.z);
		float rz = FastMath.fma(vec1.x, vec2.y, -vec1.y * vec2.x);
		return new Vector3f(rx, ry, rz);
	}

	public static Vector3f cross(float x1, float y1, float z1, float x2, float y2, float z2) {
		float rx = FastMath.fma(y1, z2, -z1 * y2);
		float ry = FastMath.fma(z1, x2, -x1 * z2);
		float rz = FastMath.fma(x1, y2, -y1 * x2);
		return new Vector3f(rx, ry, rz);
	}

	public static double sqrt(Vector3f vec) {
		return FastMath.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
	}

	public static double sqrt(float x, float y, float z) {
		return FastMath.sqrt(x * x + y * y + z * z);
	}

	public static float distance(Vector3f vec1, Vector3f vec2) {
		return FastMath.sqrt(distanceSquared(vec1, vec2));
	}

	public static float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
		return FastMath.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2));
	}

	public static float distanceSquared(Vector3f vec1, Vector3f vec2) {
		float dx = vec1.x - vec2.x;
		float dy = vec1.y - vec2.y;
		float dz = vec1.z - vec2.z;
		return FastMath.fma(dx, dx, FastMath.fma(dy, dy, dz * dz));
	}

	public static float distanceSquared(float x1, float y1, float z1, float x2, float y2, float z2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
		return FastMath.fma(dx, dx, FastMath.fma(dy, dy, dz * dz));
	}

	public static float length(Vector3f vec) {
		return FastMath.sqrt(lengthSquared(vec));
	}

	public static float length(float x, float y, float z) {
		return FastMath.sqrt(lengthSquared(x, y, z));
	}

	public static float lengthSquared(Vector3f vec) {
		return FastMath.fma(vec.x, vec.x, FastMath.fma(vec.y, vec.y, vec.z * vec.z));
	}

	public static float lengthSquared(float x, float y, float z) {
		return FastMath.fma(x, x, FastMath.fma(y, y, z * z));
	}

	public static Vector3f normalize(Vector3f vec) {
		float scalar = FastMath.invsqrt(FastMath.fma(vec.x, vec.x, FastMath.fma(vec.y, vec.y, vec.z * vec.z)));
		return new Vector3f(vec.x * scalar, vec.y * scalar, vec.z * scalar);
	}

	public static Vector3f normalize(float x, float y, float z) {
		float scalar = FastMath.invsqrt(FastMath.fma(x, x, FastMath.fma(y, y, z * z)));
		return new Vector3f(x * scalar, y * scalar, z * scalar);
	}

	public static Vector3f rotate(Vector3f vec, float angle, Vector3f rotVec) {
		return rotate(vec, angle, rotVec.x, rotVec.y, rotVec.z);
	}

	public static Vector3f rotate(Vector3f vec, float angle, float x, float y, float z) {
		if (y == 0.0f && z == 0.0f && FastMath.absEqualsOne(x))
			return rotateX(vec, x * angle);
		else if (x == 0.0f && z == 0.0f && FastMath.absEqualsOne(y))
			return rotateY(vec, y * angle);
		else if (x == 0.0f && y == 0.0f && FastMath.absEqualsOne(z))
			return rotateZ(vec, z * angle);
		return rotateAxisInternal(vec, angle, x, y, z);
	}

	private static Vector3f rotateAxisInternal(Vector3f vec, float angle, float aX, float aY, float aZ) {
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
		return new Vector3f(vecX, vecY, vecZ);
	}

	public static Vector3f rotateX(Vector3f vec, float angle) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float y = vec.y * cos - vec.z * sin;
		float z = vec.y * sin + vec.z * cos;
		return new Vector3f(vec.x, y, z);
	}

	public static Vector3f rotateY(Vector3f vec, float angle) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float x = vec.x * cos + vec.z * sin;
		float z = -vec.x * sin + vec.z * cos;
		return new Vector3f(x, vec.y, z);
	}

	public static Vector3f rotateZ(Vector3f vec, float angle) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float x = vec.x * cos - vec.y * sin;
		float y = vec.x * sin + vec.y * cos;
		return new Vector3f(x, y, vec.z);
	}

	public static final Vector3f getLeft(Vector3f vec) {
		return Vector3f.cross(vec, Vector3f.UP);
	}

	public static final Vector3f getRight(Vector3f vec) {
		return Vector3f.cross(Vector3f.UP, vec);
	}
}