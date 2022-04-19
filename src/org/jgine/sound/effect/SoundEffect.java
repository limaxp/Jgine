package org.jgine.sound.effect;

import static org.lwjgl.openal.EXTEfx.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class SoundEffect implements AutoCloseable {

	public static class Type {

		public static final int NULL = AL_EFFECT_NULL;
		public static final int AUTOWAH = AL_EFFECT_AUTOWAH;
		public static final int CHORUS = AL_EFFECT_CHORUS;
		public static final int COMPRESSOR = AL_EFFECT_COMPRESSOR;
		public static final int DISTORTION = AL_EFFECT_DISTORTION;
		public static final int EAXREVERB = AL_EFFECT_EAXREVERB;
		public static final int ECHO = AL_EFFECT_ECHO;
		public static final int EQUALIZER = AL_EFFECT_EQUALIZER;
		public static final int FLANGER = AL_EFFECT_FLANGER;
		public static final int FREQUENCY_SHIFTER = AL_EFFECT_FREQUENCY_SHIFTER;
		public static final int PITCH_SHIFTER = AL_EFFECT_PITCH_SHIFTER;
		public static final int REVERB = AL_EFFECT_REVERB;
		public static final int RING_MODULATOR = AL_EFFECT_RING_MODULATOR;
		public static final int VOCAL_MORPHER = AL_EFFECT_VOCAL_MORPHER;

	}

	public static class Parameter {

		public static final int EFFECT_TYPE = AL_EFFECT_TYPE;
		public static final int EFFECT_FIRST_PARAMETER = AL_EFFECT_FIRST_PARAMETER;
		public static final int EFFECT_LAST_PARAMETER = AL_EFFECT_LAST_PARAMETER;

		public static final int AUTOWAH_ATTACK_TIME = AL_AUTOWAH_ATTACK_TIME;
		public static final int AUTOWAH_PEAK_GAIN = AL_AUTOWAH_PEAK_GAIN;
		public static final int AUTOWAH_RELEASE_TIME = AL_AUTOWAH_RELEASE_TIME;
		public static final int AUTOWAH_RESONANCE = AL_AUTOWAH_RESONANCE;

		public static final int CHORUS_DELAY = AL_CHORUS_DELAY;
		public static final int CHORUS_DEPTH = AL_CHORUS_DEPTH;
		public static final int CHORUS_FEEDBACK = AL_CHORUS_FEEDBACK;
		public static final int CHORUS_PHASE = AL_CHORUS_PHASE;
		public static final int CHORUS_RATE = AL_CHORUS_RATE;
		public static final int CHORUS_WAVEFORM = AL_CHORUS_WAVEFORM;

		public static final int COMPRESSOR_ONOFF = AL_COMPRESSOR_ONOFF;

		public static final int DISTORTION_EDGE = AL_DISTORTION_EDGE;
		public static final int DISTORTION_EQBANDWIDTH = AL_DISTORTION_EQBANDWIDTH;
		public static final int DISTORTION_EQCENTER = AL_DISTORTION_EQCENTER;
		public static final int DISTORTION_GAIN = AL_DISTORTION_GAIN;
		public static final int DISTORTION_LOWPASS_CUTOFF = AL_DISTORTION_LOWPASS_CUTOFF;

		public static final int EAXREVERB_AIR_ABSORPTION_GAINHF = AL_EAXREVERB_AIR_ABSORPTION_GAINHF;
		public static final int EAXREVERB_DECAY_HFLIMIT = AL_EAXREVERB_DECAY_HFLIMIT;
		public static final int EAXREVERB_DECAY_HFRATIO = AL_EAXREVERB_DECAY_HFRATIO;
		public static final int EAXREVERB_DECAY_LFRATIO = AL_EAXREVERB_DECAY_LFRATIO;
		public static final int EAXREVERB_DECAY_TIME = AL_EAXREVERB_DECAY_TIME;
		public static final int EAXREVERB_DENSITY = AL_EAXREVERB_DENSITY;
		public static final int EAXREVERB_DIFFUSION = AL_EAXREVERB_DIFFUSION;
		public static final int EAXREVERB_ECHO_DEPTH = AL_EAXREVERB_ECHO_DEPTH;
		public static final int EAXREVERB_ECHO_TIME = AL_EAXREVERB_ECHO_TIME;
		public static final int EAXREVERB_GAIN = AL_EAXREVERB_GAIN;
		public static final int EAXREVERB_GAINHF = AL_EAXREVERB_GAINHF;
		public static final int EAXREVERB_GAINLF = AL_EAXREVERB_GAINLF;
		public static final int EAXREVERB_HFREFERENCE = AL_EAXREVERB_HFREFERENCE;
		public static final int EAXREVERB_LATE_REVERB_DELAY = AL_EAXREVERB_LATE_REVERB_DELAY;
		public static final int EAXREVERB_LATE_REVERB_GAIN = AL_EAXREVERB_LATE_REVERB_GAIN;
		public static final int EAXREVERB_LATE_REVERB_PAN = AL_EAXREVERB_LATE_REVERB_PAN;
		public static final int EAXREVERB_LFREFERENCE = AL_EAXREVERB_LFREFERENCE;
		public static final int EAXREVERB_MODULATION_DEPTH = AL_EAXREVERB_MODULATION_DEPTH;
		public static final int EAXREVERB_MODULATION_TIME = AL_EAXREVERB_MODULATION_TIME;
		public static final int EAXREVERB_REFLECTIONS_DELAY = AL_EAXREVERB_REFLECTIONS_DELAY;
		public static final int EAXREVERB_REFLECTIONS_GAIN = AL_EAXREVERB_REFLECTIONS_GAIN;
		public static final int EAXREVERB_REFLECTIONS_PAN = AL_EAXREVERB_REFLECTIONS_PAN;
		public static final int EAXREVERB_ROOM_ROLLOFF_FACTOR = AL_EAXREVERB_ROOM_ROLLOFF_FACTOR;

		public static final int ECHO_DAMPING = AL_ECHO_DAMPING;
		public static final int ECHO_DELAY = AL_ECHO_DELAY;
		public static final int ECHO_FEEDBACK = AL_ECHO_FEEDBACK;
		public static final int ECHO_LRDELAY = AL_ECHO_LRDELAY;
		public static final int ECHO_SPREAD = AL_ECHO_SPREAD;

		public static final int EQUALIZER_HIGH_CUTOFF = AL_EQUALIZER_HIGH_CUTOFF;
		public static final int EQUALIZER_HIGH_GAIN = AL_EQUALIZER_HIGH_GAIN;
		public static final int EQUALIZER_LOW_CUTOFF = AL_EQUALIZER_LOW_CUTOFF;
		public static final int EQUALIZER_LOW_GAIN = AL_EQUALIZER_LOW_GAIN;
		public static final int EQUALIZER_MID1_CENTER = AL_EQUALIZER_MID1_CENTER;
		public static final int EQUALIZER_MID1_GAIN = AL_EQUALIZER_MID1_GAIN;
		public static final int EQUALIZER_MID1_WIDTH = AL_EQUALIZER_MID1_WIDTH;
		public static final int EQUALIZER_MID2_CENTER = AL_EQUALIZER_MID2_CENTER;
		public static final int EQUALIZER_MID2_GAIN = AL_EQUALIZER_MID2_GAIN;
		public static final int EQUALIZER_MID2_WIDTH = AL_EQUALIZER_MID2_WIDTH;

		public static final int FLANGER_DELAY = AL_FLANGER_DELAY;
		public static final int FLANGER_DEPTH = AL_FLANGER_DEPTH;
		public static final int FLANGER_FEEDBACK = AL_FLANGER_FEEDBACK;
		public static final int FLANGER_PHASE = AL_FLANGER_PHASE;
		public static final int FLANGER_RATE = AL_FLANGER_RATE;
		public static final int FLANGER_WAVEFORM = AL_FLANGER_WAVEFORM;

		public static final int FREQUENCY_SHIFTER_FREQUENCY = AL_FREQUENCY_SHIFTER_FREQUENCY;
		public static final int FREQUENCY_SHIFTER_LEFT_DIRECTION = AL_FREQUENCY_SHIFTER_LEFT_DIRECTION;
		public static final int FREQUENCY_SHIFTER_RIGHT_DIRECTION = AL_FREQUENCY_SHIFTER_RIGHT_DIRECTION;

		public static final int PITCH_SHIFTER_COARSE_TUNE = AL_PITCH_SHIFTER_COARSE_TUNE;
		public static final int PITCH_SHIFTER_FINE_TUNE = AL_PITCH_SHIFTER_FINE_TUNE;

		public static final int REVERB_AIR_ABSORPTION_GAINHF = AL_REVERB_AIR_ABSORPTION_GAINHF;
		public static final int REVERB_DECAY_HFLIMIT = AL_REVERB_DECAY_HFLIMIT;
		public static final int REVERB_DECAY_HFRATIO = AL_REVERB_DECAY_HFRATIO;
		public static final int REVERB_DECAY_TIME = AL_REVERB_DECAY_TIME;
		public static final int REVERB_DENSITY = AL_REVERB_DENSITY;
		public static final int REVERB_DIFFUSION = AL_REVERB_DIFFUSION;
		public static final int REVERB_GAIN = AL_REVERB_GAIN;
		public static final int REVERB_GAINHF = AL_REVERB_GAINHF;
		public static final int REVERB_LATE_REVERB_DELAY = AL_REVERB_LATE_REVERB_DELAY;
		public static final int REVERB_LATE_REVERB_GAIN = AL_REVERB_LATE_REVERB_GAIN;
		public static final int REVERB_REFLECTIONS_DELAY = AL_REVERB_REFLECTIONS_DELAY;
		public static final int REVERB_REFLECTIONS_GAIN = AL_REVERB_REFLECTIONS_GAIN;
		public static final int REVERB_ROOM_ROLLOFF_FACTOR = AL_REVERB_ROOM_ROLLOFF_FACTOR;

		public static final int RING_MODULATOR_FREQUENCY = AL_RING_MODULATOR_FREQUENCY;
		public static final int RING_MODULATOR_HIGHPASS_CUTOFF = AL_RING_MODULATOR_HIGHPASS_CUTOFF;
		public static final int RING_MODULATOR_WAVEFORM = AL_RING_MODULATOR_WAVEFORM;

		public static final int VOCMORPHER_PHONEMEA = AL_VOCMORPHER_PHONEMEA;
		public static final int VOCMORPHER_PHONEMEA_COARSE_TUNING = AL_VOCMORPHER_PHONEMEA_COARSE_TUNING;
		public static final int VOCMORPHER_PHONEMEB = AL_VOCMORPHER_PHONEMEB;
		public static final int VOCMORPHER_PHONEMEB_COARSE_TUNING = AL_VOCMORPHER_PHONEMEB_COARSE_TUNING;
		public static final int VOCMORPHER_RATE = AL_VOCMORPHER_RATE;
		public static final int VOCMORPHER_WAVEFORM = AL_VOCMORPHER_WAVEFORM;
	}

	public final int id;
	private int slotId;

	public SoundEffect() {
		id = alGenEffects();
	}

	@Override
	public void close() {
		alDeleteEffects(id);
		if (slotId != 0) {
			alAuxiliaryEffectSloti(slotId, AL_EFFECTSLOT_EFFECT, AL_EFFECT_NULL);
			alDeleteAuxiliaryEffectSlots(slotId);
		}
	}

	public int getSlot() {
		if (slotId == 0)
			alAuxiliaryEffectSloti(slotId = alGenAuxiliaryEffectSlots(), AL_EFFECTSLOT_EFFECT, id);
		return slotId;
	}

	public void setType(int value) {
		set(Parameter.EFFECT_TYPE, value);
	}

	public int getType() {
		return getInt(Parameter.EFFECT_TYPE);
	}

	public void set(int param, int value) {
		alEffecti(id, param, value);
	}

	public void set(int param, int[] values) {
		alEffectiv(id, param, values);
	}

	public void set(int param, IntBuffer values) {
		alEffectiv(id, param, values);
	}

	public void set(int param, float value) {
		alEffectf(id, param, value);
	}

	public void set(int param, float[] values) {
		alEffectfv(id, param, values);
	}

	public void set(int param, FloatBuffer values) {
		alEffectfv(id, param, values);
	}

	public int getInt(int param) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		alGetEffecti(id, param, buffer);
		return buffer.get(0);
	}

	public IntBuffer getIntBuffer(int param, int size) {
		IntBuffer buffer = BufferUtils.createIntBuffer(size);
		alGetEffectiv(id, param, buffer);
		return buffer;
	}

	public int[] getIntArray(int param, int size) {
		return getIntBuffer(param, size).array();
	}

	public float getFloat(int param) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(1);
		alGetEffectf(id, param, buffer);
		return buffer.get(0);
	}

	public FloatBuffer getFloatBuffer(int param, int size) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		alGetEffectfv(id, param, buffer);
		return buffer;
	}

	public float[] getFloatArray(int param, int size) {
		return getFloatBuffer(param, size).array();
	}
}
