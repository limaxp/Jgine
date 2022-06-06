package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.Packet;

public class ConnectPacket extends Packet {

	private String name;

	public ConnectPacket() {
	}

	public ConnectPacket(String name) {
		this.name = name;
	}

	@Override
	public void read(ByteBuffer buffer) {
		byte[] nameBytes = new byte[buffer.getInt()];
		buffer.get(nameBytes);
		name = new String(nameBytes);
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(1);
		buffer.putInt(name.length());
		buffer.put(name.getBytes());
	}
	
	public String getName() {
		return name;
	}
}
