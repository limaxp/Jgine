package org.jgine.net;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jgine.net.packet.ConnectPacket;
import org.jgine.net.packet.DisconnectPacket;

public class PacketManager {

	private static final Map<Integer, Supplier<Packet>> PACKET_MAP = new HashMap<Integer, Supplier<Packet>>();

	private static final Packet EMPTY_PACKET = new Packet() {

		@Override
		public void read(ByteBuffer buffer) {
		}

		@Override
		public void write(ByteBuffer buffer) {
			buffer.putInt(0);
		}
	};

	public static final int INVALID = register(0, () -> EMPTY_PACKET);

	public static final int CONNECT = register(1, ConnectPacket::new);

	public static final int DISCONNECT = register(2, DisconnectPacket::new);

	public static int register(int id, Supplier<Packet> supplier) {
		PACKET_MAP.put(id, supplier);
		return id;
	}

	public static void unregister(int id) {
		PACKET_MAP.remove(id);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Packet> T get(int id) {
		Supplier<Packet> supplier = PACKET_MAP.getOrDefault(id, () -> EMPTY_PACKET);
		if (supplier == null)
			return null;
		return (T) supplier.get();
	}
}
