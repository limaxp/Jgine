package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.Packet;

public class SpawnPacket extends Packet {

	@Override
	public void read(ByteBuffer buffer) {

	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(4);
	}
}
