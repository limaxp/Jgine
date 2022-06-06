package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketListener;
import org.jgine.net.game.packet.PacketManager;

public class SpawnPacket extends Packet {

	@Override
	public void read(ByteBuffer buffer) {

	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.SPAWN);
	}
	
	@Override
	public BiConsumer<PacketListener, ? extends SpawnPacket> getFunction() {
		return PacketListener::on;
	}
}
