package org.jgine.core.sound;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO8;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO8;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_info;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_samples_short_interleaved;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_open_memory;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_stream_length_in_samples;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.jgine.misc.utils.FileUtils;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class SoundBuffer implements AutoCloseable {

	public final int id;
	public final int format;
	public final int samplerate;
	public final int sizeInBytes;

	public SoundBuffer(int id, int format, int samplerate, int sizeInBytes) {
		this.id = id;
		this.format = format;
		this.samplerate = samplerate;
		this.sizeInBytes = sizeInBytes;
	}

	public SoundBuffer(InputStream is) throws IOException {
		this.id = alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			ShortBuffer pcm = readVorbis(is, 32 * 1024, info);
			format = info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
			samplerate = info.sample_rate();
			sizeInBytes = pcm.remaining() * Short.BYTES;
			alBufferData(id, format, pcm, samplerate);
		}
	}

	public SoundBuffer(ByteBuffer buffer, int format, int samplerate) {
		this.id = alGenBuffers();
		this.format = format;
		this.samplerate = samplerate;
		sizeInBytes = buffer.remaining() * Byte.BYTES;
		alBufferData(id, format, buffer, samplerate);
	}

	@Override
	public void close() {
		alDeleteBuffers(this.id);
	}

	public String getFormatString() {
		return switchFormat("mono16", "mono8", "stereo16", "stereo8", "undefined");
	}

	public int getChannels() {
		return switchFormat(1, 1, 2, 2, 0);
	}

	public int getSampleBits() {
		return switchFormat(16, 8, 16, 8, 0);
	}

	private <T> T switchFormat(T mono16, T mono8, T stereo16, T stereo8, T defaultValue) {
		if (format == AL_FORMAT_MONO16)
			return mono16;
		else if (format == AL_FORMAT_MONO8)
			return mono8;
		else if (format == AL_FORMAT_STEREO16)
			return stereo16;
		else if (format == AL_FORMAT_STEREO8)
			return stereo8;
		return defaultValue;
	}

	public int getSampleSize() {
		return sizeInBytes / (getChannels() * (getSampleBits() / 8));
	}

	public int getDuration() {
		return getSampleSize() / samplerate;
	}

	public static ShortBuffer readVorbis(InputStream is, int bufferSize, STBVorbisInfo info) throws IOException {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer vorbis = FileUtils.readByteBuffer(is);
			IntBuffer error = stack.mallocInt(1);
			long decoder = stb_vorbis_open_memory(vorbis, error, null);
			if (decoder == NULL) {
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}

			stb_vorbis_get_info(decoder, info);
			int channels = info.channels();
			int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
			ShortBuffer pcm = MemoryUtil.memAllocShort(lengthSamples);
			pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
			stb_vorbis_close(decoder);
			return pcm;
		}
	}
}
