package jgine.core;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import jgine.net.game.ConnectionManager;
import jgine.net.game.GameServer;
import jgine.utils.IdGenerator;

/**
 * Storage for {@link Entity}<code>s</code> with the following specification:
 * 
 * <pre>
 *- get(id) reflects additions immediately and will return old data until id index is recycled.
 * </pre>
 */
public class EntityStorage {

	public static final int MAX_ENTITIES = IdGenerator.MAX_CAPACITY - GameServer.MAX_ENTITIES;
	private static final IdGenerator ID_GENERATOR = new IdGenerator(IdGenerator.MAX_CAPACITY);
	private static final Entity[] ID_MAP = new Entity[IdGenerator.MAX_CAPACITY];
	private static final VarHandle ID_MAP_HANDLE = MethodHandles.arrayElementVarHandle(Entity[].class);

	static int add(Entity entity) {
		int id = ID_GENERATOR.generate();
		ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(id), entity);
		return id;
	}

	static void inject(Entity entity) {
		ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(entity.id), entity);
	}

	static void remove(Entity entity) {
		if (isLocal(entity.id))
			ID_GENERATOR.free(entity.id);
		else
			ConnectionManager.freeEntityId(entity.id);
	}

	public static boolean isAlive(int id) {
		return ID_GENERATOR.isAlive(id);
	}

	public static boolean isLocal(int id) {
		return IdGenerator.index(id) < MAX_ENTITIES;
	}

	public static boolean isRemote(int id) {
		return IdGenerator.index(id) >= MAX_ENTITIES;
	}

	public static Entity get(int id) {
		return ID_MAP[IdGenerator.index(id)];
	}
}
