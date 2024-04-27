package org.jgine.core.sound;

import java.nio.ByteBuffer;

public class Sound {

	public final int format;
	public final int channelSize;
	public final int sampleRate;
	public final int sampleSize;
	public final int byteRate;
	public final int blockAlign;
	public final ByteBuffer data;

	public Sound(int format, int channelSize, int sampleRate, int sampleSize, int byteRate, int blockAlign, ByteBuffer data) {
		this.format = format;
		this.channelSize = channelSize;
		this.sampleRate = sampleRate;
		this.sampleSize = sampleSize;
		this.byteRate = byteRate;
		this.blockAlign = blockAlign;
		this.data = data;
	}
}
