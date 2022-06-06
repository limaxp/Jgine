package org.jgine.net.game;

import org.jgine.net.game.packet.listener.GameClientListener;
import org.jgine.net.game.packet.listener.GameServerListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;

public class ServerManager {

	private static GameServer server;
	private static GameClient client;

	public static void init() {
		boolean isServer = true;
		if (isServer) {
			server = new GameServer(1331);
			server.addListener(new GameServerListener());
			new Thread(server).start();
		}
		client = new GameClient("localhost", 1331);
		client.addListener(new GameClientListener());
		new Thread(client).start();

		client.sendData(new ConnectPacket("testName"));

		client.sendData(new PingPacket());
	}

	public static void terminate() {
		client.sendData(new DisconnectPacket("testName"));

		if (server != null)
			server.stop();
		client.stop();
	}
}
