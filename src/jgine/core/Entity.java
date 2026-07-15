package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import jgine.net.game.ConnectionManager;
import jgine.net.game.GameServer;
import jgine.system.SystemMap;
import jgine.system.SystemObject;
import jgine.system.SystemScene;
import jgine.system.transform.Transform;
import jgine.utils.Flag;
import jgine.utils.IdGenerator;
import jgine.utils.scheduler.Scheduler;

/**
 * A container for game entity data. Stores info about id, {@link Scene},
 * {@link Transform}, {@link Prefab}, used {@link EngineSystem}<code>s</code>,
 * the scene graph and a 32 bit flag. This is supposed to link all together in
 * an easy to use way.
 * 
 * <pre>
Currently used flags:

	0 - dead
 * </pre>
 */
public final class Entity extends SystemMap {

	public static final int MAX_ENTITIES = IdGenerator.MAX_ID - GameServer.MAX_ENTITIES - 1;

	private static final IdGenerator ID_GENERATOR = new IdGenerator(1, MAX_ENTITIES + 1);
	private static final Entity[] ID_MAP = new Entity[IdGenerator.MAX_ID];

	public static boolean isAlive(int id) {
		return ID_GENERATOR.isAlive(id);
	}

	public static boolean isLocal(int id) {
		return IdGenerator.index(id) <= MAX_ENTITIES + 1;
	}

	public static boolean isRemote(int id) {
		return IdGenerator.index(id) > MAX_ENTITIES + 1;
	}

	public final int id;
	private Transform transform;
	private Prefab prefab;
	private int flag;

	public Entity(Scene scene) {
		int id;
		synchronized (ID_GENERATOR) {
			id = ID_GENERATOR.generate();
		}
		this(id, scene);
	}

	public Entity(int id, Scene scene) {
		super(scene);
		this.id = id;
		this.prefab = Prefab.NONE;
		ID_MAP[IdGenerator.index(id)] = this;
		Scheduler.runTask(() -> scene.addEntity(this));
	}

	void free() {
		int index = IdGenerator.index(id);
		if (index <= MAX_ENTITIES + 1)
			ID_GENERATOR.free(id);
		else
			ConnectionManager.freeEntityId(id);
		ID_MAP[index] = null;

		transform = null;
		clearMap();
	}

	public void delete() {
		if (isAlive()) {
			setFlag(Flag.DELETE, true);
			Scheduler.runTask(() -> {
				if (Entity.isAlive(id))
					subDelete(this);
			});
		}
	}

	private static void subDelete(Entity entity) {
		if (entity.transform != null)
			entity.transform.forChilds((transform) -> subDelete(transform.getEntity()));
		entity.scene.removeEntity(entity);
		entity.forEach((system, id, _) -> entity.scene.getSystem(system).remove(id));
		entity.free();
	}

	public boolean isLocal() {
		return isLocal(id);
	}

	public boolean isRemote() {
		return isRemote(id);
	}

	public boolean isAlive() {
		return !getFlag(Flag.DELETE);
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int index, boolean value) {
		flag = Flag.set(flag, index, value);
	}

	public boolean getFlag(int index) {
		return Flag.get(flag, index);
	}

	public <T extends SystemObject> T add(int system, T object) {
		return add(scene.getSystem(system), object);
	}

	public <T extends SystemObject> T add(SystemScene<?, T> system, T object) {
		system.onInit(this, object);
		int index = map(system.id, object);
		Scheduler.runTask(() -> intId(index, system.add(this, object)));
		return object;
	}

	public <T extends SystemObject> void remove(int system, T object) {
		remove(scene.getSystem(system), object);
	}

	public <T extends SystemObject> void remove(SystemScene<?, T> system, T object) {
		Scheduler.runTask(() -> {
			int id = unmap(object);
			if (id != -1)
				system.remove(id);
		});
	}

	public <T extends SystemObject> void remove(int system, int id) {
		remove(scene.getSystem(system), id);
	}

	public <T extends SystemObject> void remove(SystemScene<?, T> system, int id) {
		Scheduler.runTask(() -> {
			if (unmap(id))
				system.remove(id);
		});
	}

	public <T extends SystemObject> void remove(int system) {
		remove(scene.getSystem(system));
	}

	public <T extends SystemObject> void remove(SystemScene<?, T> system) {
		Scheduler.runTask(() -> unmap(system.id, system::remove));
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

	@Override
	public void load(DataInput in) throws IOException {
		prefab = Prefab.get(in.readInt());
		flag = in.readInt();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(prefab.id);
		out.writeInt(flag);
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

	@Nullable
	public static Entity getById(int id) {
		return ID_MAP[IdGenerator.index(id)];
	}
}
