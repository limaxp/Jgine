package org.jgine.misc.math;

import org.jgine.misc.math.vector.Vector3f;

/**
 * Helper class for matrix calculations.
 * 
 * @author Maximilian Paar
 */
public class MatrixOLD implements Cloneable {

	public static final int SIZE = 16;

	public final float[] d;

	private MatrixOLD(float[] d) {
		this.d = d;
	}

	public final Vector3f getPosition() {
		return new Vector3f(d[3], d[7], d[11]);
	}

	public final Vector3f getRotation() {
		// TODO implements this
		return new Vector3f(d[3], d[7], d[11]);
	}

	public final Vector3f getScale() {
		return new Vector3f(d[0], d[5], d[10]);
	}

	public static MatrixOLD init() {
		return new MatrixOLD(new float[] {
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		});
	}

	public static MatrixOLD asPosition(Vector3f pos) {
		return asPosition(pos.x, pos.y, pos.z);
	}

	public static MatrixOLD asPosition(float x, float y, float z) {
		return new MatrixOLD(new float[] { 1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1
		});
	}

	public static MatrixOLD asScale(Vector3f scale) {
		return asScale(scale.x, scale.y, scale.z);
	}

	public static MatrixOLD asScale(float x, float y, float z) {
		return new MatrixOLD(new float[] {
				x, 0, 0, 0,
				0, y, 0, 0,
				0, 0, z, 0,
				0, 0, 0, 1
		});
	}

	public static MatrixOLD asRotation(Vector3f rot) {
		double x = FastMath.toRadians(rot.x);
		double y = FastMath.toRadians(rot.y);
		double z = FastMath.toRadians(rot.z);

		MatrixOLD rotZ = new MatrixOLD(new float[] {
				(float) FastMath.cos(z), -(float) FastMath.sin(z), 0, 0,
				(float) FastMath.sin(z), (float) FastMath.cos(z), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		});

		MatrixOLD rotX = new MatrixOLD(new float[] {
				1, 0, 0, 0,
				0, (float) FastMath.cos(x), -(float) FastMath.sin(x), 0,
				0, (float) FastMath.sin(x), (float) FastMath.cos(x), 0,
				0, 0, 0, 1
		});

		MatrixOLD rotY = new MatrixOLD(new float[] {
				(float) FastMath.cos(y), 0, -(float) FastMath.sin(y), 0,
				0, 1, 0, 0,
				(float) FastMath.sin(y), 0, (float) FastMath.cos(y), 0,
				0, 0, 0, 1
		});
		MatrixOLD tmp = mult(rotZ, rotX);
		return mult(tmp, rotY);
	}

	public static MatrixOLD asPerspective(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width / height;
		float tanHalfFOV = (float) FastMath.tan(FastMath.toRadians(fov / 2));
		float zRange = zNear - zFar;

		return new MatrixOLD(new float[] {
				1.0f / (tanHalfFOV * aspectRatio), 0, 0, 0,
				0, 1.0f / tanHalfFOV, 0, 0,
				0, 0, (-zNear - zFar) / zRange, 2 * zFar * zNear / zRange,
				0, 0, 1, 0
		});
	}

	public static MatrixOLD asOrthographic(float left, float right, float bottom, float top, float zNear, float zFar) {
		float width = right - left;
		float height = top - bottom;
		float depth = zFar - zNear;

		return new MatrixOLD(new float[] {
				2 / width, 0, 0, -(right + left) / width,
				0, 2 / height, 0, -(top + bottom) / height,
				0, 0, -2 / depth, -(zFar + zNear) / depth,
				0, 0, 0, 1 });
	}

	public static MatrixOLD asCameraRotation(Vector3f forward, Vector3f up) {
		Vector3f right = Vector3f.cross(up, forward);
		return new MatrixOLD(new float[] {
				right.x, right.y, right.z, 0,
				up.x, up.y, up.z, 0,
				forward.x, forward.y, forward.z, 0,
				0, 0, 0, 1
		});
	}

//	public static Matrix mult(Matrix m1, Matrix m2) {
//		Matrix newM = new Matrix(new float[SIZE]);
//		for (int i = 0; i < SIZE; i += 4) {
//			for (int j = 0; j < 4; j++) {
//				newM.d[i + j] = m1.d[i] * m2.d[j] + m1.d[i + 1] * m2.d[4 + j] + m1.d[i + 2] * m2.d[8 + j] + m1.d[i + 3]* m2.d[12 + j];
//			}
//		}
//		return newM;
//	}
	
	public static MatrixOLD mult(MatrixOLD m1, MatrixOLD m2) {
		float[] d2 = m1.d;
		float[] d1 = m2.d;
			float nm00 = FastMath.fma(d1[0], d2[0], FastMath.fma(d1[4], d2[1], FastMath.fma(d1[8], d2[2], d1[12] * d2[3])));
	        float nm01 = FastMath.fma(d1[1], d2[0], FastMath.fma(d1[5], d2[1], FastMath.fma(d1[9], d2[2], d1[13] * d2[3])));
	        float nm02 = FastMath.fma(d1[2], d2[0], FastMath.fma(d1[6], d2[1], FastMath.fma(d1[10], d2[2], d1[14] * d2[3])));
	        float nm03 = FastMath.fma(d1[3], d2[0], FastMath.fma(d1[7], d2[1], FastMath.fma(d1[11], d2[2], d1[15] * d2[3])));
	        float nm10 = FastMath.fma(d1[0], d2[4], FastMath.fma(d1[4], d2[5], FastMath.fma(d1[8], d2[6], d1[12] * d2[7])));
	        float nm11 = FastMath.fma(d1[1], d2[4], FastMath.fma(d1[5], d2[5], FastMath.fma(d1[9], d2[6], d1[13] * d2[7])));
	        float nm12 = FastMath.fma(d1[2], d2[4], FastMath.fma(d1[6], d2[5], FastMath.fma(d1[10], d2[6], d1[14] * d2[7])));
	        float nm13 = FastMath.fma(d1[3], d2[4], FastMath.fma(d1[7], d2[5], FastMath.fma(d1[11], d2[6], d1[15] * d2[7])));
	        float nm20 = FastMath.fma(d1[0], d2[8], FastMath.fma(d1[4], d2[9], FastMath.fma(d1[8], d2[10], d1[12] * d2[11])));
	        float nm21 = FastMath.fma(d1[1], d2[8], FastMath.fma(d1[5], d2[9], FastMath.fma(d1[9], d2[10], d1[13] * d2[11])));
	        float nm22 = FastMath.fma(d1[2], d2[8], FastMath.fma(d1[6], d2[9], FastMath.fma(d1[10], d2[10], d1[14] * d2[11])));
	        float nm23 = FastMath.fma(d1[3], d2[8], FastMath.fma(d1[7], d2[9], FastMath.fma(d1[11], d2[10], d1[15] * d2[11])));
	        float nm30 = FastMath.fma(d1[0], d2[12], FastMath.fma(d1[4], d2[13], FastMath.fma(d1[8], d2[14], d1[12] * d2[15])));
	        float nm31 = FastMath.fma(d1[1], d2[12], FastMath.fma(d1[5], d2[13], FastMath.fma(d1[9], d2[14], d1[13] * d2[15])));
	        float nm32 = FastMath.fma(d1[2], d2[12], FastMath.fma(d1[6], d2[13], FastMath.fma(d1[10], d2[14], d1[14] * d2[15])));
	        float nm33 = FastMath.fma(d1[3], d2[12], FastMath.fma(d1[7], d2[13], FastMath.fma(d1[11], d2[14], d1[15] * d2[15])));
	        
		return new MatrixOLD(new float[] {
					nm00, nm01, nm02, nm03,
			        nm10, nm11, nm12, nm13,
			        nm20, nm21, nm22, nm23,
			        nm30, nm31, nm32, nm33
		});
	}
	

	public Vector3f transform(Vector3f vec, float[] m) {
		return new Vector3f(
				m[0] * vec.x + m[1] * vec.y + m[2] * vec.z + m[3],
				m[4] * vec.x + m[5] * vec.y + m[6] * vec.z + m[7],
				m[8] * vec.x + m[9] * vec.y + m[10] * vec.z + m[11]);
	}
}