package org.jgine.core.sound;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_MAX_DISTANCE;
import static org.lwjgl.openal.AL10.AL_PAUSED;
import static org.lwjgl.openal.AL10.AL_PITCH;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_REFERENCE_DISTANCE;
import static org.lwjgl.openal.AL10.AL_ROLLOFF_FACTOR;
import static org.lwjgl.openal.AL10.AL_SOURCE_RELATIVE;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSource3f;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcefv;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alGetSourceiv;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcefv;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.AL11.alSource3i;
import static org.lwjgl.openal.AL11.alSourceiv;
import static org.lwjgl.openal.EXTEfx.AL_AUXILIARY_SEND_FILTER;
import static org.lwjgl.openal.EXTEfx.AL_DIRECT_FILTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECTSLOT_NULL;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_NULL;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_NULL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.sound.effect.SoundEffect;
import org.jgine.core.sound.effect.SoundFilter;
import org.jgine.utils.collection.list.PersistentArrayList;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.options.Options;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

/**
 * An openAL sound source.
 * <p>
 * See Also: <a href=
 * "https://www.openal.org/documentation/">https://www.openal.org/documentation/</a>
 */
public class SoundSource {

	public static final float DEFAULT_ROLLOFF = 1f;
	public static final float DEFAULT_REFERENCE_DISTANCE = 40f;
	public static final float DEFAULT_MAX_DISTANCE = 200f;

	public final int id;
	private SoundBuffer buffer;
	private SoundFilter directFilter;
	private final List<EffectEntry> effects;
	private int cursor;

	SoundSource(SoundBuffer buffer, boolean loop, boolean sourceRelative) {
		this.id = alGenSources();
		effects = new PersistentArrayList<EffectEntry>();
		this.buffer = buffer;
		set(AL_BUFFER, buffer.id);
		if (loop)
			set(AL_LOOPING, AL_TRUE);
		if (sourceRelative)
			set(AL_SOURCE_RELATIVE, AL_TRUE);
		setRolloff(DEFAULT_ROLLOFF);
		setReferenceDistance(DEFAULT_REFERENCE_DISTANCE);
		setMaxDistance(DEFAULT_MAX_DISTANCE);
	}

	void close() {
		alDeleteSources(id);
		buffer = null;
		directFilter = null;
	}

	public SoundBuffer getBuffer() {
		return buffer;
	}

	public SoundStream getStream() {
		return (SoundStream) buffer;
	}

	public int getBufferId() {
		return getInt(AL_BUFFER);
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

	public void setGain(float gain) {
		if (gain > 1.0f)
			gain = 1.0f;
		if (gain < 0.0f)
			gain = 0.0f;
		set(AL_GAIN, gain);
	}

	public float getGain() {
		return getFloat(AL_GAIN);
	}

	public void setPitch(float pitch) {
		set(AL_PITCH, pitch);
	}

	public float getPitch() {
		return getFloat(AL_PITCH);
	}

	public void setRolloff(float rolloff) {
		set(AL_ROLLOFF_FACTOR, rolloff);
	}

	public float getRolloff() {
		return getFloat(AL_ROLLOFF_FACTOR);
	}

	public void setReferenceDistance(float referenceDistance) {
		set(AL_REFERENCE_DISTANCE, referenceDistance);
	}

	public float getReferenceDistance() {
		return getFloat(AL_REFERENCE_DISTANCE);
	}

	public void setMaxDistance(float maxDistance) {
		set(AL_MAX_DISTANCE, maxDistance);
	}

	public float getMaxDistance() {
		return getFloat(AL_MAX_DISTANCE);
	}

	void play() {
		if (!isPlaying() && SoundManager.SOURCES.size() < Options.MAX_SOUNDS.getInt()) {
			if (isStream()) {
				cursor = SoundStream.NUM_BUFFERS * SoundStream.BUFFER_SIZE;
				alSourceQueueBuffers(id, getStream().initBuffer());
			}
			alSourcePlay(id);
		}
	}

	public boolean isStream() {
		return buffer instanceof SoundStream;
	}

	void updateStream() {
		int buffersProcessed = getInt(AL_BUFFERS_PROCESSED);
		if (buffersProcessed <= 0)
			return;
		SoundStream stream = getStream();
		while (buffersProcessed-- > 0) {
			int buffer = alSourceUnqueueBuffers(id);
			cursor = stream.update(buffer, cursor);
			alSourceQueueBuffers(id, buffer);
		}
	}

	public boolean isPlaying() {
		return getInt(AL_SOURCE_STATE) == AL_PLAYING;
	}

	public boolean isStopped() {
		return getInt(AL_SOURCE_STATE) == AL_STOPPED;
	}

	public boolean isPaused() {
		return getInt(AL_SOURCE_STATE) == AL_PAUSED;
	}

	public void pause() {
		alSourcePause(id);
	}

	public void stop() {
		alSourceStop(id);
	}

	public void setLooping(boolean looping) {
		if (looping)
			set(AL_LOOPING, AL_TRUE);
		else
			set(AL_LOOPING, AL_FALSE);
	}

	public boolean isLooping() {
		return getInt(AL_LOOPING) == AL_TRUE;
	}

	public void setRelative(boolean relative) {
		if (relative)
			set(AL_SOURCE_RELATIVE, AL_TRUE);
		else
			set(AL_SOURCE_RELATIVE, AL_FALSE);
	}

	public boolean isRelative() {
		return getInt(AL_LOOPING) == AL_TRUE;
	}

	public void set(int param, int value) {
		alSourcei(id, param, value);
	}

	public void set(int param, int v1, int v2, int v3) {
		alSource3i(id, param, v1, v2, v3);
	}

	public void set(int param, IntBuffer values) {
		alSourceiv(id, param, values);
	}

	public void set(int param, int[] values) {
		alSourceiv(id, param, values);
	}

	public void set(int param, float value) {
		alSourcef(id, param, value);
	}

	public void set(int param, float v1, float v2, float v3) {
		alSource3f(id, param, v1, v2, v3);
	}

	public void set(int param, Vector3f vec) {
		alSource3f(id, param, vec.x, vec.y, vec.z);
	}

	public void set(int param, FloatBuffer values) {
		alSourcefv(id, param, values);
	}

	public void set(int param, float[] values) {
		alSourcefv(id, param, values);
	}

	public int getInt(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buffer = stack.mallocInt(1);
			alGetSourcei(id, param, buffer);
			return buffer.get(0);
		}
	}

	public int[] get3Int(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buffer = stack.mallocInt(3);
			alGetSourceiv(id, param, buffer);
			return new int[] { buffer.get(0), buffer.get(1), buffer.get(2) };
		}
	}

	public IntBuffer getIntBuffer(int param, int size) {
		IntBuffer buffer = BufferUtils.createIntBuffer(size);
		alGetSourceiv(id, param, buffer);
		return buffer;
	}

	public int[] getIntArray(int param, int size) {
		return getIntBuffer(param, size).array();
	}

	public float getFloat(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(1);
			alGetSourcef(id, param, buffer);
			return buffer.get(0);
		}
	}

	public Vector3f get3Float(int param) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer bufferX = stack.mallocFloat(1);
			FloatBuffer bufferY = stack.mallocFloat(1);
			FloatBuffer bufferZ = stack.mallocFloat(1);
			alGetSource3f(id, param, bufferX, bufferY, bufferZ);
			return new Vector3f(bufferX.get(0), bufferY.get(0), bufferZ.get(0));
		}
	}

	public FloatBuffer getFloatBuffer(int param, int size) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		alGetSourcefv(id, param, buffer);
		return buffer;
	}

	public float[] getFloatArray(int param, int size) {
		return getFloatBuffer(param, size).array();
	}

	public void setDirectFilter(@Nullable SoundFilter filter) {
		if (filter == null)
			set(AL_DIRECT_FILTER, AL_FILTER_NULL);
		else
			set(AL_DIRECT_FILTER, filter.id);
		directFilter = filter;
	}

	@Nullable
	public SoundFilter getDirectFilter() {
		return directFilter;
	}

	public void addEffect(SoundEffect effect) {
		set(AL_AUXILIARY_SEND_FILTER, effect.getSlot(), effects.size(), AL_FILTER_NULL);
		effects.add(new EffectEntry(effect, null));
	}

	public void addEffect(SoundFilter filter) {
		set(AL_AUXILIARY_SEND_FILTER, AL_EFFECT_NULL, effects.size(), filter.id);
		effects.add(new EffectEntry(null, filter));
	}

	public void addEffect(SoundEffect effect, SoundFilter filter) {
		set(AL_AUXILIARY_SEND_FILTER, effect.getSlot(), effects.size(), filter.id);
		effects.add(new EffectEntry(effect, filter));
	}

	@Nullable
	public EffectEntry removeEffect(SoundEffect effect) {
		EffectEntry result = null;
		int index = 0;
		for (int i = 0; i < effects.size(); i++) {
			EffectEntry entry = effects.get(i);
			if (entry.effect == effect) {
				result = entry;
				index = i;
			}
		}
		if (result != null) {
			if (result.filter != null) {
				set(AL_AUXILIARY_SEND_FILTER, AL_EFFECTSLOT_NULL, index, result.filter.id);
				result.effect = null;
			} else {
				set(AL_AUXILIARY_SEND_FILTER, AL_EFFECTSLOT_NULL, index, AL_FILTER_NULL);
				effects.remove(index);
			}
		}
		return result;
	}

	@Nullable
	public EffectEntry removeEffect(SoundFilter filter) {
		EffectEntry result = null;
		int index = 0;
		for (int i = 0; i < effects.size(); i++) {
			EffectEntry entry = effects.get(i);
			if (entry.filter == filter) {
				result = entry;
				index = i;
			}
		}
		if (result != null) {
			if (result.effect != null) {
				set(AL_AUXILIARY_SEND_FILTER, result.effect.id, index, AL_FILTER_NULL);
				result.filter = null;
			} else {
				set(AL_AUXILIARY_SEND_FILTER, AL_EFFECTSLOT_NULL, index, AL_FILTER_NULL);
				effects.remove(index);
			}
		}
		return result;
	}

	public void clearEffects() {
		for (int i = 0; i < effects.size(); i++)
			set(AL_AUXILIARY_SEND_FILTER, AL_EFFECTSLOT_NULL, i, AL_FILTER_NULL);
		effects.clear();
	}

	public List<EffectEntry> getEntries() {
		return Collections.unmodifiableList(effects);
	}

	public List<SoundEffect> getEffects() {
		List<SoundEffect> result = new ArrayList<SoundEffect>();
		for (int i = 0; i < effects.size(); i++) {
			EffectEntry entry = effects.get(i);
			if (entry.effect != null)
				result.add(entry.effect);
		}
		return result;
	}

	public List<SoundFilter> getFilter() {
		List<SoundFilter> result = new ArrayList<SoundFilter>();
		for (int i = 0; i < effects.size(); i++) {
			EffectEntry entry = effects.get(i);
			if (entry.effect != null)
				result.add(entry.filter);
		}
		return result;
	}

	private static class EffectEntry {

		private SoundEffect effect;
		private SoundFilter filter;

		private EffectEntry(@Nullable SoundEffect effect, @Nullable SoundFilter filter) {
			this.effect = effect;
			this.filter = filter;
		}
	}
}