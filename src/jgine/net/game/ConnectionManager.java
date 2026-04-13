package jgine.net.game;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Entity;
import jgine.core.Prefab;
import jgine.core.Scene;
import jgine.net.game.packet.listener.GameClientPacketListener;
import jgine.net.game.packet.listener.GameServerPacketListener;
import jgine.net.game.packet.packets.EntityDeletePacket;
import jgine.net.game.packet.packets.EntitySpawnPacket;
import jgine.net.game.packet.packets.PrefabSpawnPacket;

public class ConnectionManager {

	private static GameServer server;
	private static GameClient client;

	public static GameServer startServer(String name, int port, int maxConnections) {
		if (isClient())
			throw new IllegalStateException("Can not start a server while already connected!");
		else if (isServer())
			throw new IllegalStateException("Can not start a server while already hosting!");
		server = new GameServer(name, port, maxConnections);
		server.addListener(new GameServerPacketListener());
		new Thread(server).start();
		return server;
	}

	public static GameClient startClient(String name, String serverIpAddress, int serverPort, int maxConnections) {
		if (isClient())
			throw new IllegalStateException("Can not start a client while already connected!");
		else if (isServer())
			throw new IllegalStateException("Can not start a client while already hosting!");
		client = new GameClient(serverIpAddress, serverPort, maxConnections);
		client.addListener(new GameClientPacketListener());
		new Thread(client).start();
		client.connect(name);
		return client;
	}

	public static void stopServer() {
		if (!isServer())
			return;
		server.stop();
		server = null;
	}

	public static void stopClient() {
		if (!isClient())
			return;
		client.disconnect();
		client.stop();
		client = null;
	}

	public static void terminate() {
		if (isClient())
			stopClient();
		else if (isServer())
			stopServer();
	}

	public static void update() {
		if (isClient())
			client.update();
		else if (isServer())
			server.update();
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

	public static void setTrackedEntity(Entity entity) {
		if (isClient())
			client.setTrackedEntity(entity);
	}

	public static void freeEntityId(int id) {
		if (isServer())
			server.freeEntityId(id);
	}

	@Nullable
	public static Entity createEntity(Scene scene) {
		if (!isServer())
			return new Entity(server.generateEntityId(), scene);
		return null;
	}

	public static void spawnEntity(Entity entity) {
		if (isClient()) {
			client.sendData(EntitySpawnPacket.fromEntity(entity));
			entity.delete();
		} else if (isServer()) {
			if (!entity.isRemote())
				throw new IllegalStateException("entity must have remote id to be spawned this way!");
			server.sendDataToAll(EntitySpawnPacket.fromEntity(entity));
		}
	}

	@Nullable
	public static Entity spawnPrefab(Prefab prefab, Scene scene) {
		Entity entity = null;
		if (isClient()) {
			client.sendData(new PrefabSpawnPacket(0, prefab, scene));
		} else if (isServer()) {
			int id = server.generateEntityId();
			entity = prefab.create(id, scene);
			server.sendDataToAll(new PrefabSpawnPacket(id, prefab, scene));
		}
		return entity;
	}

	public static void deleteEntity(Entity entity) {
		if (!entity.isRemote())
			throw new IllegalStateException("entity must be remote to be deleted this way!");
		if (isClient()) {
			client.sendData(new EntityDeletePacket(entity.id));
		} else if (isServer()) {
			server.sendDataToAll(new EntityDeletePacket(entity.id));
			entity.delete();
		}
	}
}
