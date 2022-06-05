package org.jgine.net;

import java.nio.ByteBuffer;

public abstract class Packet {

	public abstract void read(ByteBuffer buffer);

	public abstract void write(ByteBuffer buffer);
}
