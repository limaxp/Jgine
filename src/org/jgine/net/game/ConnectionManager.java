package org.jgine.net.game;

import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.net.game.packet.listener.GameClientPacketListener;
import org.jgine.net.game.packet.listener.GameServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;

public class ConnectionManager {

	private static GameServer server;
	private static GameClient client;

	public static void init() {
		boolean isServer = true;
		if (isServer) {
			server = new GameServer(1331, 100);
			server.addListener(new GameServerPacketListener());
			new Thread(server).start();
		}
		client = new GameClient("localhost", 1331);
		client.addListener(new GameClientPacketListener());
		new Thread(client).start();

		client.sendData(new ConnectPacket("testName"));
		Scheduler.runTaskLater(100, () -> client.sendData(new PingPacket()));
	}

	public static void terminate() {
		client.sendData(new DisconnectPacket("testName"));

		if (server != null)
			server.stop();
		client.stop();
	}

	public static GameServer getServer() {
		return server;
	}

	public static GameClient getClient() {
		return client;
	}
}
