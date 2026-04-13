package jgine.net.game.packet.listener;

import jgine.core.Entity;
import jgine.net.game.ConnectionManager;
import jgine.net.game.PlayerConnection;
import jgine.net.game.packet.Packet;
import jgine.net.game.packet.ServerPacketListener;
import jgine.net.game.packet.packets.ConnectPacket;
import jgine.net.game.packet.packets.DisconnectPacket;
import jgine.net.game.packet.packets.EntityDeletePacket;
import jgine.net.game.packet.packets.EntitySpawnPacket;
import jgine.net.game.packet.packets.PingPacket;
import jgine.net.game.packet.packets.PositionPacket;
import jgine.net.game.packet.packets.PrefabSpawnPacket;
import jgine.utils.math.vector.Vector3f;

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
		Entity senderEntity = Entity.getById(packet.getId());
		if (senderEntity != null)
			senderEntity.getTransform().setPosition(packet.getX(), packet.getY(), packet.getZ());

		for (Entity entity : ConnectionManager.getServer().getTrackedEntities()) {
			Vector3f pos = entity.getTransform().getPosition();
			ConnectionManager.getServer().sendData(new PositionPacket(entity, pos.x, pos.y, pos.z), connection);
		}
	}

	@Override
	public void on(PrefabSpawnPacket packet, PlayerConnection connection) {
		int id = ConnectionManager.getServer().generateEntityId();
		packet.getPrefab().create(id, packet.getScene());
		ConnectionManager.getServer().sendDataToAll(new PrefabSpawnPacket(id, packet.getPrefab(), packet.getScene()));
	}

	@Override
	public void on(EntitySpawnPacket packet, PlayerConnection connection) {
		int id = ConnectionManager.getServer().generateEntityId();
		EntitySpawnPacket.toEntity(packet, id);
		ConnectionManager.getServer().sendDataToAll(new EntitySpawnPacket(id, packet.getScene(), packet.getData()));
	}

	@Override
	public void on(EntityDeletePacket packet, PlayerConnection connection) {
		Entity.getById(packet.getId()).delete();
		ConnectionManager.getServer().sendDataToAll(packet);
	}
}
