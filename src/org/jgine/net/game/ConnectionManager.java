package org.jgine.net.game;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.net.game.packet.listener.GameClientPacketListener;
import org.jgine.net.game.packet.listener.GameServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;

public class ConnectionManager {

	private static GameServer server;
	private static GameClient client;
	
	public static void init() {
	}

	public static void terminate() {
		if (isClient())
			stopClient();
		if (isServer())
			stopServer();
	}

	public static void update() {
		if (isClient())
			client.update();
		if (isServer())
			server.update();
	}

	public static GameServer startServer(int port, int maxConnections) {
		if (isClient())
			throw new IllegalStateException("Can not start a server while already connected!");
		if (isServer())
			throw new IllegalStateException("Can not start a server while already hosting!");
		server = new GameServer(port, maxConnections);
		server.addListener(new GameServerPacketListener());
		new Thread(server).start();
		return server;
	}

	public static void stopServer() {
		if (!isServer())
			return;
		server.stop();
		server = null;
	}

	public static GameClient startClient(String serverIpAddress, int serverPort, int maxConnections) {
		if (isClient())
			throw new IllegalStateException("Can not start a client while already connected!");
		if (isServer())
			throw new IllegalStateException("Can not start a client while already hosting!");
		client = new GameClient(serverIpAddress, serverPort, maxConnections);
		client.addListener(new GameClientPacketListener());
		new Thread(client).start();
		client.sendData(new ConnectPacket("testName"));
		return client;
	}

	public static void stopClient() {
		if (!isClient())
			return;
		client.sendData(new DisconnectPacket(client.getId()));
		client.stop();
		client = null;
	}

	@Nullable
	public static GameServer getServer() {
		return server;
	}

	@Nullable
	public static GameClient getClient() {
		return client;
	}

	public static boolean isServer() {
		return server != null;
	}

	public static boolean isClient() {
		return client != null;
	}

	@Nullable
	public List<PlayerConnection> getPlayerList() {
		return isClient() ? client.getPlayerList() : isServer() ? server.getPlayerList() : null;
	}

	@Nullable
	public PlayerConnection getPlayer(String name) {
		return isClient() ? client.getPlayer(name) : isServer() ? server.getPlayer(name) : null;
	}

	@Nullable
	public PlayerConnection getPlayer(int id) {
		return isClient() ? client.getPlayer(id) : isServer() ? server.getPlayer(id) : null;
	}
	
	public PlayerConnection getPlayer() {
		return isClient() ? client.getPlayer() : isServer() ? server.getPlayer() : null;
	}

	public static void setTrackedEntity(Entity player) {
		if(isClient())
			client.setTrackedEntity(player);
	}
}
