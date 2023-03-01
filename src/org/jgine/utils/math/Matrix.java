package org.jgine.utils.math;

import java.nio.FloatBuffer;
import java.text.NumberFormat;

import org.jgine.utils.StringUtils;
import org.jgine.utils.math.rotation.AxisAngle4f;
import org.jgine.utils.math.rotation.Quaternionf;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.math.vector.Vector4f;
import org.jgine.utils.options.Options;

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
 * A 4x4 matrix with float precision.
 */
public class Matrix implements Cloneable {

	public static final int SIZE = 16;

	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	public Matrix() {
		this.m00 = 1;
		this.m11 = 1;
		this.m22 = 1;
		this.m33 = 1;
	}

	public Matrix(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
			float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public Matrix clear() {
		this.m00 = 1;
		this.m01 = 0;
		this.m02 = 0;
		this.m03 = 0;
		this.m10 = 0;
		this.m11 = 1;
		this.m12 = 0;
		this.m13 = 0;
		this.m20 = 0;
		this.m21 = 0;
		this.m22 = 1;
		this.m23 = 0;
		this.m30 = 0;
		this.m31 = 0;
		this.m32 = 0;
		this.m33 = 1;
		return this;
	}

	public Matrix(Matrix m) {
		m00 = m.m00;
		m01 = m.m01;
		m02 = m.m02;
		m03 = m.m03;
		m10 = m.m10;
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m20 = m.m20;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
		m30 = m.m30;
		m31 = m.m31;
		m32 = m.m32;
		m33 = m.m33;
	}

	public Matrix add(Matrix m) {
		m00 += m.m00;
		m01 += m.m01;
		m02 += m.m02;
		m03 += m.m03;
		m10 += m.m10;
		m11 += m.m11;
		m12 += m.m12;
		m13 += m.m13;
		m20 += m.m20;
		m21 += m.m21;
		m22 += m.m22;
		m23 += m.m23;
		m30 += m.m30;
		m31 += m.m31;
		m32 += m.m32;
		m33 += m.m33;
		return this;
	}

	public Matrix sub(Matrix m) {
		m00 -= m.m00;
		m01 -= m.m01;
		m02 -= m.m02;
		m03 -= m.m03;
		m10 -= m.m10;
		m11 -= m.m11;
		m12 -= m.m12;
		m13 -= m.m13;
		m20 -= m.m20;
		m21 -= m.m21;
		m22 -= m.m22;
		m23 -= m.m23;
		m30 -= m.m30;
		m31 -= m.m31;
		m32 -= m.m32;
		m33 -= m.m33;
		return this;
	}

	public Matrix mult(Matrix right) {
		float nm00 = FastMath.fma(m00, right.m00,
				FastMath.fma(m10, right.m01, FastMath.fma(m20, right.m02, m30 * right.m03)));
		float nm01 = FastMath.fma(m01, right.m00,
				FastMath.fma(m11, right.m01, FastMath.fma(m21, right.m02, m31 * right.m03)));
		float nm02 = FastMath.fma(m02, right.m00,
				FastMath.fma(m12, right.m01, FastMath.fma(m22, right.m02, m32 * right.m03)));
		float nm03 = FastMath.fma(m03, right.m00,
				FastMath.fma(m13, right.m01, FastMath.fma(m23, right.m02, m33 * right.m03)));
		float nm10 = FastMath.fma(m00, right.m10,
				FastMath.fma(m10, right.m11, FastMath.fma(m20, right.m12, m30 * right.m13)));
		float nm11 = FastMath.fma(m01, right.m10,
				FastMath.fma(m11, right.m11, FastMath.fma(m21, right.m12, m31 * right.m13)));
		float nm12 = FastMath.fma(m02, right.m10,
				FastMath.fma(m12, right.m11, FastMath.fma(m22, right.m12, m32 * right.m13)));
		float nm13 = FastMath.fma(m03, right.m10,
				FastMath.fma(m13, right.m11, FastMath.fma(m23, right.m12, m33 * right.m13)));
		float nm20 = FastMath.fma(m00, right.m20,
				FastMath.fma(m10, right.m21, FastMath.fma(m20, right.m22, m30 * right.m23)));
		float nm21 = FastMath.fma(m01, right.m20,
				FastMath.fma(m11, right.m21, FastMath.fma(m21, right.m22, m31 * right.m23)));
		float nm22 = FastMath.fma(m02, right.m20,
				FastMath.fma(m12, right.m21, FastMath.fma(m22, right.m22, m32 * right.m23)));
		float nm23 = FastMath.fma(m03, right.m20,
				FastMath.fma(m13, right.m21, FastMath.fma(m23, right.m22, m33 * right.m23)));
		float nm30 = FastMath.fma(m00, right.m30,
				FastMath.fma(m10, right.m31, FastMath.fma(m20, right.m32, m30 * right.m33)));
		float nm31 = FastMath.fma(m01, right.m30,
				FastMath.fma(m11, right.m31, FastMath.fma(m21, right.m32, m31 * right.m33)));
		float nm32 = FastMath.fma(m02, right.m30,
				FastMath.fma(m12, right.m31, FastMath.fma(m22, right.m32, m32 * right.m33)));
		float nm33 = FastMath.fma(m03, right.m30,
				FastMath.fma(m13, right.m31, FastMath.fma(m23, right.m32, m33 * right.m33)));
		m00 = nm00;
		m01 = nm01;
		m02 = nm02;
		m03 = nm03;
		m10 = nm10;
		m11 = nm11;
		m12 = nm12;
		m13 = nm13;
		m20 = nm20;
		m21 = nm21;
		m22 = nm22;
		m23 = nm23;
		m30 = nm30;
		m31 = nm31;
		m32 = nm32;
		m33 = nm33;
		return this;
	}

	/**
	 * Set the position components(m03, m13, m23).
	 * 
	 * @param vec
	 * @return
	 */
	public Matrix setPosition(Vector3f vec) {
		return setPosition(vec.x, vec.y, vec.z);
	}

	/**
	 * Set the position components(m03, m13, m23).
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Matrix setPosition(float x, float y, float z) {
		this.m03 = x;
		this.m13 = y;
		this.m23 = z;
		return this;
	}

	/**
	 * Get the position components(m03, m13, m23).
	 * 
	 * @return
	 */
	public final Vector3f getPosition() {
		return new Vector3f(m03, m13, m23);
	}

	/**
	 * Set the translation components(m30, m31, m32). Note only works for orthogonal
	 * matrices (without any perspective).
	 * 
	 * @param vec
	 * @return
	 */
	public Matrix setTranslation(Vector3f vec) {
		return setTranslation(vec.x, vec.y, vec.z);
	}

	/**
	 * Set the translation components(m30, m31, m32). Note only works for orthogonal
	 * matrices (without any perspective).
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Matrix setTranslation(float x, float y, float z) {
		this.m30 = x;
		this.m31 = y;
		this.m32 = z;
		return this;
	}

	/**
	 * Get the translation components(m30, m31, m32). Note only works for orthogonal
	 * matrices (without any perspective).
	 * 
	 * @return
	 */
	public Vector3f getTranslation() {
		return new Vector3f(m30, m31, m32);
	}

	/**
	 * translates this matrix
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Matrix translate(float x, float y, float z) {
		m30 = FastMath.fma(m00, x, FastMath.fma(m10, y, FastMath.fma(m20, z, m30)));
		m31 = FastMath.fma(m01, x, FastMath.fma(m11, y, FastMath.fma(m21, z, m31)));
		m32 = FastMath.fma(m02, x, FastMath.fma(m12, y, FastMath.fma(m22, z, m32)));
		m33 = FastMath.fma(m03, x, FastMath.fma(m13, y, FastMath.fma(m23, z, m33)));
		return this;
	}

	/**
	 * Set the scale components(m00, m11, m22)
	 * 
	 * @param scale
	 * @return
	 */
	public Matrix scaling(Vector3f scale) {
		return scaling(scale.x, scale.y, scale.z);
	}

	/**
	 * Set the scale components(m00, m11, m22)
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Matrix scaling(float x, float y, float z) {
		this.m00 = x;
		this.m11 = y;
		this.m22 = z;
		return this;
	}

	/**
	 * Get the scale components(m00, m11, m22)
	 * 
	 * @return
	 */
	public Vector3f getScaling() {
		return new Vector3f(m00, m11, m22);
	}

	/**
	 * scales matrix in all axes
	 * 
	 * @param scale
	 * @return
	 */
	public Matrix scale(Vector3f scale) {
		return scale(scale.x, scale.y, scale.z);
	}

	/**
	 * scales matrix in all axes
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Matrix scale(float x, float y, float z) {
		m00 *= x;
		m01 *= x;
		m02 *= x;
		m03 *= x;
		m10 *= y;
		m11 *= y;
		m12 *= y;
		m13 *= y;
		m20 *= z;
		m21 *= z;
		m22 *= z;
		m23 *= z;
		return this;
	}

	public Vector3f getScale() {
		float x = FastMath.sqrt(m00 * m00 + m01 * m01 + m02 * m02);
		float y = FastMath.sqrt(m10 * m10 + m11 * m11 + m12 * m12);
		float z = FastMath.sqrt(m20 * m20 + m21 * m21 + m22 * m22);
		return new Vector3f(x, y, z);
	}

	public float getScaleX() {
		return FastMath.sqrt(m00 * m00 + m01 * m01 + m02 * m02);
	}

	public float getScaleY() {
		return FastMath.sqrt(m10 * m10 + m11 * m11 + m12 * m12);
	}

	public float getScaleZ() {
		return FastMath.sqrt(m20 * m20 + m21 * m21 + m22 * m22);
	}

	/**
	 * Pre-multiply scaling to this matrix by scaling the base axes by the given xyz
	 * factor.
	 * 
	 * @param scale
	 * @return
	 */
	public Matrix scaleLocal(Vector3f scale) {
		return scaleLocal(scale.x, scale.y, scale.z);
	}

	/**
	 * Pre-multiply scaling to this matrix by scaling the base axes by the given xyz
	 * factor.
	 * 
	 * @param scale
	 * @return
	 */
	public Matrix scaleLocal(float x, float y, float z) {
		m00 *= x;
		m01 *= x;
		m02 *= x;
		m10 *= y;
		m11 *= y;
		m12 *= y;
		m20 *= z;
		m21 *= z;
		m22 *= z;
		m30 *= x;
		m31 *= y;
		m32 *= z;
		return this;
	}

	public AxisAngle4f getRotation(AxisAngle4f dest) {
		return dest.set(this);
	}

	public Matrix rotation(float angle, float x, float y, float z) {
		if (y == 0.0f && z == 0.0f && FastMath.absEqualsOne(x))
			return rotationX(x * angle);
		else if (x == 0.0f && z == 0.0f && FastMath.absEqualsOne(y))
			return rotationY(y * angle);
		else if (x == 0.0f && y == 0.0f && FastMath.absEqualsOne(z))
			return rotationZ(z * angle);
		return rotationInternal(angle, x, y, z);
	}

	private Matrix rotationInternal(float angle, float x, float y, float z) {
		float sin = FastMath.sin(angle), cos = FastMath.cosFromSin(sin, angle);
		float C = 1.0f - cos, xy = x * y, xz = x * z, yz = y * z;
		m00 = cos + x * x * C;
		m10 = xy * C - z * sin;
		m20 = xz * C + y * sin;
		m01 = xy * C + z * sin;
		m11 = cos + y * y * C;
		m21 = yz * C - x * sin;
		m02 = xz * C - y * sin;
		m12 = yz * C + x * sin;
		m22 = cos + z * z * C;
		return this;
	}

	public Matrix rotationX(float ang) {
		float sin = FastMath.sin(ang);
		float cos = FastMath.cosFromSin(sin, ang);
		m11 = cos;
		m12 = sin;
		m21 = -sin;
		m22 = cos;
		return this;
	}

	public Matrix rotationY(float ang) {
		float sin = FastMath.sin(ang);
		float cos = FastMath.cosFromSin(sin, ang);
		m00 = cos;
		m02 = -sin;
		m20 = sin;
		m22 = cos;
		return this;
	}

	public Matrix rotationZ(float ang) {
		float sin = FastMath.sin(ang);
		float cos = FastMath.cosFromSin(sin, ang);
		m00 = cos;
		m01 = sin;
		m10 = -sin;
		m11 = cos;
		return this;
	}

	public Matrix rotationXYZ(float angleX, float angleY, float angleZ) {
		float sinX = FastMath.sin(angleX);
		float cosX = FastMath.cosFromSin(sinX, angleX);
		float sinY = FastMath.sin(angleY);
		float cosY = FastMath.cosFromSin(sinY, angleY);
		float sinZ = FastMath.sin(angleZ);
		float cosZ = FastMath.cosFromSin(sinZ, angleZ);
		float nm01 = -sinX * -sinY, nm02 = cosX * -sinY;
		m20 = sinY;
		m21 = -sinX * cosY;
		m22 = cosX * cosY;
		m00 = cosY * cosZ;
		m01 = nm01 * cosZ + cosX * sinZ;
		m02 = nm02 * cosZ + sinX * sinZ;
		m10 = cosY * -sinZ;
		m11 = nm01 * -sinZ + cosX * cosZ;
		m12 = nm02 * -sinZ + sinX * cosZ;
		return this;
	}

	public Matrix rotationXY(float angleX, float angleY) {
		float sinX = FastMath.sin(angleX);
		float cosX = FastMath.cosFromSin(sinX, angleX);
		float sinY = FastMath.sin(angleY);
		float cosY = FastMath.cosFromSin(sinY, angleY);
		float nm01 = -sinX * -sinY, nm02 = cosX * -sinY;
		m20 = sinY;
		m21 = -sinX * cosY;
		m22 = cosX * cosY;
		m00 = cosY;
		m01 = nm01 + cosX;
		m02 = nm02 + sinX;
		m10 = cosY;
		m11 = nm01 + cosX;
		m12 = nm02 + sinX;
		return this;
	}

	// TODO rotateXYZ line 5225

	public Matrix rotation(Quaternionf quat) {
		float w2 = quat.w * quat.w;
		float x2 = quat.x * quat.x;
		float y2 = quat.y * quat.y;
		float z2 = quat.z * quat.z;
		float zw = quat.z * quat.w, dzw = zw + zw;
		float xy = quat.x * quat.y, dxy = xy + xy;
		float xz = quat.x * quat.z, dxz = xz + xz;
		float yw = quat.y * quat.w, dyw = yw + yw;
		float yz = quat.y * quat.z, dyz = yz + yz;
		float xw = quat.x * quat.w, dxw = xw + xw;
		m00 = w2 + x2 - z2 - y2;
		m01 = dxy + dzw;
		m02 = dxz - dyw;
		m10 = -dzw + dxy;
		m11 = y2 - z2 + w2 - x2;
		m12 = dyz + dxw;
		m20 = dyw + dxz;
		m21 = dyz - dxw;
		m22 = z2 - y2 - x2 + w2;
		return this;
	}

	/**
	 * Apply a model transformation to this matrix for a right-handed coordinate
	 * system, that aligns the local +Z axis with (dirX, dirY, dirZ)
	 * 
	 * @param dirX
	 * @param dirY
	 * @param dirZ
	 * @param upX
	 * @param upY
	 * @param upZ
	 * @return
	 */
	public Matrix rotateTowards(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
		// Normalize direction
		float invDirLength = FastMath.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		float ndirX = dirX * invDirLength;
		float ndirY = dirY * invDirLength;
		float ndirZ = dirZ * invDirLength;
		// left = up x direction
		float leftX, leftY, leftZ;
		leftX = upY * ndirZ - upZ * ndirY;
		leftY = upZ * ndirX - upX * ndirZ;
		leftZ = upX * ndirY - upY * ndirX;
		// normalize left
		float invLeftLength = FastMath.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = ndirY * leftZ - ndirZ * leftY;
		float upnY = ndirZ * leftX - ndirX * leftZ;
		float upnZ = ndirX * leftY - ndirY * leftX;
		float rm00 = leftX;
		float rm01 = leftY;
		float rm02 = leftZ;
		float rm10 = upnX;
		float rm11 = upnY;
		float rm12 = upnZ;
		float rm20 = ndirX;
		float rm21 = ndirY;
		float rm22 = ndirZ;
		float nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02;
		float nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02;
		float nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02;
		float nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02;
		float nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12;
		float nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12;
		float nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12;
		float nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12;
		m20 = m00 * rm20 + m10 * rm21 + m20 * rm22;
		m21 = m01 * rm20 + m11 * rm21 + m21 * rm22;
		m22 = m02 * rm20 + m12 * rm21 + m22 * rm22;
		m23 = m03 * rm20 + m13 * rm21 + m23 * rm22;
		m00 = nm00;
		m01 = nm01;
		m02 = nm02;
		m03 = nm03;
		m10 = nm10;
		m11 = nm11;
		m12 = nm12;
		m13 = nm13;
		return this;
	}

	/**
	 * Apply a rotation transformation to this matrix to make -z point along dir
	 * 
	 * @param dir
	 * @param up
	 * @return
	 */
	public Matrix lookAlong(Vector3f dir, Vector3f up) {
		return lookAlong(dir.x, dir.y, dir.z, up.x, up.y, up.z);
	}

	/**
	 * Apply a rotation transformation to this matrix to make -z point along dir
	 * 
	 * @param dirX
	 * @param dirY
	 * @param dirZ
	 * @param upX
	 * @param upY
	 * @param upZ
	 * @return
	 */
	public Matrix lookAlong(float dirX, float dirY, float dirZ, float upX, float upY, float upZ) {
		// Normalize direction
		float invDirLength = FastMath.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		dirX *= -invDirLength;
		dirY *= -invDirLength;
		dirZ *= -invDirLength;
		// left = up x direction
		float leftX, leftY, leftZ;
		leftX = upY * dirZ - upZ * dirY;
		leftY = upZ * dirX - upX * dirZ;
		leftZ = upX * dirY - upY * dirX;
		// normalize left
		float invLeftLength = FastMath.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = dirY * leftZ - dirZ * leftY;
		float upnY = dirZ * leftX - dirX * leftZ;
		float upnZ = dirX * leftY - dirY * leftX;
		// perform optimized matrix multiplication
		// introduce temporaries for dependent results
		float nm00 = m00 * leftX + m10 * upnX + m20 * dirX;
		float nm01 = m01 * leftX + m11 * upnX + m21 * dirX;
		float nm02 = m02 * leftX + m12 * upnX + m22 * dirX;
		float nm03 = m03 * leftX + m13 * upnX + m23 * dirX;
		float nm10 = m00 * leftY + m10 * upnY + m20 * dirY;
		float nm11 = m01 * leftY + m11 * upnY + m21 * dirY;
		float nm12 = m02 * leftY + m12 * upnY + m22 * dirY;
		float nm13 = m03 * leftY + m13 * upnY + m23 * dirY;
		m20 = m00 * leftZ + m10 * upnZ + m20 * dirZ;
		m21 = m01 * leftZ + m11 * upnZ + m21 * dirZ;
		m22 = m02 * leftZ + m12 * upnZ + m22 * dirZ;
		m23 = m03 * leftZ + m13 * upnZ + m23 * dirZ;
		m00 = nm00;
		m01 = nm01;
		m02 = nm02;
		m03 = nm03;
		m10 = nm10;
		m11 = nm11;
		m12 = nm12;
		m13 = nm13;
		return this;
	}

	/**
	 * Set this matrix to be a "lookat" transformation for a right-handed coordinate
	 * system,
	 * 
	 * @param pos
	 * @param target
	 * @param up
	 * @return
	 */
	public Matrix setLookAt(Vector3f pos, Vector3f target, Vector3f up) {
		return setLookAt(pos.x, pos.y, pos.z, target.x, target.y, target.z, up.x, up.y, up.z);
	}

	/**
	 * Set this matrix to be a "lookat" transformation for a right-handed coordinate
	 * system,
	 * 
	 * @param eyeX
	 * @param eyeY
	 * @param eyeZ
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param upX
	 * @param upY
	 * @param upZ
	 * @return
	 */
	public Matrix setLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ) {
		// Compute direction from position to lookAt
		float dirX, dirY, dirZ;
		dirX = eyeX - centerX;
		dirY = eyeY - centerY;
		dirZ = eyeZ - centerZ;
		// Normalize direction
		float invDirLength = FastMath.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		dirX *= invDirLength;
		dirY *= invDirLength;
		dirZ *= invDirLength;
		// left = up x direction
		float leftX, leftY, leftZ;
		leftX = upY * dirZ - upZ * dirY;
		leftY = upZ * dirX - upX * dirZ;
		leftZ = upX * dirY - upY * dirX;
		// normalize left
		float invLeftLength = FastMath.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = dirY * leftZ - dirZ * leftY;
		float upnY = dirZ * leftX - dirX * leftZ;
		float upnZ = dirX * leftY - dirY * leftX;

		m00 = leftX;
		m01 = upnX;
		m02 = dirX;
		m03 = 0.0f;
		m10 = leftY;
		m11 = upnY;
		m12 = dirY;
		m13 = 0.0f;
		m20 = leftZ;
		m21 = upnZ;
		m22 = dirZ;
		m23 = 0.0f;
		m30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ);
		m31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ);
		m32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ);
		m33 = 1.0f;
		return this;
	}

	/**
	 * Apply a "lookat" transformation to this matrix for a right-handed coordinate
	 * system
	 * 
	 * @param pos
	 * @param target
	 * @param up
	 * @return
	 */
	public Matrix lookAt(Vector3f pos, Vector3f target, Vector3f up) {
		return lookAt(pos.x, pos.y, pos.z, target.x, target.y, target.z, up.x, up.y, up.z);
	}

	/**
	 * Apply a "lookat" transformation to this matrix for a right-handed coordinate
	 * system
	 * 
	 * @param eyeX
	 * @param eyeY
	 * @param eyeZ
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param upX
	 * @param upY
	 * @param upZ
	 * @return
	 */
	public Matrix lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ) {
		// Compute direction from position to lookAt
		float dirX, dirY, dirZ;
		dirX = eyeX - centerX;
		dirY = eyeY - centerY;
		dirZ = eyeZ - centerZ;
		// Normalize direction
		float invDirLength = FastMath.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		dirX *= invDirLength;
		dirY *= invDirLength;
		dirZ *= invDirLength;
		// left = up x direction
		float leftX, leftY, leftZ;
		leftX = upY * dirZ - upZ * dirY;
		leftY = upZ * dirX - upX * dirZ;
		leftZ = upX * dirY - upY * dirX;
		// normalize left
		float invLeftLength = FastMath.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = dirY * leftZ - dirZ * leftY;
		float upnY = dirZ * leftX - dirX * leftZ;
		float upnZ = dirX * leftY - dirY * leftX;

		// calculate right matrix elements
		float rm30 = -(leftX * eyeX + leftY * eyeY + leftZ * eyeZ);
		float rm31 = -(upnX * eyeX + upnY * eyeY + upnZ * eyeZ);
		float rm32 = -(dirX * eyeX + dirY * eyeY + dirZ * eyeZ);
		// introduce temporaries for dependent results
		float nm00 = m00 * leftX + m10 * upnX + m20 * dirX;
		float nm01 = m01 * leftX + m11 * upnX + m21 * dirX;
		float nm02 = m02 * leftX + m12 * upnX + m22 * dirX;
		float nm03 = m03 * leftX + m13 * upnX + m23 * dirX;
		float nm10 = m00 * leftY + m10 * upnY + m20 * dirY;
		float nm11 = m01 * leftY + m11 * upnY + m21 * dirY;
		float nm12 = m02 * leftY + m12 * upnY + m22 * dirY;
		float nm13 = m03 * leftY + m13 * upnY + m23 * dirY;

		// perform optimized matrix multiplication
		// compute last column first, because others do not depend on it
		m30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30;
		m31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31;
		m32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32;
		m33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33;
		m20 = m00 * leftZ + m10 * upnZ + m20 * dirZ;
		m21 = m01 * leftZ + m11 * upnZ + m21 * dirZ;
		m22 = m02 * leftZ + m12 * upnZ + m22 * dirZ;
		m23 = m03 * leftZ + m13 * upnZ + m23 * dirZ;
		m00 = nm00;
		m01 = nm01;
		m02 = nm02;
		m03 = nm03;
		m10 = nm10;
		m11 = nm11;
		m12 = nm12;
		m13 = nm13;
		return this;
	}

	/**
	 * Apply a symmetric perspective projection frustum transformation for a
	 * right-handed coordinate system
	 * 
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 * @return
	 */
	public Matrix perspective(float fovy, float aspect, float zNear, float zFar) {
		return perspective(fovy, aspect, zNear, zFar, false);
	}

	/**
	 * Apply a symmetric perspective projection frustum transformation for a
	 * right-handed coordinate system
	 * 
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 * @param zZeroToOne
	 * @return
	 */
	public Matrix perspective(float fovy, float aspect, float zNear, float zFar, boolean zZeroToOne) {
		float h = FastMath.tan(fovy * 0.5f);
		// calculate right matrix elements
		float rm00 = 1.0f / (h * aspect);
		float rm11 = 1.0f / h;
		float rm22;
		float rm32;
		boolean farInf = zFar > 0 && Float.isInfinite(zFar);
		boolean nearInf = zNear > 0 && Float.isInfinite(zNear);
		if (farInf) {
			// See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
			float e = 1E-6f;
			rm22 = e - 1.0f;
			rm32 = (e - (zZeroToOne ? 1.0f : 2.0f)) * zNear;
		} else if (nearInf) {
			float e = 1E-6f;
			rm22 = (zZeroToOne ? 0.0f : 1.0f) - e;
			rm32 = ((zZeroToOne ? 1.0f : 2.0f) - e) * zFar;
		} else {
			rm22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
			rm32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
		}
		// perform optimized matrix multiplication
		float nm20 = m20 * rm22 - m30;
		float nm21 = m21 * rm22 - m31;
		float nm22 = m22 * rm22 - m32;
		float nm23 = m23 * rm22 - m33;
		m00 = m00 * rm00;
		m01 = m01 * rm00;
		m02 = m02 * rm00;
		m03 = m03 * rm00;
		m10 = m10 * rm11;
		m11 = m11 * rm11;
		m12 = m12 * rm11;
		m13 = m13 * rm11;
		m30 = m20 * rm32;
		m31 = m21 * rm32;
		m32 = m22 * rm32;
		m33 = m23 * rm32;
		m20 = nm20;
		m21 = nm21;
		m22 = nm22;
		m23 = nm23;
		return this;
	}

	/**
	 * Set this matrix to be a symmetric perspective projection frustum
	 * transformation for a right-handed coordinate system
	 * 
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 * @param zZeroToOne
	 * @return
	 */
	public Matrix setPerspective(float fovy, float aspect, float zNear, float zFar, boolean zZeroToOne) {
		float h = FastMath.tan(fovy * 0.5f);
		m00 = 1.0f / (h * aspect);
		m11 = 1.0f / h;
		boolean farInf = zFar > 0 && Float.isInfinite(zFar);
		boolean nearInf = zNear > 0 && Float.isInfinite(zNear);
		if (farInf) {
			// See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
			float e = 1E-6f;
			m22 = e - 1.0f;
			m32 = (e - (zZeroToOne ? 1.0f : 2.0f)) * zNear;
		} else if (nearInf) {
			float e = 1E-6f;
			m22 = (zZeroToOne ? 0.0f : 1.0f) - e;
			m32 = ((zZeroToOne ? 1.0f : 2.0f) - e) * zFar;
		} else {
			m22 = (zZeroToOne ? zFar : zFar + zNear) / (zNear - zFar);
			m32 = (zZeroToOne ? zFar : zFar + zFar) * zNear / (zNear - zFar);
		}
		m23 = -1.0f;
		return this;
	}

	/**
	 * Apply an orthographic projection transformation for a right-handed coordinate
	 * system
	 * 
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param zNear
	 * @param zFar
	 * @param zZeroToOne
	 * @return
	 */
	public Matrix orthographic(float left, float right, float bottom, float top, float zNear, float zFar,
			boolean zZeroToOne) {
		// calculate right matrix elements
		float rm00 = 2.0f / (right - left);
		float rm11 = 2.0f / (top - bottom);
		float rm22 = (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar);
		float rm30 = (left + right) / (left - right);
		float rm31 = (top + bottom) / (bottom - top);
		float rm32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);
		// perform optimized multiplication
		// compute the last column first, because other columns do not depend on it
		m30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30;
		m31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31;
		m32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32;
		m33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33;
		m00 = m00 * rm00;
		m01 = m01 * rm00;
		m02 = m02 * rm00;
		m03 = m03 * rm00;
		m10 = m10 * rm11;
		m11 = m11 * rm11;
		m12 = m12 * rm11;
		m13 = m13 * rm11;
		m20 = m20 * rm22;
		m21 = m21 * rm22;
		m22 = m22 * rm22;
		m23 = m23 * rm22;
		return this;
	}

	/**
	 * Set this matrix to be an orthographic projection transformation for a
	 * right-handed coordinate system
	 * 
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param zNear
	 * @param zFar
	 * @param zZeroToOne
	 * @return
	 */
	public Matrix setOrthographic(float left, float right, float bottom, float top, float zNear, float zFar,
			boolean zZeroToOne) {
		m00 = 2.0f / (right - left);
		m11 = 2.0f / (top - bottom);
		m22 = (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar);
		m30 = (right + left) / (left - right);
		m31 = (top + bottom) / (bottom - top);
		m32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);
		return this;
	}

	public Matrix translationRotateScale(Vector3f translation, Quaternionf rotation, Vector3f scale) {
		return translationRotateScale(translation.x, translation.y, translation.z, rotation.x, rotation.y, rotation.z,
				rotation.w, scale.x, scale.y, scale.z);
	}

	public Matrix translationRotateScale(float tx, float ty, float tz, float qx, float qy, float qz, float qw, float sx,
			float sy, float sz) {
		float dqx = qx + qx;
		float dqy = qy + qy;
		float dqz = qz + qz;
		float q00 = dqx * qx;
		float q11 = dqy * qy;
		float q22 = dqz * qz;
		float q01 = dqx * qy;
		float q02 = dqx * qz;
		float q03 = dqx * qw;
		float q12 = dqy * qz;
		float q13 = dqy * qw;
		float q23 = dqz * qw;
		m00 = sx - (q11 + q22) * sx;
		m01 = (q01 + q23) * sx;
		m02 = (q02 - q13) * sx;
		m03 = 0.0f;
		m10 = (q01 - q23) * sy;
		m11 = sy - (q22 + q00) * sy;
		m12 = (q12 + q03) * sy;
		m13 = 0.0f;
		m20 = (q02 + q13) * sz;
		m21 = (q12 - q03) * sz;
		m22 = sz - (q11 + q00) * sz;
		m23 = 0.0f;
		m30 = tx;
		m31 = ty;
		m32 = tz;
		m33 = 1.0f;
		return this;
	}

	public Matrix invert() {
		float a = m00 * m11 - m01 * m10;
		float b = m00 * m12 - m02 * m10;
		float c = m00 * m13 - m03 * m10;
		float d = m01 * m12 - m02 * m11;
		float e = m01 * m13 - m03 * m11;
		float f = m02 * m13 - m03 * m12;
		float g = m20 * m31 - m21 * m30;
		float h = m20 * m32 - m22 * m30;
		float i = m20 * m33 - m23 * m30;
		float j = m21 * m32 - m22 * m31;
		float k = m21 * m33 - m23 * m31;
		float l = m22 * m33 - m23 * m32;
		float det = a * l - b * k + c * j + d * i - e * h + f * g;
		det = 1.0f / det;
		float nm00 = FastMath.fma(m11, l, FastMath.fma(-m12, k, m13 * j)) * det;
		float nm01 = FastMath.fma(-m01, l, FastMath.fma(m02, k, -m03 * j)) * det;
		float nm02 = FastMath.fma(m31, f, FastMath.fma(-m32, e, m33 * d)) * det;
		float nm03 = FastMath.fma(-m21, f, FastMath.fma(m22, e, -m23 * d)) * det;
		float nm10 = FastMath.fma(-m10, l, FastMath.fma(m12, i, -m13 * h)) * det;
		float nm11 = FastMath.fma(m00, l, FastMath.fma(-m02, i, m03 * h)) * det;
		float nm12 = FastMath.fma(-m30, f, FastMath.fma(m32, c, -m33 * b)) * det;
		float nm13 = FastMath.fma(m20, f, FastMath.fma(-m22, c, m23 * b)) * det;
		float nm20 = FastMath.fma(m10, k, FastMath.fma(-m11, i, m13 * g)) * det;
		float nm21 = FastMath.fma(-m00, k, FastMath.fma(m01, i, -m03 * g)) * det;
		float nm22 = FastMath.fma(m30, e, FastMath.fma(-m31, c, m33 * a)) * det;
		float nm23 = FastMath.fma(-m20, e, FastMath.fma(m21, c, -m23 * a)) * det;
		float nm30 = FastMath.fma(-m10, j, FastMath.fma(m11, h, -m12 * g)) * det;
		float nm31 = FastMath.fma(m00, j, FastMath.fma(-m01, h, m02 * g)) * det;
		float nm32 = FastMath.fma(-m30, d, FastMath.fma(m31, b, -m32 * a)) * det;
		float nm33 = FastMath.fma(m20, d, FastMath.fma(-m21, b, m22 * a)) * det;
		m00 = nm00;
		m01 = nm01;
		m02 = nm02;
		m03 = nm03;
		m10 = nm10;
		m11 = nm11;
		m12 = nm12;
		m13 = nm13;
		m20 = nm20;
		m21 = nm21;
		m22 = nm22;
		m23 = nm23;
		m30 = nm30;
		m31 = nm31;
		m32 = nm32;
		m33 = nm33;
		return this;
	}

	public float determinant() {
		return (m00 * m11 - m01 * m10) * (m22 * m33 - m23 * m32) + (m02 * m10 - m00 * m12) * (m21 * m33 - m23 * m31)
				+ (m00 * m13 - m03 * m10) * (m21 * m32 - m22 * m31) + (m01 * m12 - m02 * m11) * (m20 * m33 - m23 * m30)
				+ (m03 * m11 - m01 * m13) * (m20 * m32 - m22 * m30) + (m02 * m13 - m03 * m12) * (m20 * m31 - m21 * m30);
	}

	public Vector4f getRow(int row) {
		switch (row) {
		case 0:
			return new Vector4f(m00, m10, m20, m30);
		case 1:
			return new Vector4f(m01, m11, m21, m31);
		case 2:
			return new Vector4f(m02, m12, m22, m32);
		case 3:
			return new Vector4f(m03, m13, m23, m33);
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	public Matrix setRow(int row, Vector4f src) {
		switch (row) {
		case 0:
			m00 = src.x;
			m10 = src.y;
			m20 = src.z;
			m30 = src.w;
			break;
		case 1:
			m01 = src.x;
			m11 = src.y;
			m21 = src.z;
			m31 = src.w;
			break;
		case 2:
			m02 = src.x;
			m12 = src.y;
			m22 = src.z;
			m32 = src.w;
			break;
		case 3:
			m03 = src.x;
			m13 = src.y;
			m23 = src.z;
			m33 = src.w;
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
		return this;
	}

	public Vector4f getColumn(int column, Vector3f dest) throws IndexOutOfBoundsException {
		switch (column) {
		case 0:
			return new Vector4f(m00, m01, m02, m03);
		case 1:
			return new Vector4f(m10, m11, m12, m13);
		case 2:
			return new Vector4f(m20, m21, m22, m23);
		case 3:
			return new Vector4f(m30, m31, m32, m33);
		default:
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(m00);
		result = prime * result + Float.floatToIntBits(m01);
		result = prime * result + Float.floatToIntBits(m02);
		result = prime * result + Float.floatToIntBits(m03);
		result = prime * result + Float.floatToIntBits(m10);
		result = prime * result + Float.floatToIntBits(m11);
		result = prime * result + Float.floatToIntBits(m12);
		result = prime * result + Float.floatToIntBits(m13);
		result = prime * result + Float.floatToIntBits(m20);
		result = prime * result + Float.floatToIntBits(m21);
		result = prime * result + Float.floatToIntBits(m22);
		result = prime * result + Float.floatToIntBits(m23);
		result = prime * result + Float.floatToIntBits(m30);
		result = prime * result + Float.floatToIntBits(m31);
		result = prime * result + Float.floatToIntBits(m32);
		result = prime * result + Float.floatToIntBits(m33);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Matrix))
			return false;
		Matrix other = (Matrix) obj;
		if (Float.floatToIntBits(m00) != Float.floatToIntBits(other.m00))
			return false;
		if (Float.floatToIntBits(m01) != Float.floatToIntBits(other.m01))
			return false;
		if (Float.floatToIntBits(m02) != Float.floatToIntBits(other.m02))
			return false;
		if (Float.floatToIntBits(m03) != Float.floatToIntBits(other.m03))
			return false;
		if (Float.floatToIntBits(m10) != Float.floatToIntBits(other.m10))
			return false;
		if (Float.floatToIntBits(m11) != Float.floatToIntBits(other.m11))
			return false;
		if (Float.floatToIntBits(m12) != Float.floatToIntBits(other.m12))
			return false;
		if (Float.floatToIntBits(m13) != Float.floatToIntBits(other.m13))
			return false;
		if (Float.floatToIntBits(m20) != Float.floatToIntBits(other.m20))
			return false;
		if (Float.floatToIntBits(m21) != Float.floatToIntBits(other.m21))
			return false;
		if (Float.floatToIntBits(m22) != Float.floatToIntBits(other.m22))
			return false;
		if (Float.floatToIntBits(m23) != Float.floatToIntBits(other.m23))
			return false;
		if (Float.floatToIntBits(m30) != Float.floatToIntBits(other.m30))
			return false;
		if (Float.floatToIntBits(m31) != Float.floatToIntBits(other.m31))
			return false;
		if (Float.floatToIntBits(m32) != Float.floatToIntBits(other.m32))
			return false;
		if (Float.floatToIntBits(m33) != Float.floatToIntBits(other.m33))
			return false;
		return true;
	}

	public FloatBuffer set(FloatBuffer buffer) {
		m00 = buffer.get();
		m01 = buffer.get();
		m02 = buffer.get();
		m03 = buffer.get();
		m10 = buffer.get();
		m11 = buffer.get();
		m12 = buffer.get();
		m13 = buffer.get();
		m20 = buffer.get();
		m21 = buffer.get();
		m22 = buffer.get();
		m23 = buffer.get();
		m30 = buffer.get();
		m31 = buffer.get();
		m32 = buffer.get();
		m33 = buffer.get();
		return buffer;
	}

	public FloatBuffer get(FloatBuffer buffer) {
		buffer.put(m00);
		buffer.put(m01);
		buffer.put(m02);
		buffer.put(m03);
		buffer.put(m10);
		buffer.put(m11);
		buffer.put(m12);
		buffer.put(m13);
		buffer.put(m20);
		buffer.put(m21);
		buffer.put(m22);
		buffer.put(m23);
		buffer.put(m30);
		buffer.put(m31);
		buffer.put(m32);
		buffer.put(m33);
		return buffer;
	}

	public float[] toArray() {
		return new float[] { m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33 };
	}

	@Override
	public String toString() {
		String str = toString(Options.NUMBER_FORMAT);
		StringBuffer res = new StringBuffer();
		int eIndex = Integer.MIN_VALUE;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == 'E') {
				eIndex = i;
			} else if (c == ' ' && eIndex == i - 1) {
				// workaround Java 1.4 DecimalFormat bug
				res.append('+');
				continue;
			} else if (Character.isDigit(c) && eIndex == i - 1) {
				res.append('+');
			}
			res.append(c);
		}
		return res.toString();
	}

	public String toString(NumberFormat formatter) {
		return StringUtils.format(m00, formatter) + " " + StringUtils.format(m10, formatter) + " "
				+ StringUtils.format(m20, formatter) + " " + StringUtils.format(m30, formatter) + "\n"
				+ StringUtils.format(m01, formatter) + " " + StringUtils.format(m11, formatter) + " "
				+ StringUtils.format(m21, formatter) + " " + StringUtils.format(m31, formatter) + "\n"
				+ StringUtils.format(m02, formatter) + " " + StringUtils.format(m12, formatter) + " "
				+ StringUtils.format(m22, formatter) + " " + StringUtils.format(m32, formatter) + "\n"
				+ StringUtils.format(m03, formatter) + " " + StringUtils.format(m13, formatter) + " "
				+ StringUtils.format(m23, formatter) + " " + StringUtils.format(m33, formatter) + "\n";
	}

	public static Matrix asPerspective(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width / height;
		float tanHalfFOV = (float) FastMath.tan(FastMath.toRadians(fov / 2));
		float zRange = zNear - zFar;

		return new Matrix(1.0f / (tanHalfFOV * aspectRatio), 0, 0, 0, 0, 1.0f / tanHalfFOV, 0, 0, 0, 0,
				(-zNear - zFar) / zRange, 2 * zFar * zNear / zRange, 0, 0, 1, 0);
	}

	public static Matrix asOrthographic(float left, float right, float bottom, float top, float zNear, float zFar) {
		float width = right - left;
		float height = top - bottom;
		float depth = zFar - zNear;

		return new Matrix(2 / width, 0, 0, -(right + left) / width, 0, 2 / height, 0, -(top + bottom) / height, 0, 0,
				-2 / depth, -(zFar + zNear) / depth, 0, 0, 0, 1);
	}

	public static Matrix asCameraRotation(Vector3f forward, Vector3f up) {
		Vector3f right = Vector3f.cross(up, forward);
		return new Matrix(right.x, right.y, right.z, 0, up.x, up.y, up.z, 0, forward.x, forward.y, forward.z, 0, 0, 0,
				0, 1);
	}
}