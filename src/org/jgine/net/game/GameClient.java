package org.jgine.net.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class GameClient implements Runnable {

	private InetAddress serverIpAddress;
	private DatagramSocket socket;
	private boolean isRunning;

	public GameClient(String serverIpAddress) {
		try {
			this.serverIpAddress = InetAddress.getByName(serverIpAddress);
			this.socket = new DatagramSocket();
			this.socket.setSoTimeout(1000);
		} catch (UnknownHostException e) {
			Logger.err("GameClient: Error server address is unknown!", e);
		} catch (SocketException e) {
			Logger.err("GameClient: Error creating socket!", e);
		}
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
			parsePacket(data, packet.getAddress(), packet.getPort());
		}
		socket.close();
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int id = buffer.getInt();
		Packet gamePacket = PacketManager.get(id);
		gamePacket.read(buffer);
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIpAddress, 1331);
		try {
			socket.send(packet);
		} catch (IOException e) {
			Logger.err("GameClient: Error sending data!", e);
		}
	}

	public InetAddress getServerIp() {
		return serverIpAddress;
	}

	public InetAddress getIp() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getPort();
	}
}
