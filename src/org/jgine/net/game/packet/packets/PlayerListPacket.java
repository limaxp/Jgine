package org.jgine.net.game.packet.packets;

import java.nio.ByteBuffer;
import java.util.List;

import org.jgine.collection.list.FastArrayList;
import org.jgine.net.game.ConnectionManager;
import org.jgine.net.game.PlayerConnection;
import org.jgine.net.game.packet.Packet;
import org.jgine.net.game.packet.PacketManager;

public class PlayerListPacket extends Packet {

	public static enum PlayerListAction {
		ADD, REMOVE
	}

	private PlayerListAction action;
	private List<PlayerConnection> players;

	public PlayerListPacket() {
	}

	public PlayerListPacket(PlayerListAction action, List<PlayerConnection> players) {
		this.action = action;
		this.players = players;
	}

	@Override
	public void read(ByteBuffer buffer) {
		action = PlayerListAction.values()[buffer.getInt()];
		int size = buffer.getInt();
		players = new FastArrayList<PlayerConnection>(size);
		for (int i = 0; i < size; i++) {
			int id = buffer.getInt();
			byte[] nameBytes = new byte[buffer.getInt()];
			buffer.get(nameBytes);
			PlayerConnection player = ConnectionManager.getClient().getPlayer(id);
			if(player == null) 
				player = new PlayerConnection(id, new String(nameBytes), null, -1);
			players.add(player);
		}
	}

	@Override
	public void write(ByteBuffer buffer) {
		buffer.putInt(PacketManager.PLAYER_LIST);
		buffer.putInt(action.ordinal());
		int size = players.size();
		buffer.putInt(size);
		for (int i = 0; i < size; i++) {
			PlayerConnection player = players.get(i);
			buffer.putInt(player.id);
			buffer.putInt(player.name.length());
			buffer.put(player.name.getBytes());
		}
	}

	public PlayerListAction getAction() {
		return action;
	}

	public List<PlayerConnection> getPlayers() {
		return players;
	}
}
