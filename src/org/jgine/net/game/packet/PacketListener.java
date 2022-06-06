package org.jgine.net.game.packet;

import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.SpawnPrefabPacket;

public interface PacketListener {

	public void onInvalid(Packet packet);

	public void on(ConnectPacket packet);

	public void on(DisconnectPacket packet);

	public void on(PingPacket packet);

	public void on(PositionPacket packet);

	public void on(SpawnPrefabPacket packet);
}
