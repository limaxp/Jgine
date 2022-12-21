package org.jgine.net.game;

import java.net.InetAddress;

public class PlayerConnection {

	public final int id;
	public final String name;
	public final InetAddress address;
	public final int port;

	public PlayerConnection(int id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
	}

	@Override
	public String toString() {
		return super.toString() + " [id=" + id + ", name=" + name + ", address=" + address + ", port" + port + "]";
	}
}
