package org.jgine.net.game;

import java.net.InetAddress;

public class PlayerConnection {

	public final InetAddress address;
	public final int port;
	public final String name;

	public PlayerConnection(InetAddress address, int port, String name) {
		this.address = address;
		this.port = port;
		this.name = name;
	}
}
