package org.jgine.core.sound.effect;

import static org.lwjgl.openal.EXTEfx.AL_BANDPASS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_BANDPASS_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_BANDPASS_GAINLF;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_BANDPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_FIRST_PARAMETER;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_HIGHPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_LAST_PARAMETER;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_LOWPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_NULL;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_TYPE;
import static org.lwjgl.openal.EXTEfx.AL_HIGHPASS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_HIGHPASS_GAINLF;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_GAINHF;
import static org.lwjgl.openal.EXTEfx.alDeleteFilters;
import static org.lwjgl.openal.EXTEfx.alFilterf;
import static org.lwjgl.openal.EXTEfx.alFilterfv;
import static org.lwjgl.openal.EXTEfx.alFilteri;
import static org.lwjgl.openal.EXTEfx.alFilteriv;
import static org.lwjgl.openal.EXTEfx.alGenFilters;
import static org.lwjgl.openal.EXTEfx.alGetFilterf;
import static org.lwjgl.openal.EXTEfx.alGetFilterfv;
import static org.lwjgl.openal.EXTEfx.alGetFilteri;
import static org.lwjgl.openal.EXTEfx.alGetFilteriv;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class SoundFilter implements AutoCloseable {

	public static class Type {

		public static final int NULL = AL_FILTER_NULL;
		public static final int LOWPASS = AL_FILTER_LOWPASS;
		public static final int HIGHPASS = AL_FILTER_HIGHPASS;
		public static final int BANDPASS = AL_FILTER_BANDPASS;
	}

	public static class Parameter {

		public static final int FILTER_TYPE = AL_FILTER_TYPE;
		public static final int FILTER_FIRST_PARAMETER = AL_FILTER_FIRST_PARAMETER;
		public static final int FILTER_LAST_PARAMETER = AL_FILTER_LAST_PARAMETER;

		public static final int LOWPASS_GAIN = AL_LOWPASS_GAIN;
		public static final int LOWPASS_GAINHF = AL_LOWPASS_GAINHF;

		public static final int HIGHPASS_GAIN = AL_HIGHPASS_GAIN;
		public static final int HIGHPASS_GAINLF = AL_HIGHPASS_GAINLF;

		public static final int BANDPASS_GAIN = AL_BANDPASS_GAIN;
		public static final int BANDPASS_GAINHF = AL_BANDPASS_GAINHF;
		public static final int BANDPASS_GAINLF = AL_BANDPASS_GAINLF;
	}

	public static SoundFilter lowPass() {
		SoundFilter filter = new SoundFilter();
		filter.setType(Type.LOWPASS);
		return filter;
	}

	public static SoundFilter highPass() {
		SoundFilter filter = new SoundFilter();
		filter.setType(Type.HIGHPASS);
		return filter;
	}

	public static SoundFilter bandPass() {
		SoundFilter filter = new SoundFilter();
		filter.setType(Type.BANDPASS);
		return filter;
	}

	public final int id;

	public SoundFilter() {
		id = alGenFilters();
	}

	@Override
	public void close() {
		alDeleteFilters(id);
	}

	public void setType(int value) {
		set(Parameter.FILTER_TYPE, value);
	}

	public int getType() {
		return getInt(Parameter.FILTER_TYPE);
	}

	public void set(int param, int value) {
		alFilteri(id, param, value);
	}

	public void set(int param, int[] values) {
		alFilteriv(id, param, values);
	}

	public void set(int param, IntBuffer values) {
		alFilteriv(id, param, values);
	}

	public void set(int param, float value) {
		alFilterf(id, param, value);
	}

	public void set(int param, float[] values) {
		alFilterfv(id, param, values);
	}

	public void set(int param, FloatBuffer values) {
		alFilterfv(id, param, values);
	}

	public int getInt(int param) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		alGetFilteri(id, param, buffer);
		return buffer.get(0);
	}

	public IntBuffer getIntBuffer(int param, int size) {
		IntBuffer buffer = BufferUtils.createIntBuffer(size);
		alGetFilteriv(id, param, buffer);
		return buffer;
	}

	public int[] getIntArray(int param, int size) {
		return getIntBuffer(param, size).array();
	}

	public float getFloat(int param) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(1);
		alGetFilterf(id, param, buffer);
		return buffer.get(0);
	}

	public FloatBuffer getFloatBuffer(int param, int size) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		alGetFilterfv(id, param, buffer);
		return buffer;
	}

	public float[] getFloatArray(int param, int size) {
		return getFloatBuffer(param, size).array();
	}
}
