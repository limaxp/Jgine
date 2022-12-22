package org.jgine.net.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.utils.function.TriConsumer;
import org.jgine.misc.utils.id.IdGenerator;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.ServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.EntitySpawnPacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;
import org.jgine.net.game.packet.packets.PlayerListPacket.PlayerListAction;

public class GameServer implements Runnable {

	public static final int MAX_ENTITIES = 10000;

	public static final Consumer<PlayerConnection> NULL_PLAYER_CALLBACK = (player) -> {
	};

	private DatagramSocket socket;
	private boolean isRunning;
	private final List<ServerPacketListener> listener;
	private final List<PlayerConnection> playerList;
	private final List<PlayerConnection> clientList;
	private final Map<String, PlayerConnection> nameMap;
	private final IdGenerator idGenerator;
	private final PlayerConnection[] idMap;
	private final PlayerConnection player;
	private final List<Entity> trackedEntities;
	private final IdGenerator entityIdGenerator;
	private Consumer<PlayerConnection> addPlayerCallback;
	private Consumer<PlayerConnection> removePlayerCallback;

	public GameServer(int port, int maxConnections) {
		try {
			this.socket = new DatagramSocket(port);
			this.socket.setSoTimeout(1000);
		} catch (SocketException e) {
			Logger.err("GameServer: Error creating socket!", e);
		}
		listener = new IdentityArrayList<ServerPacketListener>();
		playerList = new IdentityArrayList<PlayerConnection>(maxConnections);
		clientList = new IdentityArrayList<PlayerConnection>(maxConnections - 1);
		nameMap = new HashMap<String, PlayerConnection>(maxConnections);
		idGenerator = new IdGenerator(1, maxConnections + 1);
		idMap = new PlayerConnection[maxConnections + 1];

		player = new PlayerConnection(idGenerator.generate(), "host", getIp(), port);
		nameMap.put(player.name, player);
		idMap[IdGenerator.index(player.id)] = player;
		playerList.add(player);

		trackedEntities = new IdentityArrayList<Entity>(MAX_ENTITIES);
		entityIdGenerator = new IdGenerator(0, Entity.MAX_ENTITIES);
		addPlayerCallback = NULL_PLAYER_CALLBACK;
		removePlayerCallback = NULL_PLAYER_CALLBACK;
	}

	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (SocketTimeoutException e) {
				// ignore
			} catch (IOException e) {
				Logger.err("GameServer: Error listening for packets!", e);
			}
			parsePacket(data, packet.getAddress(), packet.getPort());
		}
		socket.close();
	}

	public void update() {
	}

	private <T extends Packet> void parsePacket(byte[] data, InetAddress address, int port) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int playerId = buffer.getInt();
		int paketId = buffer.getInt();
		T gamePacket = PacketManager.get(paketId);
		gamePacket.read(buffer);

		PlayerConnection connection;
		if (paketId == PacketManager.CONNECT)
			connection = addConnection((ConnectPacket) gamePacket, address, port);
		else if (paketId == PacketManager.DISCONNECT)
			connection = removeConnection((DisconnectPacket) gamePacket);
		else
			connection = getPlayer(playerId);

		TriConsumer<ServerPacketListener, T, PlayerConnection> function = PacketManager
				.getServerListenerFunction(paketId);
		for (ServerPacketListener currentListener : listener)
			function.accept(currentListener, gamePacket, connection);
	}

	private PlayerConnection addConnection(ConnectPacket paket, InetAddress address, int port) {
		PlayerConnection player = getPlayer(paket.getName());
		if (player != null) {
			Logger.warn("GameServer: Connect error! Duplicate player name '" + paket.getName() + "'");
			return null;
		}
		player = new PlayerConnection(idGenerator.generate(), paket.getName(), address, port);
		sendDataToAll(new PlayerListPacket(PlayerListAction.ADD, Arrays.asList(player)));
		idMap[IdGenerator.index(player.id)] = player;
		nameMap.put(player.name, player);
		playerList.add(player);
		clientList.add(player);
		sendData(new ConnectResponsePacket(true, player.id), player);
		sendData(new PlayerListPacket(PlayerListAction.ADD, playerList), player);

		for (Entity entity : trackedEntities)
			sendData(EntitySpawnPacket.fromEntity(entity), player);

		addPlayerCallback.accept(player);
		return player;
	}

	private PlayerConnection removeConnection(DisconnectPacket paket) {
		PlayerConnection player = getPlayer(paket.getId());
		if (player == null) {
			Logger.warn("GameServer: Disconnect error! Player does not exist '" + paket.getId() + "'");
			return null;
		}
		int idIndex = idGenerator.free(paket.getId());
		player = idMap[idIndex];
		idMap[idIndex] = null;
		nameMap.remove(player.name);
		playerList.remove(player);
		clientList.remove(player);
		sendDataToAll(new PlayerListPacket(PlayerListAction.REMOVE, Arrays.asList(player)));
		removePlayerCallback.accept(player);
		return player;
	}

	private void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			Logger.err("GameServer: Error sending data!", e);
		}
	}

	public void sendData(Packet packet, PlayerConnection connection) {
		sendData(packet, connection.address, connection.port);
	}

	public void sendData(Packet packet, InetAddress ipAddress, int port) {
		byte[] data = new byte[1024];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		packet.write(buffer);
		sendData(data, ipAddress, port);
	}

	public void sendDataToAll(Packet packet) {
		for (PlayerConnection p : clientList)
			sendData(packet, p.address, p.port);
	}

	public InetAddress getIp() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}

	public void addListener(ServerPacketListener listener) {
		this.listener.add(listener);
	}

	public void removeListener(ServerPacketListener listener) {
		this.listener.remove(listener);
	}

	public List<PlayerConnection> getPlayerList() {
		return Collections.unmodifiableList(playerList);
	}

	public List<PlayerConnection> getClientList() {
		return Collections.unmodifiableList(clientList);
	}

	@Nullable
	public PlayerConnection getPlayer(String name) {
		return nameMap.get(name);
	}

	@Nullable
	public PlayerConnection getPlayer(int id) {
		return idMap[IdGenerator.index(id)];
	}

	public PlayerConnection getPlayer() {
		return player;
	}

	public int getMaxPlayer() {
		return idMap.length;
	}

	public boolean isAlive(PlayerConnection connection) {
		return idGenerator.isAlive(connection.id);
	}

	public List<Entity> getTrackedEntities() {
		return trackedEntities;
	}

	public void setAddPlayerCallback(Consumer<PlayerConnection> addPlayerCallback) {
		this.addPlayerCallback = addPlayerCallback;
	}

	public Consumer<PlayerConnection> getAddPlayerCallback() {
		return addPlayerCallback;
	}

	public void setRemovePlayerCallback(Consumer<PlayerConnection> removePlayerCallback) {
		this.removePlayerCallback = removePlayerCallback;
	}

	public Consumer<PlayerConnection> getRemovePlayerCallback() {
		return removePlayerCallback;
	}

	public int generateEntityId() {
		int id;
		synchronized (entityIdGenerator) {
			id = entityIdGenerator.generate();
		}
		return IdGenerator.id(IdGenerator.index(id) + Entity.MAX_ENTITIES + 2, IdGenerator.generation(id));
	}

	public void freeEntityId(int id) {
		int realId = IdGenerator.id(IdGenerator.index(id) - Entity.MAX_ENTITIES - 2, IdGenerator.generation(id));
		synchronized (entityIdGenerator) {
			entityIdGenerator.free(realId);
		}
	}
}
