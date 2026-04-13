package jgine.net.game.packet.listener;

import jgine.core.Entity;
import jgine.net.game.packet.ClientPacketListener;
import jgine.net.game.packet.Packet;
import jgine.net.game.packet.packets.ConnectResponsePacket;
import jgine.net.game.packet.packets.EntityDeletePacket;
import jgine.net.game.packet.packets.EntitySpawnPacket;
import jgine.net.game.packet.packets.PingPacket;
import jgine.net.game.packet.packets.PlayerListPacket;
import jgine.net.game.packet.packets.PositionPacket;
import jgine.net.game.packet.packets.PrefabSpawnPacket;
import jgine.utils.Logger;

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
		entity.getTransform().setPosition(packet.getX(), packet.getY(), packet.getZ());
	}

	@Override
	public void on(PrefabSpawnPacket packet) {
		packet.getPrefab().create(packet.getId(), packet.getScene());
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
