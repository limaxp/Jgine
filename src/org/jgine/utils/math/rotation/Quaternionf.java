package org.jgine.utils.math.rotation;

import java.text.NumberFormat;

import org.jgine.utils.Options;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;

import maxLibs.utils.StringUtils;

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
 * Class for quaternion mathematics with float precision.
 */
public class Quaternionf implements Cloneable {

	public float x, y, z, w;

	public Quaternionf() {
		this.w = 1.0f;
	}

	public Quaternionf(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternionf(AxisAngle4f axisAngle) {
		float sin = FastMath.sin(axisAngle.angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, axisAngle.angle * 0.5f);
		x = axisAngle.x * sin;
		y = axisAngle.y * sin;
		z = axisAngle.z * sin;
		w = cos;
	}

	public Quaternionf(Quaternionf q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
	}

	public Quaternionf normalize() {
		return normalize(this);
	}

	public Quaternionf normalize(Quaternionf dest) {
		float invNorm = FastMath.invsqrt(FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w))));
		dest.x = x * invNorm;
		dest.y = y * invNorm;
		dest.z = z * invNorm;
		dest.w = w * invNorm;
		return dest;
	}

	public Quaternionf add(float x, float y, float z, float w) {
		return add(x, y, z, w, this);
	}

	public Quaternionf add(float x, float y, float z, float w, Quaternionf dest) {
		dest.x = this.x + x;
		dest.y = this.y + y;
		dest.z = this.z + z;
		dest.w = this.w + w;
		return dest;
	}

	public Quaternionf add(Quaternionf q2) {
		return add(q2, this);
	}

	public Quaternionf add(Quaternionf q2, Quaternionf dest) {
		dest.x = x + q2.x;
		dest.y = y + q2.y;
		dest.z = z + q2.z;
		dest.w = w + q2.w;
		return dest;
	}

	public float dot(Quaternionf other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
	}

	public float angle() {
		return (float) (2.0 * FastMath.safeAcos(w));
	}

	public Quaternionf set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Quaternionf set(Quaternionf q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
		return this;
	}

	public Quaternionf set(AxisAngle4f axisAngle) {
		return setAngleAxis(axisAngle.angle, axisAngle.x, axisAngle.y, axisAngle.z);
	}

	public Quaternionf setAngleAxis(float angle, float x, float y, float z) {
		float s = FastMath.sin(angle * 0.5f);
		this.x = x * s;
		this.y = y * s;
		this.z = z * s;
		this.w = FastMath.cosFromSin(s, angle * 0.5f);
		return this;
	}

	public Quaternionf rotationAxis(float angle, float axisX, float axisY, float axisZ) {
		float hangle = angle / 2.0f;
		float sinAngle = FastMath.sin(hangle);
		float invVLength = FastMath.invsqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
		return set(axisX * invVLength * sinAngle, axisY * invVLength * sinAngle, axisZ * invVLength * sinAngle,
				FastMath.cosFromSin(sinAngle, hangle));
	}

	public Quaternionf rotationX(float angle) {
		float sin = FastMath.sin(angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, angle * 0.5f);
		return set(sin, 0, 0, cos);
	}

	public Quaternionf rotationY(float angle) {
		float sin = FastMath.sin(angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, angle * 0.5f);
		return set(0, sin, 0, cos);
	}

	public Quaternionf rotationZ(float angle) {
		float sin = FastMath.sin(angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, angle * 0.5f);
		return set(0, 0, sin, cos);
	}

	private void setFromUnnormalized(float m00, float m01, float m02, float m10, float m11, float m12, float m20,
			float m21, float m22) {
		float nm00 = m00, nm01 = m01, nm02 = m02;
		float nm10 = m10, nm11 = m11, nm12 = m12;
		float nm20 = m20, nm21 = m21, nm22 = m22;
		float lenX = FastMath.invsqrt(m00 * m00 + m01 * m01 + m02 * m02);
		float lenY = FastMath.invsqrt(m10 * m10 + m11 * m11 + m12 * m12);
		float lenZ = FastMath.invsqrt(m20 * m20 + m21 * m21 + m22 * m22);
		nm00 *= lenX;
		nm01 *= lenX;
		nm02 *= lenX;
		nm10 *= lenY;
		nm11 *= lenY;
		nm12 *= lenY;
		nm20 *= lenZ;
		nm21 *= lenZ;
		nm22 *= lenZ;
		setFromNormalized(nm00, nm01, nm02, nm10, nm11, nm12, nm20, nm21, nm22);
	}

	private void setFromNormalized(float m00, float m01, float m02, float m10, float m11, float m12, float m20,
			float m21, float m22) {
		float t;
		float tr = m00 + m11 + m22;
		if (tr >= 0.0f) {
			t = FastMath.sqrt(tr + 1.0f);
			w = t * 0.5f;
			t = 0.5f / t;
			x = (m12 - m21) * t;
			y = (m20 - m02) * t;
			z = (m01 - m10) * t;
		} else {
			if (m00 >= m11 && m00 >= m22) {
				t = FastMath.sqrt(m00 - (m11 + m22) + 1.0f);
				x = t * 0.5f;
				t = 0.5f / t;
				y = (m10 + m01) * t;
				z = (m02 + m20) * t;
				w = (m12 - m21) * t;
			} else if (m11 > m22) {
				t = FastMath.sqrt(m11 - (m22 + m00) + 1.0f);
				y = t * 0.5f;
				t = 0.5f / t;
				z = (m21 + m12) * t;
				x = (m10 + m01) * t;
				w = (m20 - m02) * t;
			} else {
				t = FastMath.sqrt(m22 - (m00 + m11) + 1.0f);
				z = t * 0.5f;
				t = 0.5f / t;
				x = (m02 + m20) * t;
				y = (m21 + m12) * t;
				w = (m01 - m10) * t;
			}
		}
	}

	public Quaternionf setFromNormalized(Matrix mat) {
		setFromNormalized(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
		return this;
	}

	public Quaternionf setFromUnnormalized(Matrix mat) {
		setFromUnnormalized(mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22);
		return this;
	}

	public Quaternionf fromAxisAngleRad(Vector3f axis, float angle) {
		return fromAxisAngleRad(axis.x, axis.y, axis.z, angle);
	}

	public Quaternionf fromAxisAngleRad(float axisX, float axisY, float axisZ, float angle) {
		float hangle = angle / 2.0f;
		float sinAngle = FastMath.sin(hangle);
		float vLength = FastMath.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
		x = axisX / vLength * sinAngle;
		y = axisY / vLength * sinAngle;
		z = axisZ / vLength * sinAngle;
		w = FastMath.cosFromSin(sinAngle, hangle);
		return this;
	}

	public Quaternionf fromAxisAngleDeg(float axisX, float axisY, float axisZ, float angle) {
		return fromAxisAngleRad(axisX, axisY, axisZ, (float) Math.toRadians(angle));
	}

	public Quaternionf mul(Quaternionf q) {
		return mul(q, this);
	}

	public Quaternionf mul(Quaternionf q, Quaternionf dest) {
		return dest.set(FastMath.fma(w, q.x, FastMath.fma(x, q.w, FastMath.fma(y, q.z, -z * q.y))),
				FastMath.fma(w, q.y, FastMath.fma(-x, q.z, FastMath.fma(y, q.w, z * q.x))),
				FastMath.fma(w, q.z, FastMath.fma(x, q.y, FastMath.fma(-y, q.x, z * q.w))),
				FastMath.fma(w, q.w, FastMath.fma(-x, q.x, FastMath.fma(-y, q.y, -z * q.z))));
	}

	public Quaternionf mul(float qx, float qy, float qz, float qw) {
		return mul(qx, qy, qz, qw, this);
	}

	public Quaternionf mul(float qx, float qy, float qz, float qw, Quaternionf dest) {
		return dest.set(FastMath.fma(w, qx, FastMath.fma(x, qw, FastMath.fma(y, qz, -z * qy))),
				FastMath.fma(w, qy, FastMath.fma(-x, qz, FastMath.fma(y, qw, z * qx))),
				FastMath.fma(w, qz, FastMath.fma(x, qy, FastMath.fma(-y, qx, z * qw))),
				FastMath.fma(w, qw, FastMath.fma(-x, qx, FastMath.fma(-y, qy, -z * qz))));
	}

	public Quaternionf premul(Quaternionf q) {
		return premul(q, this);
	}

	public Quaternionf premul(Quaternionf q, Quaternionf dest) {
		return dest.set(FastMath.fma(q.w, x, FastMath.fma(q.x, w, FastMath.fma(q.y, z, -q.z * y))),
				FastMath.fma(q.w, y, FastMath.fma(-q.x, z, FastMath.fma(q.y, w, q.z * x))),
				FastMath.fma(q.w, z, FastMath.fma(q.x, y, FastMath.fma(-q.y, x, q.z * w))),
				FastMath.fma(q.w, w, FastMath.fma(-q.x, x, FastMath.fma(-q.y, y, -q.z * z))));
	}

	public Quaternionf premul(float qx, float qy, float qz, float qw) {
		return premul(qx, qy, qz, qw, this);
	}

	public Quaternionf premul(float qx, float qy, float qz, float qw, Quaternionf dest) {
		return dest.set(FastMath.fma(qw, x, FastMath.fma(qx, w, FastMath.fma(qy, z, -qz * y))),
				FastMath.fma(qw, y, FastMath.fma(-qx, z, FastMath.fma(qy, w, qz * x))),
				FastMath.fma(qw, z, FastMath.fma(qx, y, FastMath.fma(-qy, x, qz * w))),
				FastMath.fma(qw, w, FastMath.fma(-qx, x, FastMath.fma(-qy, y, -qz * z))));
	}

	public Vector3f transformPositiveX() {
		float ww = w * w;
		float xx = x * x;
		float yy = y * y;
		float zz = z * z;
		float zw = z * w;
		float xy = x * y;
		float xz = x * z;
		float yw = y * w;
		float x = ww + xx - zz - yy;
		float y = xy + zw + zw + xy;
		float z = xz - yw + xz - yw;
		return new Vector3f(x, y, z);
	}

	public Vector3f transformUnitPositiveX() {
		float xy = x * y, xz = x * z, yy = y * y;
		float yw = y * w, zz = z * z, zw = z * w;
		float x = 1 - yy - zz - yy - zz;
		float y = xy + zw + xy + zw;
		float z = xz - yw + xz - yw;
		return new Vector3f(x, y, z);
	}

	public Vector3f transformPositiveY() {
		float ww = w * w;
		float xx = x * x;
		float yy = y * y;
		float zz = z * z;
		float zw = z * w;
		float xy = x * y;
		float yz = y * z;
		float xw = x * w;
		float x = -zw + xy - zw + xy;
		float y = yy - zz + ww - xx;
		float z = yz + yz + xw + xw;
		return new Vector3f(x, y, z);
	}

	public Vector3f transformUnitPositiveY() {
		float xx = x * x, zz = z * z, xy = x * y;
		float yz = y * z, xw = x * w, zw = z * w;
		float x = xy - zw + xy - zw;
		float y = 1 - xx - xx - zz - zz;
		float z = yz + yz + xw + xw;
		return new Vector3f(x, y, z);
	}

	public Vector3f transformPositiveZ() {
		float ww = w * w, xx = x * x, yy = y * y;
		float zz = z * z, xz = x * z, yw = y * w;
		float yz = y * z, xw = x * w;
		float x = yw + xz + xz + yw;
		float y = yz + yz - xw - xw;
		float z = zz - yy - xx + ww;
		return new Vector3f(x, y, z);
	}

	public Vector3f transformUnitPositiveZ() {
		float xx = x * x, yy = y * y, xz = x * z;
		float yz = y * z, xw = x * w, yw = y * w;
		float x = xz + yw + xz + yw;
		float y = yz + yz - xw - xw;
		float z = 1.0f - xx - xx - yy - yy;
		return new Vector3f(x, y, z);
	}

	public Vector3f transform(float x, float y, float z) {
		float xx = this.x * this.x, yy = this.y * this.y, zz = this.z * this.z, ww = this.w * this.w;
		float xy = this.x * this.y, xz = this.x * this.z, yz = this.y * this.z, xw = this.x * this.w;
		float zw = this.z * this.w, yw = this.y * this.w, k = 1 / (xx + yy + zz + ww);
		return new Vector3f(
				FastMath.fma((xx - yy - zz + ww) * k, x, FastMath.fma(2 * (xy - zw) * k, y, (2 * (xz + yw) * k) * z)),
				FastMath.fma(2 * (xy + zw) * k, x, FastMath.fma((yy - xx - zz + ww) * k, y, (2 * (yz - xw) * k) * z)),
				FastMath.fma(2 * (xz - yw) * k, x, FastMath.fma(2 * (yz + xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
	}

	public Vector3f transformInverse(float x, float y, float z) {
		float n = 1.0f / FastMath.fma(this.x, this.x,
				FastMath.fma(this.y, this.y, FastMath.fma(this.z, this.z, this.w * this.w)));
		float qx = this.x * n, qy = this.y * n, qz = this.z * n, qw = this.w * n;
		float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
		float xy = qx * qy, xz = qx * qz, yz = qy * qz, xw = qx * qw;
		float zw = qz * qw, yw = qy * qw, k = 1 / (xx + yy + zz + ww);
		return new Vector3f(
				FastMath.fma((xx - yy - zz + ww) * k, x, FastMath.fma(2 * (xy + zw) * k, y, (2 * (xz - yw) * k) * z)),
				FastMath.fma(2 * (xy - zw) * k, x, FastMath.fma((yy - xx - zz + ww) * k, y, (2 * (yz + xw) * k) * z)),
				FastMath.fma(2 * (xz + yw) * k, x, FastMath.fma(2 * (yz - xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
	}

	public Vector3f transformUnit(float x, float y, float z) {
		float xx = this.x * this.x, xy = this.x * this.y, xz = this.x * this.z;
		float xw = this.x * this.w, yy = this.y * this.y, yz = this.y * this.z;
		float yw = this.y * this.w, zz = this.z * this.z, zw = this.z * this.w;
		return new Vector3f(
				FastMath.fma(FastMath.fma(-2, yy + zz, 1), x, FastMath.fma(2 * (xy - zw), y, (2 * (xz + yw)) * z)),
				FastMath.fma(2 * (xy + zw), x, FastMath.fma(FastMath.fma(-2, xx + zz, 1), y, (2 * (yz - xw)) * z)),
				FastMath.fma(2 * (xz - yw), x, FastMath.fma(2 * (yz + xw), y, FastMath.fma(-2, xx + yy, 1) * z)));
	}

	public Vector3f transformInverseUnit(float x, float y, float z) {
		float xx = this.x * this.x, xy = this.x * this.y, xz = this.x * this.z;
		float xw = this.x * this.w, yy = this.y * this.y, yz = this.y * this.z;
		float yw = this.y * this.w, zz = this.z * this.z, zw = this.z * this.w;
		return new Vector3f(
				FastMath.fma(FastMath.fma(-2, yy + zz, 1), x, FastMath.fma(2 * (xy + zw), y, (2 * (xz - yw)) * z)),
				FastMath.fma(2 * (xy - zw), x, FastMath.fma(FastMath.fma(-2, xx + zz, 1), y, (2 * (yz + xw)) * z)),
				FastMath.fma(2 * (xz + yw), x, FastMath.fma(2 * (yz - xw), y, FastMath.fma(-2, xx + yy, 1) * z)));
	}

	public Quaternionf invert() {
		return invert(this);
	}

	public Quaternionf invert(Quaternionf dest) {
		float invNorm = 1.0f / FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w)));
		dest.x = -x * invNorm;
		dest.y = -y * invNorm;
		dest.z = -z * invNorm;
		dest.w = w * invNorm;
		return dest;
	}

	public Quaternionf div(Quaternionf b) {
		return div(b, this);
	}

	public Quaternionf div(Quaternionf b, Quaternionf dest) {
		float invNorm = 1.0f / FastMath.fma(b.x, b.x, FastMath.fma(b.y, b.y, FastMath.fma(b.z, b.z, b.w * b.w)));
		float x = -b.x * invNorm;
		float y = -b.y * invNorm;
		float z = -b.z * invNorm;
		float w = b.w * invNorm;
		return dest.set(FastMath.fma(this.w, x, FastMath.fma(this.x, w, FastMath.fma(this.y, z, -this.z * y))),
				FastMath.fma(this.w, y, FastMath.fma(-this.x, z, FastMath.fma(this.y, w, this.z * x))),
				FastMath.fma(this.w, z, FastMath.fma(this.x, y, FastMath.fma(-this.y, x, this.z * w))),
				FastMath.fma(this.w, w, FastMath.fma(-this.x, x, FastMath.fma(-this.y, y, -this.z * z))));
	}

	public Quaternionf conjugate() {
		return conjugate(this);
	}

	public Quaternionf conjugate(Quaternionf dest) {
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		dest.w = w;
		return dest;
	}

	public Quaternionf identity() {
		x = 0;
		y = 0;
		z = 0;
		w = 1;
		return this;
	}

	public Quaternionf rotateXYZ(float angleX, float angleY, float angleZ) {
		return rotateXYZ(angleX, angleY, angleZ, this);
	}

	public Quaternionf rotateXYZ(float angleX, float angleY, float angleZ, Quaternionf dest) {
		float sx = FastMath.sin(angleX * 0.5f);
		float cx = FastMath.cosFromSin(sx, angleX * 0.5f);
		float sy = FastMath.sin(angleY * 0.5f);
		float cy = FastMath.cosFromSin(sy, angleY * 0.5f);
		float sz = FastMath.sin(angleZ * 0.5f);
		float cz = FastMath.cosFromSin(sz, angleZ * 0.5f);

		float cycz = cy * cz;
		float sysz = sy * sz;
		float sycz = sy * cz;
		float cysz = cy * sz;
		float w = cx * cycz - sx * sysz;
		float x = sx * cycz + cx * sysz;
		float y = cx * sycz - sx * cysz;
		float z = cx * cysz + sx * sycz;
		// right-multiply
		return dest.set(FastMath.fma(this.w, x, FastMath.fma(this.x, w, FastMath.fma(this.y, z, -this.z * y))),
				FastMath.fma(this.w, y, FastMath.fma(-this.x, z, FastMath.fma(this.y, w, this.z * x))),
				FastMath.fma(this.w, z, FastMath.fma(this.x, y, FastMath.fma(-this.y, x, this.z * w))),
				FastMath.fma(this.w, w, FastMath.fma(-this.x, x, FastMath.fma(-this.y, y, -this.z * z))));
	}

	public Quaternionf rotateZYX(float angleZ, float angleY, float angleX) {
		return rotateZYX(angleZ, angleY, angleX, this);
	}

	public Quaternionf rotateZYX(float angleZ, float angleY, float angleX, Quaternionf dest) {
		float sx = FastMath.sin(angleX * 0.5f);
		float cx = FastMath.cosFromSin(sx, angleX * 0.5f);
		float sy = FastMath.sin(angleY * 0.5f);
		float cy = FastMath.cosFromSin(sy, angleY * 0.5f);
		float sz = FastMath.sin(angleZ * 0.5f);
		float cz = FastMath.cosFromSin(sz, angleZ * 0.5f);

		float cycz = cy * cz;
		float sysz = sy * sz;
		float sycz = sy * cz;
		float cysz = cy * sz;
		float w = cx * cycz + sx * sysz;
		float x = sx * cycz - cx * sysz;
		float y = cx * sycz + sx * cysz;
		float z = cx * cysz - sx * sycz;
		// right-multiply
		return dest.set(FastMath.fma(this.w, x, FastMath.fma(this.x, w, FastMath.fma(this.y, z, -this.z * y))),
				FastMath.fma(this.w, y, FastMath.fma(-this.x, z, FastMath.fma(this.y, w, this.z * x))),
				FastMath.fma(this.w, z, FastMath.fma(this.x, y, FastMath.fma(-this.y, x, this.z * w))),
				FastMath.fma(this.w, w, FastMath.fma(-this.x, x, FastMath.fma(-this.y, y, -this.z * z))));
	}

	public Quaternionf rotateYXZ(float angleY, float angleX, float angleZ) {
		return rotateYXZ(angleY, angleX, angleZ, this);
	}

	public Quaternionf rotateYXZ(float angleY, float angleX, float angleZ, Quaternionf dest) {
		float sx = FastMath.sin(angleX * 0.5f);
		float cx = FastMath.cosFromSin(sx, angleX * 0.5f);
		float sy = FastMath.sin(angleY * 0.5f);
		float cy = FastMath.cosFromSin(sy, angleY * 0.5f);
		float sz = FastMath.sin(angleZ * 0.5f);
		float cz = FastMath.cosFromSin(sz, angleZ * 0.5f);

		float yx = cy * sx;
		float yy = sy * cx;
		float yz = sy * sx;
		float yw = cy * cx;
		float x = yx * cz + yy * sz;
		float y = yy * cz - yx * sz;
		float z = yw * sz - yz * cz;
		float w = yw * cz + yz * sz;
		// right-multiply
		return dest.set(FastMath.fma(this.w, x, FastMath.fma(this.x, w, FastMath.fma(this.y, z, -this.z * y))),
				FastMath.fma(this.w, y, FastMath.fma(-this.x, z, FastMath.fma(this.y, w, this.z * x))),
				FastMath.fma(this.w, z, FastMath.fma(this.x, y, FastMath.fma(-this.y, x, this.z * w))),
				FastMath.fma(this.w, w, FastMath.fma(-this.x, x, FastMath.fma(-this.y, y, -this.z * z))));
	}

	public Vector3f getEulerAnglesXYZ() {
		float xn = FastMath.atan2(x * w - y * z, 0.5f - x * x - y * y);
		float yn = FastMath.safeAsin(2.0f * (x * z + y * w));
		float zn = FastMath.atan2(z * w - x * y, 0.5f - y * y - z * z);
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f getEulerAnglesZYX() {
		float xn = FastMath.atan2(y * z + w * x, 0.5f - x * x + y * y);
		float yn = FastMath.safeAsin(-2.0f * (x * z - w * y));
		float zn = FastMath.atan2(x * y + w * z, 0.5f - y * y - z * z);
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f getEulerAnglesZXY() {
		float xn = FastMath.safeAsin(2.0f * (w * x + y * z));
		float yn = FastMath.atan2(w * y - x * z, 0.5f - y * y - x * x);
		float zn = FastMath.atan2(w * z - x * y, 0.5f - z * z - x * x);
		return new Vector3f(xn, yn, zn);
	}

	public float lengthSquared() {
		return FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w)));
	}

	public Quaternionf rotationXYZ(float angleX, float angleY, float angleZ) {
		float sx = FastMath.sin(angleX * 0.5f);
		float cx = FastMath.cosFromSin(sx, angleX * 0.5f);
		float sy = FastMath.sin(angleY * 0.5f);
		float cy = FastMath.cosFromSin(sy, angleY * 0.5f);
		float sz = FastMath.sin(angleZ * 0.5f);
		float cz = FastMath.cosFromSin(sz, angleZ * 0.5f);

		float cycz = cy * cz;
		float sysz = sy * sz;
		float sycz = sy * cz;
		float cysz = cy * sz;
		w = cx * cycz - sx * sysz;
		x = sx * cycz + cx * sysz;
		y = cx * sycz - sx * cysz;
		z = cx * cysz + sx * sycz;

		return this;
	}

	public Quaternionf rotationZYX(float angleZ, float angleY, float angleX) {
		float sx = FastMath.sin(angleX * 0.5f);
		float cx = FastMath.cosFromSin(sx, angleX * 0.5f);
		float sy = FastMath.sin(angleY * 0.5f);
		float cy = FastMath.cosFromSin(sy, angleY * 0.5f);
		float sz = FastMath.sin(angleZ * 0.5f);
		float cz = FastMath.cosFromSin(sz, angleZ * 0.5f);

		float cycz = cy * cz;
		float sysz = sy * sz;
		float sycz = sy * cz;
		float cysz = cy * sz;
		w = cx * cycz + sx * sysz;
		x = sx * cycz - cx * sysz;
		y = cx * sycz + sx * cysz;
		z = cx * cysz - sx * sycz;

		return this;
	}

	public Quaternionf rotationYXZ(float angleY, float angleX, float angleZ) {
		float sx = FastMath.sin(angleX * 0.5f);
		float cx = FastMath.cosFromSin(sx, angleX * 0.5f);
		float sy = FastMath.sin(angleY * 0.5f);
		float cy = FastMath.cosFromSin(sy, angleY * 0.5f);
		float sz = FastMath.sin(angleZ * 0.5f);
		float cz = FastMath.cosFromSin(sz, angleZ * 0.5f);

		float x = cy * sx;
		float y = sy * cx;
		float z = sy * sx;
		float w = cy * cx;
		this.x = x * cz + y * sz;
		this.y = y * cz - x * sz;
		this.z = w * sz - z * cz;
		this.w = w * cz + z * sz;

		return this;
	}

	public Quaternionf slerp(Quaternionf target, float alpha) {
		return slerp(target, alpha, this);
	}

	public Quaternionf slerp(Quaternionf target, float alpha, Quaternionf dest) {
		float cosom = FastMath.fma(x, target.x, FastMath.fma(y, target.y, FastMath.fma(z, target.z, w * target.w)));
		float absCosom = FastMath.abs(cosom);
		float scale0, scale1;
		if (1.0f - absCosom > 1E-6f) {
			float sinSqr = 1.0f - absCosom * absCosom;
			float sinom = FastMath.invsqrt(sinSqr);
			float omega = FastMath.atan2(sinSqr * sinom, absCosom);
			scale0 = (float) (FastMath.sin((1.0 - alpha) * omega) * sinom);
			scale1 = (float) (FastMath.sin(alpha * omega) * sinom);
		} else {
			scale0 = 1.0f - alpha;
			scale1 = alpha;
		}
		scale1 = cosom >= 0.0f ? scale1 : -scale1;
		dest.x = FastMath.fma(scale0, x, scale1 * target.x);
		dest.y = FastMath.fma(scale0, y, scale1 * target.y);
		dest.z = FastMath.fma(scale0, z, scale1 * target.z);
		dest.w = FastMath.fma(scale0, w, scale1 * target.w);
		return dest;
	}

	public static Quaternionf slerp(Quaternionf[] qs, float[] weights, Quaternionf dest) {
		dest.set(qs[0]);
		float w = weights[0];
		for (int i = 1; i < qs.length; i++) {
			float w0 = w;
			float w1 = weights[i];
			float rw1 = w1 / (w0 + w1);
			w += w1;
			dest.slerp(qs[i], rw1);
		}
		return dest;
	}

	public Quaternionf scale(float factor) {
		return scale(factor, this);
	}

	public Quaternionf scale(float factor, Quaternionf dest) {
		float sqrt = FastMath.sqrt(factor);
		dest.x = sqrt * x;
		dest.y = sqrt * y;
		dest.z = sqrt * z;
		dest.w = sqrt * w;
		return dest;
	}

	public Quaternionf scaling(float factor) {
		float sqrt = FastMath.sqrt(factor);
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		this.w = sqrt;
		return this;
	}

	public Quaternionf integrate(float dt, float vx, float vy, float vz) {
		return integrate(dt, vx, vy, vz, this);
	}

	public Quaternionf integrate(float dt, float vx, float vy, float vz, Quaternionf dest) {
		float thetaX = dt * vx * 0.5f;
		float thetaY = dt * vy * 0.5f;
		float thetaZ = dt * vz * 0.5f;
		float thetaMagSq = thetaX * thetaX + thetaY * thetaY + thetaZ * thetaZ;
		float s;
		float dqX, dqY, dqZ, dqW;
		if (thetaMagSq * thetaMagSq / 24.0f < 1E-8f) {
			dqW = 1.0f - thetaMagSq * 0.5f;
			s = 1.0f - thetaMagSq / 6.0f;
		} else {
			float thetaMag = FastMath.sqrt(thetaMagSq);
			float sin = FastMath.sin(thetaMag);
			s = sin / thetaMag;
			dqW = FastMath.cosFromSin(sin, thetaMag);
		}
		dqX = thetaX * s;
		dqY = thetaY * s;
		dqZ = thetaZ * s;
		/* Pre-multiplication */
		return dest.set(FastMath.fma(dqW, x, FastMath.fma(dqX, w, FastMath.fma(dqY, z, -dqZ * y))),
				FastMath.fma(dqW, y, FastMath.fma(-dqX, z, FastMath.fma(dqY, w, dqZ * x))),
				FastMath.fma(dqW, z, FastMath.fma(dqX, y, FastMath.fma(-dqY, x, dqZ * w))),
				FastMath.fma(dqW, w, FastMath.fma(-dqX, x, FastMath.fma(-dqY, y, -dqZ * z))));
	}

	public Quaternionf nlerp(Quaternionf q, float factor) {
		return nlerp(q, factor, this);
	}

	public Quaternionf nlerp(Quaternionf q, float factor, Quaternionf dest) {
		float cosom = FastMath.fma(x, q.x, FastMath.fma(y, q.y, FastMath.fma(z, q.z, w * q.w)));
		float scale0 = 1.0f - factor;
		float scale1 = (cosom >= 0.0f) ? factor : -factor;
		dest.x = FastMath.fma(scale0, x, scale1 * q.x);
		dest.y = FastMath.fma(scale0, y, scale1 * q.y);
		dest.z = FastMath.fma(scale0, z, scale1 * q.z);
		dest.w = FastMath.fma(scale0, w, scale1 * q.w);
		float s = FastMath.invsqrt(FastMath.fma(dest.x, dest.x,
				FastMath.fma(dest.y, dest.y, FastMath.fma(dest.z, dest.z, dest.w * dest.w))));
		dest.x *= s;
		dest.y *= s;
		dest.z *= s;
		dest.w *= s;
		return dest;
	}

	public static Quaternionf nlerp(Quaternionf[] qs, float[] weights, Quaternionf dest) {
		dest.set(qs[0]);
		float w = weights[0];
		for (int i = 1; i < qs.length; i++) {
			float w0 = w;
			float w1 = weights[i];
			float rw1 = w1 / (w0 + w1);
			w += w1;
			dest.nlerp(qs[i], rw1);
		}
		return dest;
	}

	public Quaternionf nlerpIterative(Quaternionf q, float alpha, float dotThreshold) {
		return nlerpIterative(q, alpha, dotThreshold, this);
	}

	public Quaternionf nlerpIterative(Quaternionf q, float alpha, float dotThreshold, Quaternionf dest) {
		float q1x = x, q1y = y, q1z = z, q1w = w;
		float q2x = q.x, q2y = q.y, q2z = q.z, q2w = q.w;
		float dot = FastMath.fma(q1x, q2x, FastMath.fma(q1y, q2y, FastMath.fma(q1z, q2z, q1w * q2w)));
		float absDot = FastMath.abs(dot);
		if (1.0f - 1E-6f < absDot) {
			return dest.set(this);
		}
		float alphaN = alpha;
		while (absDot < dotThreshold) {
			float scale0 = 0.5f;
			float scale1 = dot >= 0.0f ? 0.5f : -0.5f;
			if (alphaN < 0.5f) {
				q2x = FastMath.fma(scale0, q2x, scale1 * q1x);
				q2y = FastMath.fma(scale0, q2y, scale1 * q1y);
				q2z = FastMath.fma(scale0, q2z, scale1 * q1z);
				q2w = FastMath.fma(scale0, q2w, scale1 * q1w);
				float s = FastMath
						.invsqrt(FastMath.fma(q2x, q2x, FastMath.fma(q2y, q2y, FastMath.fma(q2z, q2z, q2w * q2w))));
				q2x *= s;
				q2y *= s;
				q2z *= s;
				q2w *= s;
				alphaN = alphaN + alphaN;
			} else {
				q1x = FastMath.fma(scale0, q1x, scale1 * q2x);
				q1y = FastMath.fma(scale0, q1y, scale1 * q2y);
				q1z = FastMath.fma(scale0, q1z, scale1 * q2z);
				q1w = FastMath.fma(scale0, q1w, scale1 * q2w);
				float s = FastMath
						.invsqrt(FastMath.fma(q1x, q1x, FastMath.fma(q1y, q1y, FastMath.fma(q1z, q1z, q1w * q1w))));
				q1x *= s;
				q1y *= s;
				q1z *= s;
				q1w *= s;
				alphaN = alphaN + alphaN - 1.0f;
			}
			dot = FastMath.fma(q1x, q2x, FastMath.fma(q1y, q2y, FastMath.fma(q1z, q2z, q1w * q2w)));
			absDot = FastMath.abs(dot);
		}
		float scale0 = 1.0f - alphaN;
		float scale1 = dot >= 0.0f ? alphaN : -alphaN;
		float resX = FastMath.fma(scale0, q1x, scale1 * q2x);
		float resY = FastMath.fma(scale0, q1y, scale1 * q2y);
		float resZ = FastMath.fma(scale0, q1z, scale1 * q2z);
		float resW = FastMath.fma(scale0, q1w, scale1 * q2w);
		float s = FastMath
				.invsqrt(FastMath.fma(resX, resX, FastMath.fma(resY, resY, FastMath.fma(resZ, resZ, resW * resW))));
		dest.x = resX * s;
		dest.y = resY * s;
		dest.z = resZ * s;
		dest.w = resW * s;
		return dest;
	}

	public static Quaternionf nlerpIterative(Quaternionf[] qs, float[] weights, float dotThreshold, Quaternionf dest) {
		dest.set(qs[0]);
		float w = weights[0];
		for (int i = 1; i < qs.length; i++) {
			float w0 = w;
			float w1 = weights[i];
			float rw1 = w1 / (w0 + w1);
			w += w1;
			dest.nlerpIterative(qs[i], rw1, dotThreshold);
		}
		return dest;
	}

	public Quaternionf lookAlong(Vector3f dir, Vector3f up) {
		return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, this);
	}

	public Quaternionf lookAlong(Vector3f dir, Vector3f up, Quaternionf dest) {
		return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z, dest);
	}

	public Quaternionf lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
		return lookAlong(dirX, dirY, dirZ, upX, upY, upZ, this);
	}

	public Quaternionf lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ,
			Quaternionf dest) {
		// Normalize direction
		float invDirLength = FastMath.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		float dirnX = -dirX * invDirLength;
		float dirnY = -dirY * invDirLength;
		float dirnZ = -dirZ * invDirLength;
		// left = up x dir
		float leftX, leftY, leftZ;
		leftX = upY * dirnZ - upZ * dirnY;
		leftY = upZ * dirnX - upX * dirnZ;
		leftZ = upX * dirnY - upY * dirnX;
		// normalize left
		float invLeftLength = FastMath.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = dirnY * leftZ - dirnZ * leftY;
		float upnY = dirnZ * leftX - dirnX * leftZ;
		float upnZ = dirnX * leftY - dirnY * leftX;

		/* Convert orthonormal basis vectors to quaternion */
		float x, y, z, w;
		double t;
		double tr = leftX + upnY + dirnZ;
		if (tr >= 0.0) {
			t = FastMath.sqrt(tr + 1.0);
			w = (float) (t * 0.5);
			t = 0.5 / t;
			x = (float) ((dirnY - upnZ) * t);
			y = (float) ((leftZ - dirnX) * t);
			z = (float) ((upnX - leftY) * t);
		} else {
			if (leftX > upnY && leftX > dirnZ) {
				t = FastMath.sqrt(1.0 + leftX - upnY - dirnZ);
				x = (float) (t * 0.5);
				t = 0.5 / t;
				y = (float) ((leftY + upnX) * t);
				z = (float) ((dirnX + leftZ) * t);
				w = (float) ((dirnY - upnZ) * t);
			} else if (upnY > dirnZ) {
				t = FastMath.sqrt(1.0 + upnY - leftX - dirnZ);
				y = (float) (t * 0.5);
				t = 0.5 / t;
				x = (float) ((leftY + upnX) * t);
				z = (float) ((upnZ + dirnY) * t);
				w = (float) ((leftZ - dirnX) * t);
			} else {
				t = FastMath.sqrt(1.0 + dirnZ - leftX - upnY);
				z = (float) (t * 0.5);
				t = 0.5 / t;
				x = (float) ((dirnX + leftZ) * t);
				y = (float) ((upnZ + dirnY) * t);
				w = (float) ((upnX - leftY) * t);
			}
		}
		/* Multiply */
		return dest.set(FastMath.fma(this.w, x, FastMath.fma(this.x, w, FastMath.fma(this.y, z, -this.z * y))),
				FastMath.fma(this.w, y, FastMath.fma(-this.x, z, FastMath.fma(this.y, w, this.z * x))),
				FastMath.fma(this.w, z, FastMath.fma(this.x, y, FastMath.fma(-this.y, x, this.z * w))),
				FastMath.fma(this.w, w, FastMath.fma(-this.x, x, FastMath.fma(-this.y, y, -this.z * z))));
	}

	public Quaternionf rotationTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY,
			float toDirZ) {
		float fn = FastMath
				.invsqrt(FastMath.fma(fromDirX, fromDirX, FastMath.fma(fromDirY, fromDirY, fromDirZ * fromDirZ)));
		float tn = FastMath.invsqrt(FastMath.fma(toDirX, toDirX, FastMath.fma(toDirY, toDirY, toDirZ * toDirZ)));
		float fx = fromDirX * fn, fy = fromDirY * fn, fz = fromDirZ * fn;
		float tx = toDirX * tn, ty = toDirY * tn, tz = toDirZ * tn;
		float dot = fx * tx + fy * ty + fz * tz;
		float x, y, z, w;
		if (dot < -1.0f + 1E-6f) {
			x = fy;
			y = -fx;
			z = 0.0f;
			w = 0.0f;
			if (x * x + y * y == 0.0f) {
				x = 0.0f;
				y = fz;
				z = -fy;
				w = 0.0f;
			}
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = 0;
		} else {
			float sd2 = FastMath.sqrt((1.0f + dot) * 2.0f);
			float isd2 = 1.0f / sd2;
			float cx = fy * tz - fz * ty;
			float cy = fz * tx - fx * tz;
			float cz = fx * ty - fy * tx;
			x = cx * isd2;
			y = cy * isd2;
			z = cz * isd2;
			w = sd2 * 0.5f;
			float n2 = FastMath.invsqrt(FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w))));
			this.x = x * n2;
			this.y = y * n2;
			this.z = z * n2;
			this.w = w * n2;
		}
		return this;
	}

	public Quaternionf rotationTo(Vector3f fromDir, Vector3f toDir) {
		return rotationTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z);
	}

	public Quaternionf rotateTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY,
			float toDirZ, Quaternionf dest) {
		float fn = FastMath
				.invsqrt(FastMath.fma(fromDirX, fromDirX, FastMath.fma(fromDirY, fromDirY, fromDirZ * fromDirZ)));
		float tn = FastMath.invsqrt(FastMath.fma(toDirX, toDirX, FastMath.fma(toDirY, toDirY, toDirZ * toDirZ)));
		float fx = fromDirX * fn, fy = fromDirY * fn, fz = fromDirZ * fn;
		float tx = toDirX * tn, ty = toDirY * tn, tz = toDirZ * tn;
		float dot = fx * tx + fy * ty + fz * tz;
		float x, y, z, w;
		if (dot < -1.0f + 1E-6f) {
			x = fy;
			y = -fx;
			z = 0.0f;
			w = 0.0f;
			if (x * x + y * y == 0.0f) {
				x = 0.0f;
				y = fz;
				z = -fy;
				w = 0.0f;
			}
		} else {
			float sd2 = FastMath.sqrt((1.0f + dot) * 2.0f);
			float isd2 = 1.0f / sd2;
			float cx = fy * tz - fz * ty;
			float cy = fz * tx - fx * tz;
			float cz = fx * ty - fy * tx;
			x = cx * isd2;
			y = cy * isd2;
			z = cz * isd2;
			w = sd2 * 0.5f;
			float n2 = FastMath.invsqrt(FastMath.fma(x, x, FastMath.fma(y, y, FastMath.fma(z, z, w * w))));
			x *= n2;
			y *= n2;
			z *= n2;
			w *= n2;
		}
		/* Multiply */
		return dest.set(FastMath.fma(this.w, x, FastMath.fma(this.x, w, FastMath.fma(this.y, z, -this.z * y))),
				FastMath.fma(this.w, y, FastMath.fma(-this.x, z, FastMath.fma(this.y, w, this.z * x))),
				FastMath.fma(this.w, z, FastMath.fma(this.x, y, FastMath.fma(-this.y, x, this.z * w))),
				FastMath.fma(this.w, w, FastMath.fma(-this.x, x, FastMath.fma(-this.y, y, -this.z * z))));
	}

	public Quaternionf rotateTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY,
			float toDirZ) {
		return rotateTo(fromDirX, fromDirY, fromDirZ, toDirX, toDirY, toDirZ, this);
	}

	public Quaternionf rotateTo(Vector3f fromDir, Vector3f toDir, Quaternionf dest) {
		return rotateTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z, dest);
	}

	public Quaternionf rotateTo(Vector3f fromDir, Vector3f toDir) {
		return rotateTo(fromDir.x, fromDir.y, fromDir.z, toDir.x, toDir.y, toDir.z, this);
	}

	public Quaternionf rotateX(float angle) {
		return rotateX(angle, this);
	}

	public Quaternionf rotateX(float angle, Quaternionf dest) {
		float sin = FastMath.sin(angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, angle * 0.5f);
		return dest.set(w * sin + x * cos, y * cos + z * sin, z * cos - y * sin, w * cos - x * sin);
	}

	public Quaternionf rotateY(float angle) {
		return rotateY(angle, this);
	}

	public Quaternionf rotateY(float angle, Quaternionf dest) {
		float sin = FastMath.sin(angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, angle * 0.5f);
		return dest.set(x * cos - z * sin, w * sin + y * cos, x * sin + z * cos, w * cos - y * sin);
	}

	public Quaternionf rotateZ(float angle) {
		return rotateZ(angle, this);
	}

	public Quaternionf rotateZ(float angle, Quaternionf dest) {
		float sin = FastMath.sin(angle * 0.5f);
		float cos = FastMath.cosFromSin(sin, angle * 0.5f);
		return dest.set(x * cos + y * sin, y * cos - x * sin, w * sin + z * cos, w * cos - z * sin);
	}

	public Quaternionf rotateLocalX(float angle) {
		return rotateLocalX(angle, this);
	}

	public Quaternionf rotateLocalX(float angle, Quaternionf dest) {
		float hangle = angle * 0.5f;
		float s = FastMath.sin(hangle);
		float c = FastMath.cosFromSin(s, hangle);
		dest.set(c * x + s * w, c * y - s * z, c * z + s * y, c * w - s * x);
		return dest;
	}

	public Quaternionf rotateLocalY(float angle) {
		return rotateLocalY(angle, this);
	}

	public Quaternionf rotateLocalY(float angle, Quaternionf dest) {
		float hangle = angle * 0.5f;
		float s = FastMath.sin(hangle);
		float c = FastMath.cosFromSin(s, hangle);
		dest.set(c * x + s * z, c * y + s * w, c * z - s * x, c * w - s * y);
		return dest;
	}

	public Quaternionf rotateLocalZ(float angle) {
		return rotateLocalZ(angle, this);
	}

	public Quaternionf rotateLocalZ(float angle, Quaternionf dest) {
		float hangle = angle * 0.5f;
		float s = FastMath.sin(hangle);
		float c = FastMath.cosFromSin(s, hangle);
		dest.set(c * x - s * y, c * y + s * x, c * z + s * w, c * w - s * z);
		return dest;
	}

	public Quaternionf rotateAxis(float angle, float axisX, float axisY, float axisZ, Quaternionf dest) {
		float hangle = angle / 2.0f;
		float sinAngle = FastMath.sin(hangle);
		float invVLength = FastMath.invsqrt(FastMath.fma(axisX, axisX, FastMath.fma(axisY, axisY, axisZ * axisZ)));
		float rx = axisX * invVLength * sinAngle;
		float ry = axisY * invVLength * sinAngle;
		float rz = axisZ * invVLength * sinAngle;
		float rw = FastMath.cosFromSin(sinAngle, hangle);
		return dest.set(FastMath.fma(this.w, rx, FastMath.fma(this.x, rw, FastMath.fma(this.y, rz, -this.z * ry))),
				FastMath.fma(this.w, ry, FastMath.fma(-this.x, rz, FastMath.fma(this.y, rw, this.z * rx))),
				FastMath.fma(this.w, rz, FastMath.fma(this.x, ry, FastMath.fma(-this.y, rx, this.z * rw))),
				FastMath.fma(this.w, rw, FastMath.fma(-this.x, rx, FastMath.fma(-this.y, ry, -this.z * rz))));
	}

	public Quaternionf rotateAxis(float angle, Vector3f axis, Quaternionf dest) {
		return rotateAxis(angle, axis.x, axis.y, axis.z, dest);
	}

	public Quaternionf rotateAxis(float angle, Vector3f axis) {
		return rotateAxis(angle, axis.x, axis.y, axis.z, this);
	}

	public Quaternionf rotateAxis(float angle, float axisX, float axisY, float axisZ) {
		return rotateAxis(angle, axisX, axisY, axisZ, this);
	}

	public String toString() {
		return StringUtils.formatNumbers(toString(Options.NUMBER_FORMAT));
	}

	public String toString(NumberFormat formatter) {
		return "(" + StringUtils.format(x, formatter) + " " + StringUtils.format(y, formatter) + " "
				+ StringUtils.format(z, formatter) + " " + StringUtils.format(w, formatter) + ")";
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(w);
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
		Quaternionf other = (Quaternionf) obj;
		if (Float.floatToIntBits(w) != Float.floatToIntBits(other.w))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	public Quaternionf difference(Quaternionf other) {
		return difference(other, this);
	}

	public Quaternionf difference(Quaternionf other, Quaternionf dest) {
		float invNorm = 1.0f / lengthSquared();
		float x = -this.x * invNorm;
		float y = -this.y * invNorm;
		float z = -this.z * invNorm;
		float w = this.w * invNorm;
		dest.set(FastMath.fma(w, other.x, FastMath.fma(x, other.w, FastMath.fma(y, other.z, -z * other.y))),
				FastMath.fma(w, other.y, FastMath.fma(-x, other.z, FastMath.fma(y, other.w, z * other.x))),
				FastMath.fma(w, other.z, FastMath.fma(x, other.y, FastMath.fma(-y, other.x, z * other.w))),
				FastMath.fma(w, other.w, FastMath.fma(-x, other.x, FastMath.fma(-y, other.y, -z * other.z))));
		return dest;
	}

	public Vector3f positiveX(Vector3f dir) {
		float invNorm = 1.0f / lengthSquared();
		float nx = -x * invNorm;
		float ny = -y * invNorm;
		float nz = -z * invNorm;
		float nw = w * invNorm;
		float dy = ny + ny;
		float dz = nz + nz;
		float xn = -ny * dy - nz * dz + 1.0f;
		float yn = nx * dy + nw * dz;
		float zn = nx * dz - nw * dy;
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f normalizedPositiveX(Vector3f dir) {
		float dy = y + y;
		float dz = z + z;
		float xn = -y * dy - z * dz + 1.0f;
		float yn = x * dy - w * dz;
		float zn = x * dz + w * dy;
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f positiveY(Vector3f dir) {
		float invNorm = 1.0f / lengthSquared();
		float nx = -x * invNorm;
		float ny = -y * invNorm;
		float nz = -z * invNorm;
		float nw = w * invNorm;
		float dx = nx + nx;
		float dy = ny + ny;
		float dz = nz + nz;
		float xn = nx * dy - nw * dz;
		float yn = -nx * dx - nz * dz + 1.0f;
		float zn = ny * dz + nw * dx;
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f normalizedPositiveY(Vector3f dir) {
		float dx = x + x;
		float dy = y + y;
		float dz = z + z;
		float xn = x * dy + w * dz;
		float yn = -x * dx - z * dz + 1.0f;
		float zn = y * dz - w * dx;
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f positiveZ(Vector3f dir) {
		float invNorm = 1.0f / lengthSquared();
		float nx = -x * invNorm;
		float ny = -y * invNorm;
		float nz = -z * invNorm;
		float nw = w * invNorm;
		float dx = nx + nx;
		float dy = ny + ny;
		float dz = nz + nz;
		float xn = nx * dz + nw * dy;
		float yn = ny * dz - nw * dx;
		float zn = -nx * dx - ny * dy + 1.0f;
		return new Vector3f(xn, yn, zn);
	}

	public Vector3f normalizedPositiveZ(Vector3f dir) {
		float dx = x + x;
		float dy = y + y;
		float dz = z + z;
		float xn = x * dz - w * dy;
		float yn = y * dz + w * dx;
		float zn = -x * dx - y * dy + 1.0f;
		return new Vector3f(xn, yn, zn);
	}

	public Quaternionf conjugateBy(Quaternionf q) {
		return conjugateBy(q, this);
	}

	public Quaternionf conjugateBy(Quaternionf q, Quaternionf dest) {
		float invNorm = 1.0f / q.lengthSquared();
		float qix = -q.x * invNorm, qiy = -q.y * invNorm, qiz = -q.z * invNorm, qiw = q.w * invNorm;
		float qpx = FastMath.fma(q.w, x, FastMath.fma(q.x, w, FastMath.fma(q.y, z, -q.z * y)));
		float qpy = FastMath.fma(q.w, y, FastMath.fma(-q.x, z, FastMath.fma(q.y, w, q.z * x)));
		float qpz = FastMath.fma(q.w, z, FastMath.fma(q.x, y, FastMath.fma(-q.y, x, q.z * w)));
		float qpw = FastMath.fma(q.w, w, FastMath.fma(-q.x, x, FastMath.fma(-q.y, y, -q.z * z)));
		return dest.set(FastMath.fma(qpw, qix, FastMath.fma(qpx, qiw, FastMath.fma(qpy, qiz, -qpz * qiy))),
				FastMath.fma(qpw, qiy, FastMath.fma(-qpx, qiz, FastMath.fma(qpy, qiw, qpz * qix))),
				FastMath.fma(qpw, qiz, FastMath.fma(qpx, qiy, FastMath.fma(-qpy, qix, qpz * qiw))),
				FastMath.fma(qpw, qiw, FastMath.fma(-qpx, qix, FastMath.fma(-qpy, qiy, -qpz * qiz))));
	}

	public boolean isFinite() {
		return FastMath.isFinite(x) && FastMath.isFinite(y) && FastMath.isFinite(z) && FastMath.isFinite(w);
	}

	public boolean equals(Quaternionf q, float delta) {
		if (this == q)
			return true;
		if (q == null)
			return false;
		if (!(q instanceof Quaternionf))
			return false;
		if (!equals(x, q.x, delta))
			return false;
		if (!equals(y, q.y, delta))
			return false;
		if (!equals(z, q.z, delta))
			return false;
		if (!equals(w, q.w, delta))
			return false;
		return true;
	}

	public boolean equals(float x, float y, float z, float w) {
		if (Float.floatToIntBits(this.x) != Float.floatToIntBits(x))
			return false;
		if (Float.floatToIntBits(this.y) != Float.floatToIntBits(y))
			return false;
		if (Float.floatToIntBits(this.z) != Float.floatToIntBits(z))
			return false;
		if (Float.floatToIntBits(this.w) != Float.floatToIntBits(w))
			return false;
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static boolean equals(float a, float b, float delta) {
		return Float.floatToIntBits(a) == Float.floatToIntBits(b) || Math.abs(a - b) <= delta;
	}

	public static boolean equals(double a, double b, double delta) {
		return Double.doubleToLongBits(a) == Double.doubleToLongBits(b) || Math.abs(a - b) <= delta;
	}
}
