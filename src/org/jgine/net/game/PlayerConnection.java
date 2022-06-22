package org.jgine.net.game;

import java.net.InetAddress;

public class PlayerConnection {

	public final int id;
	public final InetAddress address;
	public final int port;
	public final String name;

	public PlayerConnection(InetAddress address, int port, String name, int id) {
		this.id = id;
		this.address = address;
		this.port = port;
		this.name = name;
	}

	@Override
	public String toString() {
		return super.toString() + " [name=" + name + ", id=" + id + ", address=" + address + ", port" + port + "]";
	}
}
