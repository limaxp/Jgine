package jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import jgine.core.Prefab;
import jgine.core.Scene;
import jgine.net.game.packet.Packet;
import jgine.net.game.packet.PacketManager;

public class PrefabSpawnPacket extends Packet {

	private int id;
	private Prefab prefab;
	private Scene scene;

	public PrefabSpawnPacket() {
	}

	public PrefabSpawnPacket(int id, Prefab prefab, Scene scene) {
		this.id = id;
		this.prefab = prefab;
		this.scene = scene;
	}

	@Override
	public void read(ByteBuffer buffer) {
		id = buffer.getInt();
		prefab = Prefab.get(buffer.getInt());
		scene = Scene.get(buffer.getInt());
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.PREFAB_SPAWN);
		buffer.putInt(id);
		buffer.putInt(prefab.id);
		buffer.putInt(scene.id);
	}

	public int getId() {
		return id;
	}

	public Prefab getPrefab() {
		return prefab;
	}

	public Scene getScene() {
		return scene;
	}
}
