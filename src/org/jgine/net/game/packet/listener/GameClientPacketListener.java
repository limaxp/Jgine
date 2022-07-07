package org.jgine.net.game.packet.listener;

import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.ClientPacketListener;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.SpawnPrefabPacket;

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
		// TODO this will throw errors sometime because UpdateManager call in
		// setPosition()
//		System.out.println("hi"); // important for testing this error
//		Entity entity = Entity.getById(packet.getEntityId());
//		Vector3f pos = entity.transform.getPosition();
//		if (Vector3f.distance(pos.x, pos.y, pos.z, packet.getX(), packet.getY(), packet.getZ()) > 10)
//			entity.transform.setPosition(packet.getX(), packet.getY(), packet.getZ());
	}

	@Override
	public void on(SpawnPrefabPacket packet) {
		Entity entity = packet.getPrefab().create(packet.getScene());
		entity.transform.setPosition(packet.getX(), packet.getY(), packet.getZ());
	}
}
