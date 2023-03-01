package org.jgine.net.game;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.entity.Prefab;
import org.jgine.net.game.packet.listener.GameClientPacketListener;
import org.jgine.net.game.packet.listener.GameServerPacketListener;
import org.jgine.net.game.packet.packets.EntityDeletePacket;
import org.jgine.net.game.packet.packets.EntitySpawnPacket;
import org.jgine.net.game.packet.packets.PrefabSpawnPacket;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

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
		return createEntity(scene, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Nullable
	public static Entity createEntity(Scene scene, Vector2f position) {
		return createEntity(scene, position, Vector2f.NULL, Vector2f.FULL);
	}

	@Nullable
	public static Entity createEntity(Scene scene, Vector2f position, Vector2f rotation, Vector2f scale) {
		return createEntity(scene, position.x, position.y, 0.0f, rotation.x, rotation.y, 0.0f, scale.x, scale.y, 1.0f);
	}

	@Nullable
	public static Entity createEntity(Scene scene, float posX, float posY) {
		return createEntity(scene, posX, posY, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Nullable
	public static Entity createEntity(Scene scene, float posX, float posY, float rotX, float rotY, float scaleX,
			float scaleY) {
		return createEntity(scene, posX, posY, 0.0f, rotX, rotY, 0.0f, scaleX, scaleY, 1.0f);
	}

	@Nullable
	public static Entity createEntity(Scene scene, Vector3f position) {
		return createEntity(scene, position, Vector3f.NULL, Vector3f.FULL);
	}

	@Nullable
	public static Entity createEntity(Scene scene, Vector3f position, Vector3f rotation, Vector3f scale) {
		return createEntity(scene, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x,
				scale.y, scale.z);
	}

	@Nullable
	public static Entity createEntity(Scene scene, float posX, float posY, float posZ) {
		return createEntity(scene, posX, posY, posZ, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Nullable
	public static Entity createEntity(Scene scene, float posX, float posY, float posZ, float rotX, float rotY,
			float rotZ, float scaleX, float scaleY, float scaleZ) {
		if (!isServer())
			return new Entity(server.generateEntityId(), scene, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY,
					scaleZ);
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
	public static Entity spawnPrefab(Prefab prefab, Scene scene, float x, float y, float z) {
		Entity entity = null;
		if (isClient()) {
			client.sendData(new PrefabSpawnPacket(0, prefab, scene, x, y, z));
		} else if (isServer()) {
			int id = server.generateEntityId();
			entity = prefab.create(id, scene, x, y, z, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			server.sendDataToAll(new PrefabSpawnPacket(id, prefab, scene, x, y, z));
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
