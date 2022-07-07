package org.jgine.net.game;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.net.game.packet.listener.GameClientPacketListener;
import org.jgine.net.game.packet.listener.GameServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;

public class ConnectionManager {

	private static GameServer server;
	private static GameClient client;
	private static boolean isServer = true;

	public static void init() {
		if (isServer) {
			server = new GameServer(1331, 100);
			server.addListener(new GameServerPacketListener());
			new Thread(server).start();
		}
		client = new GameClient("localhost", 1331, 100);
		client.addListener(new GameClientPacketListener());
		new Thread(client).start();

		client.sendData(new ConnectPacket("testName"));
		Scheduler.runTaskLater(100, () -> client.sendData(new PingPacket()));
	}

	public static void terminate() {
		client.sendData(new DisconnectPacket("testName"));

		if (isServer)
			server.stop();
		client.stop();
	}

	public static void update() {
		if (isServer)
			server.update();
		client.update();
	}

	@Nullable
	public static GameServer getServer() {
		return server;
	}

	public static GameClient getClient() {
		return client;
	}

	public List<PlayerConnection> getPlayer() {
		return isServer ? server.getPlayer() : client.getPlayer();
	}

	@Nullable
	public PlayerConnection getPlayer(String name) {
		return isServer ? server.getPlayer(name) : client.getPlayer(name);
	}

	@Nullable
	public PlayerConnection getPlayer(int id) {
		return isServer ? server.getPlayer(id) : client.getPlayer(id);
	}
}
