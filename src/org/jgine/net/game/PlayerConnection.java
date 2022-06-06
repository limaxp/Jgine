package org.jgine.net.game;

import java.net.InetAddress;

public class PlayerConnection {

	public final InetAddress address;
	public final int port;
	public final String name;
	public final int id;

	public PlayerConnection(InetAddress address, int port, String name, int id) {
		this.address = address;
		this.port = port;
		this.name = name;
		this.id = id;
	}
}
