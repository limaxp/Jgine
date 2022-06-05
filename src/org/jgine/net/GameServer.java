package org.jgine.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.packet.ConnectPacket;
import org.jgine.net.packet.DisconnectPacket;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private List<Player> player;
	private Map<String, Player> nameMap;

	public GameServer() {
		try {
			this.socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			Logger.err("Server: Error creating socket!", e);
		}
		player = new ArrayList<Player>();
		nameMap = new HashMap<String, Player>();
	}

	public void close() {
		socket.close();
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				Logger.err("Server: Error listening for packets!", e);
			}
			parsePacket(data, packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int id = buffer.getInt();
		Packet gamePacket = PacketManager.get(id);
		gamePacket.read(buffer);
		if (gamePacket instanceof ConnectPacket)
			addConnection((ConnectPacket) gamePacket, address, port);
		else if (gamePacket instanceof DisconnectPacket)
			removeConnection((DisconnectPacket) gamePacket);
	}

	private void addConnection(ConnectPacket paket, InetAddress address, int port) {
		Player player = getPlayer(paket.getName());
		if (player != null) {
			Logger.warn("Server: Connect error! Duplicate player name '" + paket.getName() + "'");
			return;
		}
		player = new Player(address, port, paket.getName());
		this.player.add(player);
		nameMap.put(player.name, player);
	}

	private void removeConnection(DisconnectPacket paket) {
		Player player = getPlayer(paket.getName());
		if (player == null) {
			Logger.warn("Server: Disconnect error! Player does not exist '" + paket.getName() + "'");
			return;
		}
		player = nameMap.remove(paket.getName());
		this.player.remove(player);
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			Logger.err("Server: Error sending data!", e);
		}
	}

	public void sendToAllData(byte[] data) {
		for (Player p : player)
			sendData(data, p.address, p.port);
	}

	public InetAddress getIp() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}

	public List<Player> getPlayer() {
		return Collections.unmodifiableList(player);
	}

	@Nullable
	public Player getPlayer(String name) {
		return nameMap.get(name);
	}
}
