package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;
import java.util.List;

import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class PlayerListPacket extends Packet {

	private List<PlayerConnection> players;

	public PlayerListPacket() {
	}

	public PlayerListPacket(List<PlayerConnection> players) {
		this.players = players;
	}

	@Override
	public void read(ByteBuffer buffer) {
		int size = buffer.getInt();
		players = new FastArrayList<PlayerConnection>(size);
		for (int i = 0; i < size; i++) {
			int id = buffer.getInt();
			byte[] nameBytes = new byte[buffer.getInt()];
			buffer.get(nameBytes);
			players.add(new PlayerConnection(null, -1, new String(nameBytes), id));
		}
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.PLAYER_LIST);
		int size = players.size();
		buffer.putInt(size);
		for (int i = 0; i < size; i++) {
			PlayerConnection player = players.get(i);
			buffer.putInt(player.id);
			buffer.putInt(player.name.length());
			buffer.put(player.name.getBytes());
		}
	}

	public List<PlayerConnection> getPlayers() {
		return players;
	}
}
