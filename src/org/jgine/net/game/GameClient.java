package org.jgine.net.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.ClientPacketListener;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;

public class GameClient implements Runnable {

	private InetAddress serverIpAddress;
	private int serverPort;
	private DatagramSocket socket;
	private boolean isRunning;
	private List<ClientPacketListener> listener;
	private int id;
	private List<PlayerConnection> player;
	private Map<String, PlayerConnection> nameMap;
	private PlayerConnection[] idMap;

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
		listener = new ArrayList<ClientPacketListener>();
		id = -1;
		player = new ArrayList<PlayerConnection>(maxConnections);
		nameMap = new HashMap<String, PlayerConnection>(maxConnections);
		idMap = new PlayerConnection[maxConnections];
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
			for (PlayerConnection p : playerListPacket.getPlayers()) {
				player.add(p);
				nameMap.put(p.name, p);
				idMap[p.id] = p;
			}
		}

		BiConsumer<ClientPacketListener, T> function = PacketManager.getClientListenerFunction(paketId);
		for (ClientPacketListener currentListener : listener)
			function.accept(currentListener, gamePacket);
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

	public List<PlayerConnection> getPlayer() {
		return Collections.unmodifiableList(player);
	}

	@Nullable
	public PlayerConnection getPlayer(String name) {
		return nameMap.get(name);
	}

	@Nullable
	public PlayerConnection getPlayer(int id) {
		return idMap[id];
	}

	public int getMaxPlayer() {
		return idMap.length;
	}
}
