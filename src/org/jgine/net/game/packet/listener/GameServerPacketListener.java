package org.jgine.net.game.packet.listener;

import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.net.game.ConnectionManager;
import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.ServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.EntityDeletePacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.EntitySpawnPacket;
import org.jgine.net.game.packet.packets.PrefabSpawnPacket;

public class GameServerPacketListener implements ServerPacketListener {

	@Override
	public void onInvalid(Packet packet, PlayerConnection connection) {
	}

	@Override
	public void on(ConnectPacket packet, PlayerConnection connection) {
	}

	@Override
	public void on(DisconnectPacket packet, PlayerConnection connection) {
	}

	@Override
	public void on(PingPacket packet, PlayerConnection connection) {
		ConnectionManager.getServer().sendData(packet, connection);
	}

	@Override
	public void on(PositionPacket packet, PlayerConnection connection) {
		for (Entity entity : ConnectionManager.getServer().getTrackedEntities()) {
			Vector3f pos = entity.transform.getPosition();
			ConnectionManager.getServer().sendData(new PositionPacket(entity, pos.x, pos.y, pos.z), connection);
		}
	}

	@Override
	public void on(PrefabSpawnPacket packet, PlayerConnection connection) {
		int id = ConnectionManager.getServer().generateEntityId();
		packet.getPrefab().create(id, packet.getScene(), packet.getX(), packet.getY(), packet.getZ(), 0.0f, 0.0f, 0.0f,
				1.0f, 1.0f, 1.0f);
		ConnectionManager.getServer().sendDataToAll(new PrefabSpawnPacket(id, packet.getPrefab(), packet.getScene(),
				packet.getX(), packet.getY(), packet.getZ()));
	}

	@Override
	public void on(EntitySpawnPacket packet, PlayerConnection connection) {
		int id = ConnectionManager.getServer().generateEntityId();
		EntitySpawnPacket.toEntity(packet, id);
		ConnectionManager.getServer().sendDataToAll(new EntitySpawnPacket(id, packet.getScene(), packet.getX(),
				packet.getY(), packet.getZ(), packet.getData()));
	}

	@Override
	public void on(EntityDeletePacket packet, PlayerConnection connection) {
		Entity.getById(packet.getId()).delete();
		ConnectionManager.getServer().sendDataToAll(packet);
	}
}
