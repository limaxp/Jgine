package org.jgine.core.sound;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.nalBufferData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

public class SoundStream extends SoundBuffer {

	public static final int NUM_BUFFERS = 4;
	public static final int BUFFER_SIZE = 65536;
	public static final int MIN_SIZE = NUM_BUFFERS * BUFFER_SIZE * 8;

	public final Buffer data;

	public static SoundStream from(InputStream is) throws IOException {
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			ShortBuffer pcm = readVorbis(is, 32 * 1024, info);
			int format = info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
			int samplerate = info.sample_rate();
			int sizeInBytes = pcm.remaining() * Short.BYTES;
			return new SoundStream(pcm, format, samplerate, sizeInBytes);
		}
	}

	public SoundStream(Buffer data, int format, int samplerate, int sizeInBytes) {
		super(0, format, samplerate, sizeInBytes);
		this.data = data;
	}

	int[] initBuffer() {
		long dataAddress = MemoryUtil.memAddress(data);
		int[] buffer = new int[NUM_BUFFERS];
		for (int i = 0; i < NUM_BUFFERS; i++)
			nalBufferData(buffer[i] = alGenBuffers(), format, dataAddress + (i * BUFFER_SIZE), BUFFER_SIZE, samplerate);
		return buffer;
	}

	int update(int buffer, int cursor) {
		int dataSize = BUFFER_SIZE;
		ByteBuffer bufferData = BufferUtils.createByteBuffer(dataSize);

		int dataSizeToCopy = BUFFER_SIZE;
		if (cursor + BUFFER_SIZE > sizeInBytes)
			dataSizeToCopy = sizeInBytes - cursor;

		MemoryUtil.memCopy(MemoryUtil.memAddress(data) + cursor, MemoryUtil.memAddress(bufferData), dataSizeToCopy);
		cursor += dataSizeToCopy;

		if (dataSizeToCopy < BUFFER_SIZE) {
			cursor = 0;
			MemoryUtil.memCopy(MemoryUtil.memAddress(data) + cursor, MemoryUtil.memAddress(bufferData) + dataSizeToCopy,
					BUFFER_SIZE - dataSizeToCopy);
			cursor = BUFFER_SIZE - dataSizeToCopy;
		}
		nalBufferData(buffer, format, MemoryUtil.memAddress(bufferData), BUFFER_SIZE, samplerate);
		return cursor;
	}
}
