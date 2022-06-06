package org.jgine.net.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.utils.function.TriConsumer;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.ServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;

public class GameServer implements Runnable {

	private DatagramSocket socket;
	private boolean isRunning;
	private List<ServerPacketListener> listener;
	private List<PlayerConnection> player;
	private Map<String, PlayerConnection> nameMap;
	private Map<Integer, PlayerConnection> idMap;

	public GameServer(int port) {
		try {
			this.socket = new DatagramSocket(port);
			this.socket.setSoTimeout(1000);
		} catch (SocketException e) {
			Logger.err("GameServer: Error creating socket!", e);
		}
		listener = new ArrayList<ServerPacketListener>();
		player = new ArrayList<PlayerConnection>();
		nameMap = new HashMap<String, PlayerConnection>();
		idMap = new HashMap<Integer, PlayerConnection>();
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
		for (PlayerConnection p : player)
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

	private PlayerConnection addConnection(ConnectPacket paket, InetAddress address, int port) {
		PlayerConnection player = getPlayer(paket.getName());
		if (player != null) {
			Logger.warn("GameServer: Connect error! Duplicate player name '" + paket.getName() + "'");
			return null;
		}
		int playerId = generateId();
		player = new PlayerConnection(address, port, paket.getName(), playerId);
		this.player.add(player);
		nameMap.put(player.name, player);
		idMap.put(player.id, player);
		sendData(new ConnectResponsePacket(true, playerId), player);
		return player;
	}

	private PlayerConnection removeConnection(DisconnectPacket paket) {
		PlayerConnection player = getPlayer(paket.getName());
		if (player == null) {
			Logger.warn("GameServer: Disconnect error! Player does not exist '" + paket.getName() + "'");
			return null;
		}
		player = nameMap.remove(paket.getName());
		idMap.remove(player.id);
		this.player.remove(player);
		return player;
	}

	private int generateId() {
		int id;
		do {
			id = FastMath.random(Integer.MAX_VALUE);
		} while (getPlayer(id) != null);
		return id;
	}

	public List<PlayerConnection> getPlayer() {
		return Collections.unmodifiableList(player);
	}

	@Nullable
	public PlayerConnection getPlayer(String name) {
		return nameMap.get(name);
	}

	@Nullable
	public PlayerConnection getPlayer(int id) {
		return idMap.get(id);
	}
}
