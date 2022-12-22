package org.jgine.net.game.packet.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.lwjgl.BufferUtils;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;

public class EntitySpawnPacket extends Packet {

	private int id;
	private Scene scene;
	private float x;
	private float y;
	private float z;
	private ByteBuffer data;

	public EntitySpawnPacket() {
	}

	public EntitySpawnPacket(int id, Scene scene, float x, float y, float z, ByteBuffer data) {
		this.id = id;
		this.scene = scene;
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	@Override
	public void read(ByteBuffer buffer) {
		id = buffer.getInt();
		scene = Engine.getInstance().getScene(buffer.getInt());
		x = buffer.getFloat();
		y = buffer.getFloat();
		z = buffer.getFloat();
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
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(z);
		buffer.putInt(data.remaining());
		buffer.put(data);
	}

	public int getId() {
		return id;
	}

	public Scene getScene() {
		return scene;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public ByteBuffer getData() {
		return data;
	}

	public static EntitySpawnPacket fromEntity(Entity entity) {
		Vector3f pos = entity.transform.getPosition();
		ByteBuffer data = BufferUtils.createByteBuffer(1024);
		DataOutputStream os = new DataOutputStream(new ByteBufferBackedOutputStream(data));
		try {
			entity.save(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		data.flip();
		return new EntitySpawnPacket(entity.id, entity.scene, pos.x, pos.y, pos.z, data);
	}

	public static Entity toEntity(EntitySpawnPacket packet) {
		return toEntity(packet, packet.id);
	}

	public static Entity toEntity(EntitySpawnPacket packet, int id) {
		Entity entity = new Entity(packet.id, packet.scene, packet.x, packet.y, packet.z, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
				1.0f);
		DataInputStream is = new DataInputStream(new ByteBufferBackedInputStream(packet.data));
		try {
			entity.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entity;
	}
}
