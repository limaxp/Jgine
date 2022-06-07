package org.jgine.net.game;

import java.net.InetAddress;

import org.jgine.misc.utils.IdGenerator;

public class PlayerConnection {

	private static final IdGenerator idGenerator = new IdGenerator();

	public final int id;
	public final InetAddress address;
	public final int port;
	public final String name;

	public PlayerConnection(InetAddress address, int port, String name) {
		this.id = idGenerator.generate();
		this.address = address;
		this.port = port;
		this.name = name;
	}

	void free() {
		idGenerator.free(id);
	}

	public boolean isAlive() {
		return idGenerator.isAlive(id);
	}
}
