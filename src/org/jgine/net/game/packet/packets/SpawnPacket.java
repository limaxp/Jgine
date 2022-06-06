package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketListener;

public class SpawnPacket extends Packet {

	@Override
	public void read(ByteBuffer buffer) {

	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(4);
	}
	
	@Override
	public BiConsumer<PacketListener, ? extends SpawnPacket> getFunction() {
		return PacketListener::on;
	}
}
