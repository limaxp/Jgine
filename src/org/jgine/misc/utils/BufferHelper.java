package org.jgine.misc.utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.vector.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIVector3D;

public class BufferHelper {

	public static FloatBuffer createFloatBuffer(int size1, float[] buffer1) {
		return createFloatBuffer(new int[] { size1 }, buffer1);
	}

	public static FloatBuffer createFloatBuffer(int size1, int size2, float[] buffer1, float[] buffer2) {
		return createFloatBuffer(new int[] { size1, size2 }, buffer1, buffer2);
	}

	public static FloatBuffer createFloatBuffer(int size1, int size2, int size3, float[] buffer1, float[] buffer2,
			float[] buffer3) {
		return createFloatBuffer(new int[] { size1, size2, size3 }, buffer1, buffer2, buffer3);
	}

	public static FloatBuffer createFloatBuffer(int size1, int size2, int size3, int size4, float[] buffer1,
			float[] buffer2, float[] buffer3, float[] buffer4) {
		return createFloatBuffer(new int[] { size1, size2, size3, size4 }, buffer1, buffer2, buffer3, buffer4);
	}

	public static FloatBuffer createFloatBuffer(int[] sizes, float[]... buffer) {
		int size = FastMath.sum(sizes) * (buffer[0].length / sizes[0]);
		FloatBuffer newBuffer = BufferUtils.createFloatBuffer(size);
		int[] bufferOffsets = new int[sizes.length];
		int bufferIndex = 0;
		int currentSize = 0;
		for (int i = 0; i < size; i++) {
			int delta = bufferOffsets[bufferIndex]++;
			if (delta < buffer[bufferIndex].length)
				newBuffer.put(buffer[bufferIndex][delta]);
			else
				newBuffer.put(0);

			if (currentSize++ >= sizes[bufferIndex] - 1) {
				if (bufferIndex++ >= sizes.length - 1)
					bufferIndex = 0;
				currentSize = 0;
			}
		}
		newBuffer.flip();
		return newBuffer;
	}

	public static FloatBuffer createFloatBuffer(int size1, AIVector3D.Buffer buffer1) {
		return createFloatBuffer(new int[] { size1 }, buffer1);
	}

	public static FloatBuffer createFloatBuffer(int size1, int size2, AIVector3D.Buffer buffer1,
			AIVector3D.Buffer buffer2) {
		return createFloatBuffer(new int[] { size1, size2 }, buffer1, buffer2);
	}

	public static FloatBuffer createFloatBuffer(int size1, int size2, int size3, AIVector3D.Buffer buffer1,
			AIVector3D.Buffer buffer2, AIVector3D.Buffer buffer3) {
		return createFloatBuffer(new int[] { size1, size2, size3 }, buffer1, buffer2, buffer3);
	}

	public static FloatBuffer createFloatBuffer(int size1, int size2, int size3, int size4, AIVector3D.Buffer buffer1,
			AIVector3D.Buffer buffer2, AIVector3D.Buffer buffer3, AIVector3D.Buffer buffer4) {
		return createFloatBuffer(new int[] { size1, size2, size3, size4 }, buffer1, buffer2, buffer3, buffer4);
	}

	public static FloatBuffer createFloatBuffer(int[] sizes, AIVector3D.Buffer... buffer) {
		int size = FastMath.sum(sizes) * (buffer[0].remaining() + 1 / sizes[0]);
		FloatBuffer newBuffer = BufferUtils.createFloatBuffer(size);
		int bufferIndex = 0;
		int i = 0;
		while (i < size) {
			AIVector3D.Buffer currentBuffer = buffer[bufferIndex];
			if (currentBuffer != null && currentBuffer.hasRemaining()) {
				AIVector3D verctor = currentBuffer.get();
				switch (sizes[bufferIndex]) {
				case 1:
					newBuffer.put(verctor.x());
					i++;
					break;

				case 2:
					newBuffer.put(verctor.x());
					newBuffer.put(verctor.y());
					i += 2;
					break;

				case 3:
					newBuffer.put(verctor.x());
					newBuffer.put(verctor.y());
					newBuffer.put(verctor.z());
					i += 3;
					break;

				default:
					break;
				}
			}
			else {
				for (int j = 0; j < sizes[bufferIndex]; j++)
					newBuffer.put(0);
				i += sizes[bufferIndex];
			}
			if (bufferIndex++ >= sizes.length - 1)
				bufferIndex = 0;
		}
		newBuffer.flip();
		return newBuffer;
	}

	public static float[] calculateNormals(float[] vertices, int[] indices) {
		float[] normals = new float[vertices.length];
		for (int i = 0; i < indices.length; i += 3) {
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f pos0 = new Vector3f(vertices[i0 * 3], vertices[i0 * 3 + 1], vertices[i0 * 3 + 2]);
			Vector3f pos1 = new Vector3f(vertices[i1 * 3], vertices[i1 * 3 + 1], vertices[i1 * 3 + 2]);
			Vector3f pos2 = new Vector3f(vertices[i2 * 3], vertices[i2 * 3 + 1], vertices[i2 * 3 + 2]);
			Vector3f v1 = Vector3f.sub(pos1, pos0);
			Vector3f v2 = Vector3f.sub(pos2, pos0);
			Vector3f normal = Vector3f.cross(v1, v2);
			normal = Vector3f.normalize(normal);

			normals[i0 * 3] += normal.x;
			normals[i0 * 3 + 1] += normal.y;
			normals[i0 * 3 + 2] += normal.z;

			normals[i1 * 3] += normal.x;
			normals[i1 * 3 + 1] += normal.y;
			normals[i1 * 3 + 2] += normal.z;

			normals[i2 * 3] += normal.x;
			normals[i2 * 3 + 1] += normal.y;
			normals[i2 * 3 + 2] += normal.z;
		}

		for (int i = 0; i < normals.length; i += 3) {
			float x = normals[i];
			float y = normals[i + 1];
			float z = normals[i + 2];
			float length = (float) Math.sqrt(x * x + y * y + z * z);
			normals[i] /= length;
			normals[i + 1] /= length;
			normals[i + 2] /= length;
		}
		return normals;
	}

	public static AIVector3D.Buffer calculateNormals(AIVector3D.Buffer vertices, IntBuffer indices) {
		AIVector3D.Buffer normals = AIVector3D.malloc(vertices.remaining());
		int indicesSize = indices.remaining();
		for (int i = 0; i < indicesSize; i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);
			AIVector3D pos0 = vertices.get(i0);
			AIVector3D pos1 = vertices.get(i1);
			AIVector3D pos2 = vertices.get(i2);

			Vector3f v1 = Vector3f.sub(pos1.x(), pos1.y(), pos1.z(), pos0.x(), pos0.y(), pos0.z());
			Vector3f v2 = Vector3f.sub(pos2.x(), pos2.y(), pos2.z(), pos0.x(), pos0.y(), pos0.z());
			Vector3f normal = Vector3f.cross(v1, v2);
			normal = Vector3f.normalize(normal);

			AIVector3D n0 = normals.get(i0);
			n0.set(n0.x() + normal.x, n0.y() + normal.y, n0.z() + normal.z);
			AIVector3D n1 = normals.get(i1);
			n1.set(n1.x() + normal.x, n1.y() + normal.y, n1.z() + normal.z);
			AIVector3D n2 = normals.get(i2);
			n2.set(n2.x() + normal.x, n2.y() + normal.y, n2.z() + normal.z);
		}

		int normalsSize = normals.remaining();
		for (int i = 0; i < normalsSize; i++) {
			AIVector3D normal = vertices.get(i);
			float x = normal.x();
			float y = normal.y();
			float z = normal.z();
			float length = (float) Math.sqrt(x * x + y * y + z * z);
			normal.set(x / length, y / length, z / length);
		}
		return normals;
	}

	public static float[] generateTextureChords(float[] vertices) {
		float[] textureCords = new float[vertices.length];
		boolean first = true;
		for (int i = 0; i < vertices.length; i++) {
			if (first) {
				textureCords[i] = 0;
				first = false;
			}
			else {
				textureCords[i] = 1;
				first = true;
			}
		}
		return textureCords;
	}
}
