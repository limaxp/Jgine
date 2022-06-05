package org.jgine.net;

import java.net.InetAddress;

public class Player {

	public final InetAddress address;
	public final int port;
	public final String name;

	public Player(InetAddress address, int port, String name) {
		this.address = address;
		this.port = port;
		this.name = name;
	}
}
