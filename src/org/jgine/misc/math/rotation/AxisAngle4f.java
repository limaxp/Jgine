package org.jgine.misc.math.rotation;

import java.text.NumberFormat;

import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.StringUtils;
import org.jgine.misc.utils.options.Options;

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
 * An axis angle in float precision.
 */
public class AxisAngle4f implements Cloneable {

	public float angle, x, y, z;

	public AxisAngle4f() {
		z = 1.0f;
	}

	public AxisAngle4f(AxisAngle4f a) {
		x = a.x;
		y = a.y;
		z = a.z;
		angle = (float) ((a.angle < 0.0 ? Math.PI + Math.PI + a.angle % (Math.PI + Math.PI) : a.angle)
				% (Math.PI + Math.PI));
	}

	public AxisAngle4f(Quaternionf q) {
		float acos = FastMath.safeAcos(q.w);
		float invSqrt = FastMath.invsqrt(1.0f - q.w * q.w);
		if (Float.isInfinite(invSqrt)) {
			this.x = 0.0f;
			this.y = 0.0f;
			this.z = 1.0f;
		} else {
			this.x = q.x * invSqrt;
			this.y = q.y * invSqrt;
			this.z = q.z * invSqrt;
		}
		this.angle = acos + acos;
	}

	public AxisAngle4f(float angle, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = (float) ((angle < 0.0 ? Math.PI + Math.PI + angle % (Math.PI + Math.PI) : angle)
				% (Math.PI + Math.PI));
	}

	public AxisAngle4f(float angle, Vector3f v) {
		this(angle, v.x, v.y, v.z);
	}

	public AxisAngle4f set(AxisAngle4f a) {
		x = a.x;
		y = a.y;
		z = a.z;
		angle = a.angle;
		angle = (float) ((angle < 0.0 ? Math.PI + Math.PI + angle % (Math.PI + Math.PI) : angle) % (Math.PI + Math.PI));
		return this;
	}

	public AxisAngle4f set(float angle, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = (float) ((angle < 0.0 ? Math.PI + Math.PI + angle % (Math.PI + Math.PI) : angle)
				% (Math.PI + Math.PI));
		return this;
	}

	public AxisAngle4f set(float angle, Vector3f v) {
		return set(angle, v.x, v.y, v.z);
	}

	public AxisAngle4f set(Quaternionf q) {
		float acos = FastMath.safeAcos(q.w);
		float invSqrt = FastMath.invsqrt(1.0f - q.w * q.w);
		if (Float.isInfinite(invSqrt)) {
			this.x = 0.0f;
			this.y = 0.0f;
			this.z = 1.0f;
		} else {
			this.x = q.x * invSqrt;
			this.y = q.y * invSqrt;
			this.z = q.z * invSqrt;
		}
		this.angle = acos + acos;
		return this;
	}

	public AxisAngle4f set(Matrix m) {
		float nm00 = m.m00, nm01 = m.m01, nm02 = m.m02;
		float nm10 = m.m10, nm11 = m.m11, nm12 = m.m12;
		float nm20 = m.m20, nm21 = m.m21, nm22 = m.m22;
		float lenX = FastMath.invsqrt(m.m00 * m.m00 + m.m01 * m.m01 + m.m02 * m.m02);
		float lenY = FastMath.invsqrt(m.m10 * m.m10 + m.m11 * m.m11 + m.m12 * m.m12);
		float lenZ = FastMath.invsqrt(m.m20 * m.m20 + m.m21 * m.m21 + m.m22 * m.m22);
		nm00 *= lenX;
		nm01 *= lenX;
		nm02 *= lenX;
		nm10 *= lenY;
		nm11 *= lenY;
		nm12 *= lenY;
		nm20 *= lenZ;
		nm21 *= lenZ;
		nm22 *= lenZ;
		float epsilon = 1E-4f, epsilon2 = 1E-3f;
		if (FastMath.abs(nm10 - nm01) < epsilon && FastMath.abs(nm20 - nm02) < epsilon
				&& FastMath.abs(nm21 - nm12) < epsilon) {
			if (FastMath.abs(nm10 + nm01) < epsilon2 && FastMath.abs(nm20 + nm02) < epsilon2
					&& FastMath.abs(nm21 + nm12) < epsilon2 && FastMath.abs(nm00 + nm11 + nm22 - 3) < epsilon2) {
				x = 0;
				y = 0;
				z = 1;
				angle = 0;
				return this;
			}
			angle = FastMath.PI_f;
			float xx = (nm00 + 1) / 2;
			float yy = (nm11 + 1) / 2;
			float zz = (nm22 + 1) / 2;
			float xy = (nm10 + nm01) / 4;
			float xz = (nm20 + nm02) / 4;
			float yz = (nm21 + nm12) / 4;
			if ((xx > yy) && (xx > zz)) {
				x = FastMath.sqrt(xx);
				y = xy / x;
				z = xz / x;
			} else if (yy > zz) {
				y = FastMath.sqrt(yy);
				x = xy / y;
				z = yz / y;
			} else {
				z = FastMath.sqrt(zz);
				x = xz / z;
				y = yz / z;
			}
			return this;
		}
		float s = FastMath
				.sqrt((nm12 - nm21) * (nm12 - nm21) + (nm20 - nm02) * (nm20 - nm02) + (nm01 - nm10) * (nm01 - nm10));
		angle = FastMath.safeAcos((nm00 + nm11 + nm22 - 1) / 2);
		x = (nm12 - nm21) / s;
		y = (nm20 - nm02) / s;
		z = (nm01 - nm10) / s;
		return this;
	}

	public AxisAngle4f normalize() {
		float invLength = FastMath.invsqrt(x * x + y * y + z * z);
		x *= invLength;
		y *= invLength;
		z *= invLength;
		return this;
	}

	public AxisAngle4f rotate(float ang) {
		angle += ang;
		angle = (float) ((angle < 0.0 ? FastMath.PI + FastMath.PI + angle % (FastMath.PI + FastMath.PI) : angle)
				% (FastMath.PI + FastMath.PI));
		return this;
	}

	public Vector3f transform(Vector3f v) {
		double sin = FastMath.sin(angle);
		double cos = FastMath.cosFromSin(sin, angle);
		float dot = x * v.x + y * v.y + z * v.z;
		return new Vector3f((float) (v.x * cos + sin * (y * v.z - z * v.y) + (1.0 - cos) * dot * x),
				(float) (v.y * cos + sin * (z * v.x - x * v.z) + (1.0 - cos) * dot * y),
				(float) (v.z * cos + sin * (x * v.y - y * v.x) + (1.0 - cos) * dot * z));
	}

	public Vector4f transform(Vector4f v) {
		double sin = FastMath.sin(angle);
		double cos = FastMath.cosFromSin(sin, angle);
		float dot = x * v.x + y * v.y + z * v.z;
		return new Vector4f((float) (v.x * cos + sin * (y * v.z - z * v.y) + (1.0 - cos) * dot * x),
				(float) (v.y * cos + sin * (z * v.x - x * v.z) + (1.0 - cos) * dot * y),
				(float) (v.z * cos + sin * (x * v.y - y * v.x) + (1.0 - cos) * dot * z), v.w);
	}

	public String toString() {
		return StringUtils.formatNumbers(toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat formatter) {
		return "(" + StringUtils.format(x, formatter) + " " + StringUtils.format(y, formatter) + " "
				+ StringUtils.format(z, formatter) + " <| " + StringUtils.format(angle, formatter) + ")";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		float nangle = (float) ((angle < 0.0 ? Math.PI + Math.PI + angle % (Math.PI + Math.PI) : angle)
				% (Math.PI + Math.PI));
		result = prime * result + Float.floatToIntBits(nangle);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AxisAngle4f other = (AxisAngle4f) obj;
		float nangle = (float) ((angle < 0.0 ? Math.PI + Math.PI + angle % (Math.PI + Math.PI) : angle)
				% (Math.PI + Math.PI));
		float nangleOther = (float) ((other.angle < 0.0 ? Math.PI + Math.PI + other.angle % (Math.PI + Math.PI)
				: other.angle) % (Math.PI + Math.PI));
		if (Float.floatToIntBits(nangle) != Float.floatToIntBits(nangleOther))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
