package org.jgine.net.game.packet;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

public abstract class Packet {

	public abstract void read(ByteBuffer buffer);

	public abstract void write(ByteBuffer buffer);

	public abstract BiConsumer<PacketListener, ? extends Packet> getFunction();
}
