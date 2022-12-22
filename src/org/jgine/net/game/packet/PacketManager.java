package org.jgine.net.game.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.jgine.misc.utils.function.TriConsumer;
import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.packets.ConnectPacket;
import org.jgine.net.game.packet.packets.ConnectResponsePacket;
import org.jgine.net.game.packet.packets.DisconnectPacket;
import org.jgine.net.game.packet.packets.EntityDeletePacket;
import org.jgine.net.game.packet.packets.PingPacket;
import org.jgine.net.game.packet.packets.PlayerListPacket;
import org.jgine.net.game.packet.packets.PositionPacket;
import org.jgine.net.game.packet.packets.EntitySpawnPacket;
import org.jgine.net.game.packet.packets.PrefabSpawnPacket;

public class PacketManager {

	public static final int MAX_PACKET_TYPES = 1024;

	private static final Map<Integer, Supplier<? extends Packet>> PACKET_MAP = new HashMap<Integer, Supplier<? extends Packet>>();
	@SuppressWarnings("unchecked")
	private static BiConsumer<ClientPacketListener, ? extends Packet>[] CLIENT_LISTENER_LINK = new BiConsumer[MAX_PACKET_TYPES];
	@SuppressWarnings("unchecked")
	private static TriConsumer<ServerPacketListener, ? extends Packet, PlayerConnection>[] SERVER_LISTENER_LINK = new TriConsumer[MAX_PACKET_TYPES];

	private static final Packet EMPTY_PACKET = new Packet() {

		@Override
		public void read(ByteBuffer buffer) {
		}

		@Override
		public void write(ByteBuffer buffer) {
			buffer.putInt(PacketManager.INVALID);
		}
	};

	public static final int INVALID = register(0, () -> EMPTY_PACKET, ClientPacketListener::onInvalid,
			ServerPacketListener::onInvalid);

	public static final int PING = register(1, PingPacket::new, ClientPacketListener::on, ServerPacketListener::on);

	public static final int CONNECT = register(2, ConnectPacket::new, ServerPacketListener::on);

	public static final int CONNECT_RESPONSE = register(3, ConnectResponsePacket::new, ClientPacketListener::on);

	public static final int DISCONNECT = register(4, DisconnectPacket::new, ServerPacketListener::on);

	public static final int PLAYER_LIST = register(5, PlayerListPacket::new, ClientPacketListener::on);

	public static final int POSITION = register(6, PositionPacket::new, ClientPacketListener::on,
			ServerPacketListener::on);

	public static final int PREFAB_SPAWN = register(7, PrefabSpawnPacket::new, ClientPacketListener::on,
			ServerPacketListener::on);

	public static final int ENTITY_SPAWN = register(8, EntitySpawnPacket::new, ClientPacketListener::on,
			ServerPacketListener::on);

	public static final int ENTITY_DELETE = register(8, EntityDeletePacket::new, ClientPacketListener::on,
			ServerPacketListener::on);

	public static <T extends Packet> int register(int id, Supplier<T> supplier,
			BiConsumer<ClientPacketListener, T> clientFunction) {
		return register(id, supplier, clientFunction, (cpl, t, p) -> {
		});
	}

	public static <T extends Packet> int register(int id, Supplier<T> supplier,
			TriConsumer<ServerPacketListener, T, PlayerConnection> serverFunction) {
		return register(id, supplier, (spl, t) -> {
		}, serverFunction);
	};

	public static <T extends Packet> int register(int id, Supplier<T> supplier,
			BiConsumer<ClientPacketListener, T> clientFunction,
			TriConsumer<ServerPacketListener, T, PlayerConnection> serverFunction) {
		PACKET_MAP.put(id, supplier);
		CLIENT_LISTENER_LINK[id] = clientFunction;
		SERVER_LISTENER_LINK[id] = serverFunction;
		return id;
	}

	public static void unregister(int id) {
		PACKET_MAP.remove(id);
		CLIENT_LISTENER_LINK[id] = null;
		SERVER_LISTENER_LINK[id] = null;
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
	public static <T extends Packet> BiConsumer<ClientPacketListener, T> getClientListenerFunction(int id) {
		return (BiConsumer<ClientPacketListener, T>) CLIENT_LISTENER_LINK[id];
	}

	@SuppressWarnings("unchecked")
	public static <T extends Packet> TriConsumer<ServerPacketListener, T, PlayerConnection> getServerListenerFunction(
			int id) {
		return (TriConsumer<ServerPacketListener, T, PlayerConnection>) SERVER_LISTENER_LINK[id];
	}
}
