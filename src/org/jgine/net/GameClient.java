package org.jgine.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.jgine.misc.utils.logger.Logger;

public class GameClient extends Thread {

	private InetAddress serverIpAddress;
	private DatagramSocket socket;

	public GameClient(String serverIpAddress) {
		try {
			this.serverIpAddress = InetAddress.getByName(serverIpAddress);
			this.socket = new DatagramSocket();
		} catch (UnknownHostException e) {
			Logger.err("Client: Error server address is unknown!", e);
		} catch (SocketException e) {
			Logger.err("Client: Error creating socket!", e);
		}
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
				Logger.err("Client: Error listening for packets!", e);
			}
			parsePacket(data, packet.getAddress(), packet.getPort());
		}
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
			Logger.err("Server: Error sending data!", e);
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
