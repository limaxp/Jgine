package org.jgine.net.game.packet;

import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.SpawnPrefabPacket;

public interface ClientPacketListener {

	public void onInvalid(Packet packet);

	public void on(ConnectResponsePacket packet);

	public void on(PlayerListPacket packet);

	public void on(PingPacket packet);

	public void on(PositionPacket packet);

	public void on(SpawnPrefabPacket packet);
}
