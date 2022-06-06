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
import org.jgine.misc.utils.function.TriConsumer;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.ServerPacketListener;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;

public class GameServer implements Runnable {

	private DatagramSocket socket;
	private boolean isRunning;
	private List<ServerPacketListener> listener;
	private List<PlayerConnection> player;
	private Map<String, PlayerConnection> nameMap;

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
		int id = buffer.getInt();
		T gamePacket = PacketManager.get(id);
		gamePacket.read(buffer);

		PlayerConnection connection;
		if (gamePacket instanceof ConnectPacket)
			connection = addConnection((ConnectPacket) gamePacket, address, port);
		else if (gamePacket instanceof DisconnectPacket)
			connection = removeConnection((DisconnectPacket) gamePacket);
		else
			connection = null; // TODO: search for player here!

		TriConsumer<ServerPacketListener, T, PlayerConnection> function = PacketManager.getServerListenerFunction(id);
		for (ServerPacketListener currentListener : listener)
			function.accept(currentListener, gamePacket, connection);
	}

	public void sendData(byte[] data, PlayerConnection connection) {
		sendData(data, connection.address, connection.port);
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
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

	public void sendToAllData(byte[] data) {
		for (PlayerConnection p : player)
			sendData(data, p.address, p.port);
	}

	public void sendToAllData(Packet packet) {
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
		player = new PlayerConnection(address, port, paket.getName());
		this.player.add(player);
		nameMap.put(player.name, player);
		return player;
	}

	private PlayerConnection removeConnection(DisconnectPacket paket) {
		PlayerConnection player = getPlayer(paket.getName());
		if (player == null) {
			Logger.warn("GameServer: Disconnect error! Player does not exist '" + paket.getName() + "'");
			return null;
		}
		player = nameMap.remove(paket.getName());
		this.player.remove(player);
		return player;
	}

	public List<PlayerConnection> getPlayer() {
		return Collections.unmodifiableList(player);
	}

	@Nullable
	public PlayerConnection getPlayer(String name) {
		return nameMap.get(name);
	}
}
