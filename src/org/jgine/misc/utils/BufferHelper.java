package org.jgine.misc.utils;

import java.nio.FloatBuffer;
import org.jgine.misc.math.FastMath;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIVector3D;

/**
 * Helper class for buffer creation.
 */
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
			} else {
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
}
