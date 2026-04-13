package jgine.net.game.packet;

import jgine.net.game.PlayerConnection;
import jgine.net.game.packet.packets.ConnectPacket;
import jgine.net.game.packet.packets.DisconnectPacket;
import jgine.net.game.packet.packets.EntityDeletePacket;
import jgine.net.game.packet.packets.EntitySpawnPacket;
import jgine.net.game.packet.packets.PingPacket;
import jgine.net.game.packet.packets.PositionPacket;
import jgine.net.game.packet.packets.PrefabSpawnPacket;

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
