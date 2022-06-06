package org.jgine.net.game.packet.listener;

import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.SpawnPrefabPacket;

public class GameServerListener implements PacketListener {

	@Override
	public void onInvalid(Packet packet) {
	}

	@Override
	public void on(ConnectPacket packet) {
	}

	@Override
	public void on(DisconnectPacket packet) {
	}

	@Override
	public void on(PingPacket packet) {
	}

	@Override
	public void on(PositionPacket packet) {
	}

	@Override
	public void on(SpawnPrefabPacket packet) {
	}
}
