package org.jgine.net.game.packet.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;

public class SpawnEntityPacket extends Packet {

	private Entity entity;

	public SpawnEntityPacket() {
	}

	public SpawnEntityPacket(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void read(ByteBuffer buffer) {
		Scene scene = Engine.getInstance().getScene(buffer.getInt());
		entity = new Entity(scene);
		DataInputStream is = new DataInputStream(new ByteBufferBackedInputStream(buffer));
		try {
			entity.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.SPAWN_ENTITY);
		buffer.putInt(entity.scene.id);
		DataOutputStream os = new DataOutputStream(new ByteBufferBackedOutputStream(buffer));
		try {
			entity.save(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Entity getEntity() {
		return entity;
	}
}
