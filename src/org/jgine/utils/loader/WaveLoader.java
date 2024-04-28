package org.jgine.utils.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.jgine.core.sound.Sound;
import org.lwjgl.openal.AL10;

import maxLibs.utils.logger.Logger;

public class WaveLoader {

	private static final IntBuffer RIFF = IntBuffer.wrap(new int[] { 0x52, 0x49, 0x46, 0x46 });
	private static final IntBuffer WAVE = IntBuffer.wrap(new int[] { 0x57, 0x41, 0x56, 0x45 });
	private static final IntBuffer FMT_ = IntBuffer.wrap(new int[] { 0x66, 0x6D, 0x74, 0x20 });
	private static final IntBuffer DATA = IntBuffer.wrap(new int[] { 0x64, 0x61, 0x74, 0x61 });

	public static Sound load(InputStream dir) {
		int sample_rate = 0;
		int number_of_channels = 0;
		int byte_rate = 0;
		int block_align = 0;
		int sample_size = 0;
		int format;

		try {
			int fileLength = dir.available();
			// Read in the first 4 bytes. These bytes should spell RIFF.
			byte[] buffer = new byte[4];
			dir.read(buffer);

			// Unsign the bytes and put it into a IntBuffer for comparison to the IntBuffer
			// RIFF
			IntBuffer is_riff = IntBuffer.wrap(unsign_byte(buffer));

			if (RIFF.compareTo(is_riff) != 0) {
				Logger.warn("WaveLoader: Not a riff!");
				return null;
			}

			// The next 4 bytes describe the total filesize minus 8.
			dir.read(buffer);

			int length = turn_byte_buffer_into_int(buffer);

			if (length + 8 != fileLength) {
				Logger.warn("WaveLoader: Incorrect file size!");
				return null;
			}

			// The next 4 bytes should read WAVE
			dir.read(buffer);
			IntBuffer is_wave = IntBuffer.wrap(unsign_byte(buffer));

			if (WAVE.compareTo(is_wave) != 0) {
				Logger.warn("WaveLoader: Not a wave!");
				return null;
			}

			// As the format is WAVE, the next 4 bytes should read "fmt ". Includes the
			// space.
			dir.read(buffer);
			IntBuffer is_fmt = IntBuffer.wrap(unsign_byte(buffer));

			if (FMT_.compareTo(is_fmt) != 0) {
				Logger.warn("WaveLoader: Does not have fmt!");
				return null;
			}

			// Next we read the subchunk size. As we are reading an uncompressed .wav, it
			// should be 16.
			dir.read(buffer);

			length = turn_byte_buffer_into_int(buffer);

			if (length != 16) {
				Logger.warn("WaveLoader: Subchunk size incorrect!");
				return null;
			}

			clear(buffer);

			// Next we read in the AudioFormat. This must equal 1 for PCM
			dir.read(buffer, 0, 2);

			length = turn_byte_buffer_into_int(buffer);

			if (length != 1) {
				Logger.warn("WaveLoader: Compression detected!");
				return null;
			}

			// Now we need to read in the number of channels
			clear(buffer);
			dir.read(buffer, 0, 2);
			number_of_channels = turn_byte_buffer_into_int(buffer);

			// Next is the sample rate;
			dir.read(buffer);

			sample_rate = turn_byte_buffer_into_int(buffer);

			// Now the byte rate
			dir.read(buffer);
			byte_rate = turn_byte_buffer_into_int(buffer);

			// The block align size (Size of one sample with all channels.
			clear(buffer);
			dir.read(buffer, 0, 2);
			block_align = turn_byte_buffer_into_int(buffer);

			// Read in the bits per sample;
			clear(buffer);
			dir.read(buffer, 0, 2);
			sample_size = turn_byte_buffer_into_int(buffer);

			// The next thing should be the value data.
			dir.read(buffer);
			IntBuffer is_data = IntBuffer.wrap(unsign_byte(buffer));

			if (DATA.compareTo(is_data) != 0) {
				Logger.warn("WaveLoader: Data not found!");
				return null;
			}

			// remainder filesize. Don't care about it.
			dir.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (sample_size == 8) {
			if (number_of_channels == 1) {
				format = AL10.AL_FORMAT_MONO8;
			} else if (number_of_channels == 2) {
				format = AL10.AL_FORMAT_STEREO8;
			} else {
				Logger.warn("WaveLoader: Unsupported channel number!");
				return null;
			}
		} else if (sample_size == 16) {
			if (number_of_channels == 1) {
				format = AL10.AL_FORMAT_MONO16;
			} else if (number_of_channels == 2) {
				format = AL10.AL_FORMAT_STEREO16;
			} else {
				Logger.warn("WaveLoader: Unsupported channel number!");
				return null;
			}
		} else {
			Logger.warn("WaveLoader: Unsupported sample size!");
			return null;
		}

		return new Sound(format, number_of_channels, sample_rate, sample_size, byte_rate, block_align,
				read(dir, ByteBuffer.allocateDirect(4096)));
	}

	public static ByteBuffer read(InputStream dir, ByteBuffer buffer) {
		buffer.clear();
		byte[] buf = new byte[buffer.capacity()];
		try {
			dir.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.put(buf);
		return buffer;
	}

	private static int turn_byte_buffer_into_int(byte[] buf) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(buf);
		bb.rewind();
		return bb.getInt();
	}

	private static void clear(byte[] buffer) {
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = 0;
	}

	private static int[] unsign_byte(byte[] b) {
		int[] rv = new int[b.length];
		for (int i = 0; i < rv.length; i++)
			rv[i] = 0xFF & b[i];
		return rv;
	}
}