package org.jgine.net.packet;

import java.nio.ByteBuffer;

import org.jgine.net.Packet;

public class DisconnectPacket extends Packet {

	private String name;

	public DisconnectPacket() {
	}

	public DisconnectPacket(String name) {
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
		buffer.putInt(2);
		buffer.putInt(name.length());
		buffer.put(name.getBytes());
	}

	public String getName() {
		return name;
	}
}
