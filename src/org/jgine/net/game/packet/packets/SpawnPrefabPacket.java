package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.core.entity.Prefab;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class SpawnPrefabPacket extends Packet {

	private Prefab prefab;

	public SpawnPrefabPacket() {
	}

	public SpawnPrefabPacket(Prefab prefab) {
		this.prefab = prefab;
	}

	@Override
	public void read(ByteBuffer buffer) {
		byte[] nameBytes = new byte[buffer.getInt()];
		buffer.get(nameBytes);
		prefab = Prefab.get(new String(nameBytes));
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.SPAWN_PREFAB);
		buffer.putInt(prefab.name.length());
		buffer.put(prefab.name.getBytes());
	}
}
