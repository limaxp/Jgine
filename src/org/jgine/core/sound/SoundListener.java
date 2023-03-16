package org.jgine.core.sound;

import static org.lwjgl.openal.AL10.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jgine.utils.math.vector.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

/**
 * An openAL sound listener.
 * <p>
 * See Also: <a href=
 * "https://www.openal.org/documentation/">https://www.openal.org/documentation/</a>
 */
public class SoundListener {

	SoundListener() {
	}

	void init(Vector3f position) {
		set(AL_POSITION, position.x, position.y, position.z);
		set(AL_VELOCITY, 0, 0, 0);
	}

	public void setPosition(Vector3f position) {
		set(AL_POSITION, position.x, position.y, position.z);
	}

	public void setPosition(float x, float y, float z) {
		set(AL_POSITION, x, y, z);
	}

	public Vector3f getPosition() {
		return get3Float(AL_POSITION);
	}

	public void setVelocity(Vector3f velocity) {
		set(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}

	public void setVelocity(float x, float y, float z) {
		set(AL_VELOCITY, x, y, z);
	}

	public Vector3f getVelocity() {
		return get3Float(AL_VELOCITY);
	}

	public void setOrientation(Vector3f at, Vector3f up) {
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		set(AL_ORIENTATION, data);
	}

	public float[] getOrientation() {
		return getFloatArray(AL_ORIENTATION, 6);
	}

	public void setGain(float pitch) {
		set(AL_GAIN, pitch);
	}

	public float getGain() {
		return getFloat(AL_GAIN);
	}

	public void set(int param, float value) {
		alListenerf(param, value);
	}

	public void set(int param, float v1, float v2, float v3) {
		alListener3f(param, v1, v2, v3);
	}

	public void set(int param, Vector3f vec) {
		alListener3f(param, vec.x, vec.y, vec.z);
	}

	public void set(int param, int value) {
		alListeneri(param, value);
	}

	public void set(int param, FloatBuffer values) {
		alListenerfv(param, values);
	}

	public void set(int param, float[] values) {
		alListenerfv(param, values);
	}

	public float getFloat(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(1);
			alGetListenerf(param, buffer);
			return buffer.get(0);
		}
	}

	public Vector3f get3Float(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer bufferX = stack.mallocFloat(1);
			FloatBuffer bufferY = stack.mallocFloat(1);
			FloatBuffer bufferZ = stack.mallocFloat(1);
			alGetListener3f(param, bufferX, bufferY, bufferZ);
			return new Vector3f(bufferX.get(0), bufferY.get(0), bufferZ.get(0));
		}
	}

	public int getInt(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buffer = stack.mallocInt(1);
			alGetListeneri(param, buffer);
			return buffer.get(0);
		}
	}

	public FloatBuffer getFloatBuffer(int param, int size) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		alGetListenerfv(param, buffer);
		return buffer;
	}

	public float[] getFloatArray(int param, int size) {
		return getFloatBuffer(param, size).array();
	}
}
