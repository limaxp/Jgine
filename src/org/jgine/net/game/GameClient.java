package org.jgine.net.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.id.IdGenerator;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.ClientPacketListener;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;
import org.jgine.net.game.packet.packets.PositionPacket;

public class GameClient implements Runnable {

	public static final Consumer<PlayerConnection> NULL_PLAYER_CALLBACK = (player) -> {
	};

	private InetAddress serverIpAddress;
	private int serverPort;
	private DatagramSocket socket;
	private boolean isRunning;
	private final List<ClientPacketListener> listener;
	private final List<PlayerConnection> playerList;
	private final Map<String, PlayerConnection> nameMap;
	private final PlayerConnection[] idMap;
	private int id;
	private Entity trackedEntity;
	private Consumer<PlayerConnection> addPlayerCallback;
	private Consumer<PlayerConnection> removePlayerCallback;

	public GameClient(String serverIpAddress, int serverPort, int maxConnections) {
		try {
			this.serverIpAddress = InetAddress.getByName(serverIpAddress);
			this.socket = new DatagramSocket();
			this.socket.setSoTimeout(1000);
		} catch (UnknownHostException e) {
			Logger.err("GameClient: Error server address is unknown!", e);
		} catch (SocketException e) {
			Logger.err("GameClient: Error creating socket!", e);
		}
		this.serverPort = serverPort;
		listener = new IdentityArrayList<ClientPacketListener>();
		playerList = new IdentityArrayList<PlayerConnection>(maxConnections);
		nameMap = new HashMap<String, PlayerConnection>(maxConnections);
		idMap = new PlayerConnection[maxConnections + 1];
		id = -1;
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
				Logger.err("GameClient: Error listening for packets!", e);
			}
			parsePacket(data);
		}
		socket.close();
	}

	public void update() {
		if (trackedEntity != null) {
			Vector3f pos = trackedEntity.transform.getPosition();
			sendData(new PositionPacket(trackedEntity, pos.x, pos.y, pos.z));
		} else {
			sendData(new PositionPacket(-1, 0, 0, 0));
		}
	}

	private <T extends Packet> void parsePacket(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int paketId = buffer.getInt();
		T gamePacket = PacketManager.get(paketId);
		gamePacket.read(buffer);

		if (paketId == PacketManager.CONNECT_RESPONSE) {
			ConnectResponsePacket connectResponsePacket = (ConnectResponsePacket) gamePacket;
			if (connectResponsePacket.isAccepted())
				this.id = connectResponsePacket.getId();
		} else if (paketId == PacketManager.PLAYER_LIST) {
			PlayerListPacket playerListPacket = (PlayerListPacket) gamePacket;
			switch (playerListPacket.getAction()) {
			case ADD:
				for (PlayerConnection player : playerListPacket.getPlayers())
					addConnection(player);
				break;

			case REMOVE:
				for (PlayerConnection player : playerListPacket.getPlayers())
					removeConnection(player);
				break;
			}
		}

		BiConsumer<ClientPacketListener, T> function = PacketManager.getClientListenerFunction(paketId);
		for (ClientPacketListener currentListener : listener)
			function.accept(currentListener, gamePacket);
	}

	private void addConnection(PlayerConnection player) {
		idMap[IdGenerator.index(player.id)] = player;
		nameMap.put(player.name, player);
		playerList.add(player);
		addPlayerCallback.accept(player);
	}

	private void removeConnection(PlayerConnection player) {
		idMap[IdGenerator.index(player.id)] = null;
		nameMap.remove(player.name);
		playerList.remove(player);
		removePlayerCallback.accept(player);
	}

	private void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIpAddress, serverPort);
		try {
			socket.send(packet);
		} catch (IOException e) {
			Logger.err("GameClient: Error sending data!", e);
		}
	}

	public void sendData(Packet packet) {
		byte[] data = new byte[1024];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.putInt(this.id);
		packet.write(buffer);
		sendData(data);
	}

	public InetAddress getServerIp() {
		return serverIpAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public InetAddress getIp() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}

	public int getId() {
		return id;
	}

	public void addListener(ClientPacketListener listener) {
		this.listener.add(listener);
	}

	public void removeListener(ClientPacketListener listener) {
		this.listener.remove(listener);
	}

	public List<PlayerConnection> getPlayerList() {
		return Collections.unmodifiableList(playerList);
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
		return idMap[IdGenerator.index(id)];
	}

	public int getMaxPlayer() {
		return idMap.length;
	}

	public void setTrackedEntity(Entity trackedEntity) {
		this.trackedEntity = trackedEntity;
	}

	public Entity getTrackedEntity() {
		return trackedEntity;
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
}
