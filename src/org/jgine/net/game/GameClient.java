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
import java.util.List;
import java.util.function.BiConsumer;

import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.ClientPacketListener;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;

public class GameClient implements Runnable {

	private InetAddress serverIpAddress;
	private int serverPort;
	private DatagramSocket socket;
	private boolean isRunning;
	private List<ClientPacketListener> listener;
	private int id;

	public GameClient(String serverIpAddress, int serverPort) {
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

		if (gamePacket instanceof ConnectResponsePacket) {
			ConnectResponsePacket connectResponse = (ConnectResponsePacket) gamePacket;
			if (connectResponse.isAccepted())
				this.id = connectResponse.getId();
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
}
