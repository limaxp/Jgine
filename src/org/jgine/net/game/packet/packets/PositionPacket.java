package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketListener;

public class PositionPacket extends Packet {

	@Override
	public void read(ByteBuffer buffer) {

	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(3);
	}
	
	@Override
	public BiConsumer<PacketListener, ? extends PositionPacket> getFunction() {
		return PacketListener::on;
	}
}
