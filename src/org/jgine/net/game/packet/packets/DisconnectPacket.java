package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketListener;

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

	@Override
	public BiConsumer<PacketListener, ? extends DisconnectPacket> getFunction() {
		return PacketListener::on;
	}

	public String getName() {
		return name;
	}
}
