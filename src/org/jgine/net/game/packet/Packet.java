package org.jgine.net.game.packet;

import java.nio.ByteBuffer;

public abstract class Packet {

	public abstract void read(ByteBuffer buffer);

	public abstract void write(ByteBuffer buffer);
}
