package org.jgine.net.game.packet.listener;

import org.jgine.net.game.ConnectionManager;
import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.ServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;
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
	public void on(SpawnPrefabPacket packet, PlayerConnection connection) {
		ConnectionManager.getServer().sendDataToAll(packet);
	}
}
