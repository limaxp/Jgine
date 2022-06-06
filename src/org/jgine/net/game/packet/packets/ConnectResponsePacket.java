package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class ConnectResponsePacket extends Packet {

	private boolean accepted;
	private int id;

	public ConnectResponsePacket() {
	}

	public ConnectResponsePacket(boolean accepted, int id) {
		this.accepted = accepted;
		this.id = id;
	}

	@Override
	public void read(ByteBuffer buffer) {
		accepted = buffer.get() != 0;
		id = buffer.getInt();
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.CONNECT_RESPONSE);
		buffer.put(accepted ? (byte) 1 : (byte) 0);
		buffer.putInt(id);
	}

	public boolean isAccepted() {
		return accepted;
	}

	public int getId() {
		return id;
	}
}
