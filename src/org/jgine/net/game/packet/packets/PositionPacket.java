package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class PositionPacket extends Packet {

	@Override
	public void read(ByteBuffer buffer) {

	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.POSITION);
	}
}
