package org.jgine.net.game;

import java.nio.ByteBuffer;

import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;

public class ServerManager {

	private static GameServer server;
	private static GameClient client;

	public static void init() {
		boolean isServer = true;
		if (isServer) {
			server = new GameServer();
			new Thread(server).start();
		}
		client = new GameClient("localhost");
		new Thread(client).start();

		ConnectPacket packet = new ConnectPacket("testName");
		byte[] data = new byte[1024];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		packet.write(buffer);
		client.sendData(data);
	}

	public static void terminate() {
		DisconnectPacket packet = new DisconnectPacket("testName");
		byte[] data = new byte[1024];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		packet.write(buffer);
		client.sendData(data);

		if (server != null)
			server.stop();
		client.stop();
	}
}
