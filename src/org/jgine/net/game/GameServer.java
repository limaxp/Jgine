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
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;

public class GameServer implements Runnable {

	private DatagramSocket socket;
	private List<Player> player;
	private Map<String, Player> nameMap;
	private boolean isRunning;

	public GameServer() {
		try {
			this.socket = new DatagramSocket(1331);
			this.socket.setSoTimeout(1000);
		} catch (SocketException e) {
			Logger.err("GameServer: Error creating socket!", e);
		}
		player = new ArrayList<Player>();
		nameMap = new HashMap<String, Player>();
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
			Logger.warn("GameServer: Connect error! Duplicate player name '" + paket.getName() + "'");
			return;
		}
		player = new Player(address, port, paket.getName());
		this.player.add(player);
		nameMap.put(player.name, player);
	}

	private void removeConnection(DisconnectPacket paket) {
		Player player = getPlayer(paket.getName());
		if (player == null) {
			Logger.warn("GameServer: Disconnect error! Player does not exist '" + paket.getName() + "'");
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
			Logger.err("GameServer: Error sending data!", e);
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
