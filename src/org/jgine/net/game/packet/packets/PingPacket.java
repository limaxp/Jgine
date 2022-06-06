package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class PingPacket extends Packet {

	private long time;

	public PingPacket() {
		time = System.currentTimeMillis();
	}

	@Override
	public void read(ByteBuffer buffer) {
		time = buffer.getLong();
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.PING);
		buffer.putLong(time);
	}

	public long getTime() {
		return time;
	}
}
