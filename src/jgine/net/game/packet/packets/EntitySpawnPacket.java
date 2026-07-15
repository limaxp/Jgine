package jgine.net.game.packet.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.net.game.packet.Packet;
import jgine.net.game.packet.PacketManager;

public class EntitySpawnPacket extends Packet {

	private int id;
	private Scene scene;
	private ByteBuffer data;

	public EntitySpawnPacket() {
	}

	public EntitySpawnPacket(int id, Scene scene, ByteBuffer data) {
		this.id = id;
		this.scene = scene;
		this.data = data;
	}

	@Override
	public void read(ByteBuffer buffer) {
		id = buffer.getInt();
		scene = Scene.get(buffer.getInt());
		int dataSize = buffer.getInt();
		data = BufferUtils.createByteBuffer(dataSize);
		for (int i = 0; i < dataSize; i++)
			data.put(buffer.get());
		data.flip();
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.ENTITY_SPAWN);
		buffer.putInt(id);
		buffer.putInt(scene.id);
		buffer.putInt(data.remaining());
		buffer.put(data);
	}

	public int getId() {
		return id;
	}

	public Scene getScene() {
		return scene;
	}

	public ByteBuffer getData() {
		return data;
	}

	public static EntitySpawnPacket fromEntity(Entity entity) {
		ByteBuffer data = BufferUtils.createByteBuffer(1024);
		DataOutputStream os = new DataOutputStream(new ByteBufferBackedOutputStream(data));
		try {
			entity.save(os);
			// TODO save all systems
		} catch (IOException e) {
			e.printStackTrace();
		}
		data.flip();
		return new EntitySpawnPacket(entity.id, entity.scene, data);
	}

	public static Entity toEntity(EntitySpawnPacket packet) {
		return toEntity(packet, packet.id);
	}

	public static Entity toEntity(EntitySpawnPacket packet, int id) {
		Entity entity = new Entity(packet.id, packet.scene);
		DataInputStream is = new DataInputStream(new ByteBufferBackedInputStream(packet.data));
		try {
			entity.load(is);
			// TODO load all systems
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entity;
	}
}
