package org.jgine.net.game.packet.listener;

import org.jgine.core.entity.Entity;
import org.jgine.net.game.packet.ClientPacketListener;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.EntityDeletePacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.EntitySpawnPacket;
import org.jgine.net.game.packet.packets.PrefabSpawnPacket;
import org.jgine.utils.logger.Logger;

public class GameClientPacketListener implements ClientPacketListener {

	@Override
	public void onInvalid(Packet packet) {
	}

	@Override
	public void on(ConnectResponsePacket packet) {
	}

	@Override
	public void on(PlayerListPacket packet) {
	}

	@Override
	public void on(PingPacket packet) {
		long time = System.currentTimeMillis() - packet.getTime();
		Logger.log("GameClient: ping = " + time);
	}

	@Override
	public void on(PositionPacket packet) {
		Entity entity = Entity.getById(packet.getId());
		entity.transform.setPosition(packet.getX(), packet.getY(), packet.getZ());
	}

	@Override
	public void on(PrefabSpawnPacket packet) {
		packet.getPrefab().create(packet.getId(), packet.getScene(), packet.getX(), packet.getY(), packet.getZ(), 0.0f,
				0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void on(EntitySpawnPacket packet) {
		EntitySpawnPacket.toEntity(packet);
	}

	@Override
	public void on(EntityDeletePacket packet) {
		Entity.getById(packet.getId()).delete();
	}
}
