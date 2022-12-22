package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.core.entity.Entity;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class PositionPacket extends Packet {

	private int id;
	private float x;
	private float y;
	private float z;

	public PositionPacket() {
	}

	public PositionPacket(Entity entity, float x, float y, float z) {
		this(entity.id, x, y, z);
	}

	public PositionPacket(int id, float x, float y, float z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(ByteBuffer buffer) {
		id = buffer.getInt();
		x = buffer.getFloat();
		y = buffer.getFloat();
		z = buffer.getFloat();
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.POSITION);
		buffer.putInt(id);
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(z);
	}

	public int getId() {
		return id;
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
}
