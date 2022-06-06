package org.jgine.net.game.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.SpawnPrefabPacket;

public class PacketManager {

	public static final int MAX_PACKET_TYPES = 1024;

	private static final Map<Integer, Supplier<? extends Packet>> PACKET_MAP = new HashMap<Integer, Supplier<? extends Packet>>();
	@SuppressWarnings("unchecked")
	private static BiConsumer<PacketListener, ? extends Packet>[] LISTENER_LINK = new BiConsumer[MAX_PACKET_TYPES];

	private static final Packet EMPTY_PACKET = new Packet() {

		@Override
		public void read(ByteBuffer buffer) {
		}

		@Override
		public void write(ByteBuffer buffer) {
			buffer.putInt(PacketManager.INVALID);
		}
	};

	public static final int INVALID = register(0, () -> EMPTY_PACKET, PacketListener::onInvalid);

	public static final int CONNECT = register(1, ConnectPacket::new, PacketListener::on);

	public static final int DISCONNECT = register(2, DisconnectPacket::new, PacketListener::on);

	public static final int PING = register(3, PingPacket::new, PacketListener::on);

	public static final int POSITION = register(4, PositionPacket::new, PacketListener::on);

	public static final int SPAWN_PREFAB = register(5, SpawnPrefabPacket::new, PacketListener::on);

	public static <T extends Packet> int register(int id, Supplier<T> supplier,
			BiConsumer<PacketListener, T> function) {
		PACKET_MAP.put(id, supplier);
		LISTENER_LINK[id] = function;
		return id;
	}

	public static void unregister(int id) {
		PACKET_MAP.remove(id);
		LISTENER_LINK[id] = null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Packet> T get(int id) {
		Supplier<? extends Packet> supplier = PACKET_MAP.getOrDefault(id, () -> EMPTY_PACKET);
		if (supplier == null)
			return null;
		return (T) supplier.get();
	}

	public static int size() {
		return PACKET_MAP.size();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Packet> BiConsumer<PacketListener, T> getListenerFunction(int id) {
		return (BiConsumer<PacketListener, T>) LISTENER_LINK[id];
	}
}
