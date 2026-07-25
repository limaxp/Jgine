package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import org.eclipse.jdt.annotation.Nullable;

import jgine.net.game.ConnectionManager;
import jgine.net.game.GameServer;
import jgine.system.EngineSystem;
import jgine.system.SystemMap;
import jgine.system.SystemObject;
import jgine.system.SystemScene;
import jgine.system.transform.Transform;
import jgine.utils.Flag;
import jgine.utils.IdGenerator;
import jgine.utils.Tag;

/**
 * A container for game entity data.
 * 
 * <pre>
 * Stores:
 * - int id 
 * - {@link Scene}
 * - {@link Prefab}
 * - {@link EngineSystem}<code>s</code>
 * - {@link Transform}
 * - {@link Flag}
 * - {@link Tag}
 * </pre>
 * 
 * <pre>
 * Flags:
 *  0 - DELETED
 * </pre>
 */
public final class Entity extends SystemMap {

	public static final int MAX_ENTITIES = IdGenerator.MAX_CAPACITY - GameServer.MAX_ENTITIES;

	private static final VarHandle FLAG_HANDLE;
	private static final VarHandle TAG_HANDLE;

	static {
		try {
			FLAG_HANDLE = MethodHandles.lookup().findVarHandle(Entity.class, "flag", int.class);
			TAG_HANDLE = MethodHandles.lookup().findVarHandle(Entity.class, "tag", int.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public final int id;
	private int index = -1; // main thread only
	private Transform transform;
	private Prefab prefab = Prefab.NONE; // effectively final
	private volatile int flag;
	private volatile int tag;

	public Entity(Scene scene) {
		super(scene);
		this.id = EntityStorage.add(this);
		scene.addEntity(this);
	}

	public Entity(int id, Scene scene) {
		super(scene);
		this.id = id;
		EntityStorage.inject(this);
		scene.addEntity(this);
	}

	void free() {
		EntityStorage.remove(this);
	}

	public void delete() {
		if (!setFlag(0, true))
			return;

		free();
		if (transform != null)
			transform.forChilds((transform) -> transform.getEntity().delete());
		scene.removeEntity(this);
		scene.getCommandQueue().add(() -> forEach((system, id, _) -> scene.getSystem(system).remove(id)));
	}

	public boolean isLocal() {
		return isLocal(id);
	}

	public boolean isRemote() {
		return isRemote(id);
	}

	public boolean isAlive() {
		return !getFlag(0);
	}

	public boolean setFlag(int f, boolean value) {
		for (;;) {
			int flag = this.flag;
			if (Flag.get(flag, f) == value)
				return false;
			if (FLAG_HANDLE.compareAndSet(this, flag, Flag.set(flag, f, value)))
				return true;
		}
	}

	public boolean getFlag(int f) {
		return Flag.get(flag, f);
	}

	public boolean setTag(int t, boolean value) {
		for (;;) {
			int tag = this.tag;
			if (Tag.get(tag, t) == value)
				return false;
			if (TAG_HANDLE.compareAndSet(this, tag, Tag.set(tag, t, value)))
				return true;
		}
	}

	public boolean getTag(int t) {
		return Tag.get(tag, t);
	}

	public <T extends SystemObject> T add(int system, T object) {
		return add(scene.getSystem(system), object);
	}

	public <T extends SystemObject> T add(SystemScene<?, T> system, T object) {
		int index = map(system.id, object);
		system.onInit(this, object);
		scene.getCommandQueue().add(() -> intId(index, system.add(this, object)));
		return object;
	}

	public <T extends SystemObject> void remove(int system, T object) {
		remove(scene.getSystem(system), object);
	}

	public <T extends SystemObject> void remove(SystemScene<?, T> system, T object) {
		scene.getCommandQueue().add(() -> {
			int id = unmap(object);
			if (id != -1)
				system.remove(id);
		});
	}

	public <T extends SystemObject> void remove(int system, int id) {
		remove(scene.getSystem(system), id);
	}

	public <T extends SystemObject> void remove(SystemScene<?, T> system, int id) {
		scene.getCommandQueue().add(() -> {
			if (unmap(id))
				system.remove(id);
		});
	}

	public <T extends SystemObject> void remove(int system) {
		remove(scene.getSystem(system));
	}

	public <T extends SystemObject> void remove(SystemScene<?, T> system) {
		scene.getCommandQueue().add(() -> unmap(system.id, system::remove));
	}

	/**
	 * DO NOT CALL THIS EVER!
	 * 
	 * @param transform
	 */
	public void initTransform(Transform transform) {
		this.transform = transform;
	}

	@Nullable
	public Transform getTransform() {
		return transform;
	}

	void setPrefab(Prefab prefab) {
		this.prefab = prefab;
	}

	public Prefab getPrefab() {
		return prefab;
	}

	void setIndex(int index) {
		this.index = index;
	}

	int getIndex() {
		return index;
	}

	@Override
	public void load(DataInput in) throws IOException {
		flag = in.readInt();
		tag = in.readInt();
		prefab = Prefab.get(in.readInt());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(flag);
		out.writeInt(tag);
		out.writeInt(prefab.id);
	}

	public void loadMap(DataInput in) throws IOException {
		super.load(in);
		forEach((system, id, value) -> {
			SystemScene<?, ?> systemScene = scene.getSystem(system);
			systemScene.onInit_(this, value);
			systemScene.relink(id, this);
			systemScene.onAdd_(this, value);
		});
	}

	public void saveMap(DataOutput out) throws IOException {
		super.save(out);
	}

	@Override
	public String toString() {
		return "[id=" + id + ", prefab=" + prefab.name + ", scene=" + scene.name + ", systems=" + super.toString()
				+ "]";
	}

	public static boolean isAlive(int id) {
		return EntityStorage.isAlive(id);
	}

	@Nullable
	public static Entity getById(int id) {
		return EntityStorage.get(id);
	}

	public static boolean isLocal(int id) {
		return EntityStorage.isLocal(id);
	}

	public static boolean isRemote(int id) {
		return EntityStorage.isRemote(id);
	}

	/**
	 * Storage for {@link Entity}<code>s</code> with the following specification:
	 * 
	 * <pre>
	 *- get(id) reflects additions immediately and will return old data until id index is recycled.
	 * </pre>
	 */
	private static class EntityStorage {

		private static final IdGenerator ID_GENERATOR = new IdGenerator(IdGenerator.MAX_CAPACITY);
		private static final Entity[] ID_MAP = new Entity[IdGenerator.MAX_CAPACITY];
		private static final VarHandle ID_MAP_HANDLE = MethodHandles.arrayElementVarHandle(Entity[].class);

		private static int add(Entity entity) {
			int id = ID_GENERATOR.generate();
			ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(id), entity);
			return id;
		}

		private static void inject(Entity entity) {
			ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(entity.id), entity);
		}

		private static void remove(Entity entity) {
			if (isLocal(entity.id))
				ID_GENERATOR.free(entity.id);
			else
				ConnectionManager.freeEntityId(entity.id);
		}

		private static boolean isAlive(int id) {
			return ID_GENERATOR.isAlive(id);
		}

		private static Entity get(int id) {
			return (Entity) ID_MAP_HANDLE.getVolatile(ID_MAP, IdGenerator.index(id));
		}

		private static boolean isLocal(int id) {
			return IdGenerator.index(id) < MAX_ENTITIES;
		}

		private static boolean isRemote(int id) {
			return IdGenerator.index(id) >= MAX_ENTITIES;
		}
	}
}
