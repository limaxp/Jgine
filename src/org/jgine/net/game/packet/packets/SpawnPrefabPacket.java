package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Prefab;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class SpawnPrefabPacket extends Packet {

	private Prefab prefab;
	private Scene scene;
	private float x;
	private float y;
	private float z;

	public SpawnPrefabPacket() {
	}

	public SpawnPrefabPacket(Prefab prefab, Scene scene, float x, float y, float z) {
		this.prefab = prefab;
		this.scene = scene;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void read(ByteBuffer buffer) {
		prefab = Prefab.get(buffer.getInt());
		scene = Engine.getInstance().getScene(buffer.getInt());
		x = buffer.getFloat();
		y = buffer.getFloat();
		z = buffer.getFloat();
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.SPAWN_PREFAB);
		buffer.putInt(prefab.id);
		buffer.putInt(scene.id);
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(z);
	}

	public Prefab getPrefab() {
		return prefab;
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
}
