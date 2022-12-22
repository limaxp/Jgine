package org.jgine.net.game.packet;

import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.EntityDeletePacket;
import org.jgine.net.game.packet.packets.EntitySpawnPacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.PrefabSpawnPacket;

public interface ServerPacketListener {

	public void onInvalid(Packet packet, PlayerConnection connection);

	public void on(ConnectPacket packet, PlayerConnection connection);

	public void on(DisconnectPacket packet, PlayerConnection connection);

	public void on(PingPacket packet, PlayerConnection connection);

	public void on(PositionPacket packet, PlayerConnection connection);

	public void on(PrefabSpawnPacket packet, PlayerConnection connection);

	public void on(EntitySpawnPacket packet, PlayerConnection connection);

	public void on(EntityDeletePacket packet, PlayerConnection connection);
}
