package org.jgine.net.game;

import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;

public class ServerManager {

	private static GameServer server;
	private static GameClient client;

	public static void init() {
		boolean isServer = true;
		if (isServer) {
			server = new GameServer(1331);
			new Thread(server).start();
		}
		client = new GameClient("localhost", 1331);
		new Thread(client).start();

		client.sendData(new ConnectPacket("testName"));
	}

	public static void terminate() {
		client.sendData(new DisconnectPacket("testName"));

		if (server != null)
			server.stop();
		client.stop();
	}
}
