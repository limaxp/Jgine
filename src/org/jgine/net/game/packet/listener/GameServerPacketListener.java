package org.jgine.net.game.packet.listener;

import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.net.game.ConnectionManager;
import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.ServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.SpawnEntityPacket;
import org.jgine.net.game.packet.packets.SpawnPrefabPacket;

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
	public void on(SpawnPrefabPacket packet, PlayerConnection connection) {
		ConnectionManager.getServer().sendDataToAll(packet);
	}
	
	@Override
	public void on(SpawnEntityPacket packet, PlayerConnection connection) {
		ConnectionManager.getServer().sendDataToAll(packet);
	}
}
