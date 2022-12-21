package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class DisconnectPacket extends Packet {

	private int id;

	public DisconnectPacket() {
	}

	public DisconnectPacket(int id) {
		this.id = id;
	}

	@Override
	public void read(ByteBuffer buffer) {
		id = buffer.getInt();
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.DISCONNECT);
		buffer.putInt(id);
	}

	public int getId() {
		return id;
	}
}
