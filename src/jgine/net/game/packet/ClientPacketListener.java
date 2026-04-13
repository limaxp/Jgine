package jgine.net.game.packet;

import jgine.net.game.packet.packets.ConnectResponsePacket;
import jgine.net.game.packet.packets.EntityDeletePacket;
import jgine.net.game.packet.packets.EntitySpawnPacket;
import jgine.net.game.packet.packets.PingPacket;
import jgine.net.game.packet.packets.PlayerListPacket;
import jgine.net.game.packet.packets.PositionPacket;
import jgine.net.game.packet.packets.PrefabSpawnPacket;

public interface ClientPacketListener {

	public void onInvalid(Packet packet);

	public void on(ConnectResponsePacket packet);

	public void on(PlayerListPacket packet);

	public void on(PingPacket packet);

	public void on(PositionPacket packet);

	public void on(PrefabSpawnPacket packet);

	public void on(EntitySpawnPacket packet);

	public void on(EntityDeletePacket packet);
}
