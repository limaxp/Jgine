package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Prefab;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class SpawnPrefabPacket extends Packet {

	// TODO: prefab and scenes should get an id;

	private Prefab prefab;
	private Scene scene;

	public SpawnPrefabPacket() {
	}

	public SpawnPrefabPacket(Prefab prefab) {
		this.prefab = prefab;
	}

	@Override
	public void read(ByteBuffer buffer) {
		byte[] prefabNameBytes = new byte[buffer.getInt()];
		buffer.get(prefabNameBytes);
		prefab = Prefab.get(new String(prefabNameBytes));

		byte[] sceneNameBytes = new byte[buffer.getInt()];
		buffer.get(sceneNameBytes);
		scene = Engine.getInstance().getScene(new String(sceneNameBytes));
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.SPAWN_PREFAB);
		buffer.putInt(prefab.name.length());
		buffer.put(prefab.name.getBytes());

		buffer.putInt(scene.name.length());
		buffer.put(scene.name.getBytes());
	}

	public Prefab getPrefab() {
		return prefab;
	}

	public Scene getScene() {
		return scene;
	}
}
