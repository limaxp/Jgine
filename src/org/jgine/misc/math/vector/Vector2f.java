package org.jgine.misc.math.vector;

import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.Matrix;

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

public class Vector2f {

	public static final Vector2f NULL = new Vector2f(0);
	public static final Vector2f FULL = new Vector2f(1);
	public static final Vector2f UP = new Vector2f(0, 1);
	public static final Vector2f DOWN = new Vector2f(0, -1);
	public static final Vector2f LEFT = new Vector2f(-1, 0);
	public static final Vector2f RIGHT = new Vector2f(1, 0);
	public static final Vector2f X_AXIS = new Vector2f(1, 0);
	public static final Vector2f Y_AXIS = new Vector2f(0, 1);

	public final float x, y;

	public Vector2f(float d) {
		this.x = d;
		this.y = d;
	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(Vector2f vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	public Vector2f(Vector3f vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	public Vector2f(Vector4f vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Vector2f vec = (Vector2f) obj;
		return vec.x == x && vec.y == y;
	}

	public static Vector2f add(Vector2f vec1, Vector2f vec2) {
		return new Vector2f(vec1.x + vec2.x, vec1.y + vec2.y);
	}

	public static Vector2f add(Vector2f vec, float d) {
		return new Vector2f(vec.x + d, vec.y + d);
	}

	public static Vector2f add(float x, float y, float d) {
		return new Vector2f(x + d, y + d);
	}

	public static Vector2f add(Vector2f vec, float x, float y) {
		return new Vector2f(vec.x + x, vec.y + y);
	}

	public static Vector2f add(float x1, float y1, float x2, float y2) {
		return new Vector2f(x1 + x2, y1 + y2);
	}

	public static Vector2f sub(Vector2f vec1, Vector2f vec2) {
		return new Vector2f(vec1.x - vec2.x, vec1.y - vec2.y);
	}

	public static Vector2f sub(Vector2f vec, float d) {
		return new Vector2f(vec.x - d, vec.y - d);
	}

	public static Vector2f sub(float x, float y, float d) {
		return new Vector2f(x - d, y - d);
	}

	public static Vector2f sub(Vector2f vec, float x, float y) {
		return new Vector2f(vec.x - x, vec.y - y);
	}

	public static Vector2f sub(float x1, float y1, float x2, float y2) {
		return new Vector2f(x1 - x2, y1 - y2);
	}

	public static Vector2f mult(Vector2f vec, float d) {
		return new Vector2f(vec.x * d, vec.y * d);
	}

	public static Vector2f mult(float x, float y, float d) {
		return new Vector2f(x * d, y * d);
	}

	public static Vector2f mult(Vector2f vec, Matrix mat) {
		return mult(vec.x, vec.y, mat);
	}

	public static Vector2f mult(float x, float y, Matrix mat) {
		return new Vector2f(Math.fma(mat.m00, x, mat.m10 * y), Math.fma(mat.m01, x, mat.m11 * y));
	}

	public static Vector2f div(Vector2f vec, float d) {
		return new Vector2f(vec.x / d, vec.y / d);
	}

	public static Vector2f div(float x, float y, float d) {
		return new Vector2f(x / d, y / d);
	}

	public static float dot(Vector2f vec1, Vector2f vec2) {
		return vec1.x * vec2.x + vec1.y * vec2.y;
	}

	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	public static float cross(Vector2f vec1, Vector2f vec2) {
		return vec1.x * vec2.y - vec1.y * vec2.x;
	}

	public static float cross(float x1, float y1, float x2, float y2) {
		return x1 * y2 - y1 * x2;
	}

	public static double sqrt(Vector2f vec) {
		return FastMath.sqrt(vec.x * vec.x + vec.y * vec.y);
	}

	public static double sqrt(float x, float y) {
		return FastMath.sqrt(x * x + y * y);
	}

	public static float distance(Vector2f vec1, Vector2f vec2) {
		return FastMath.sqrt(distanceSquared(vec1, vec2));
	}

	public static float distance(float x1, float y1, float x2, float y2) {
		return FastMath.sqrt(distanceSquared(x1, y1, x2, y2));
	}

	public static float distanceSquared(Vector2f vec1, Vector2f vec2) {
		float dx = vec1.x - vec2.x;
		float dy = vec1.y - vec2.y;
		return dx * dx + dy * dy;
	}

	public static float distanceSquared(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	public static float length(Vector2f vec) {
		return FastMath.sqrt(lengthSquared(vec));
	}

	public static float length(float x, float y) {
		return FastMath.sqrt(lengthSquared(x, y));
	}

	public static float lengthSquared(Vector2f vec) {
		return vec.x * vec.x + vec.y * vec.y;
	}

	public static float lengthSquared(float x, float y) {
		return x * x + y * y;
	}

	public static Vector2f normalize(Vector2f vec) {
		float invLength = FastMath.invsqrt(vec.x * vec.x + vec.y * vec.y);
		return new Vector2f(vec.x * invLength, vec.y * invLength);
	}

	public static Vector2f normalize(float x, float y) {
		float invLength = FastMath.invsqrt(x * x + y * y);
		return new Vector2f(x * invLength, y * invLength);
	}

	// public static Vector2 rotate(Vector2 vec, float angle) {
	// double rad = Math.toRadians(angle);
	// float cos = (float) Math.cos(rad);
	// float sin = (float) Math.sin(rad);
	// return new Vector2(vec.x * cos - vec.y * sin, vec.x * sin + vec.y * cos);
	// }
}