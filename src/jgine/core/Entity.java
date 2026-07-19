package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import org.eclipse.jdt.annotation.Nullable;

import jgine.system.EngineSystem;
import jgine.system.SystemMap;
import jgine.system.SystemObject;
import jgine.system.SystemScene;
import jgine.system.transform.Transform;
import jgine.utils.Flag;

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
 * </pre>
 */
public final class Entity extends SystemMap {

	private static final VarHandle FLAG_HANDLE;

	static {
		try {
			FLAG_HANDLE = MethodHandles.lookup().findVarHandle(Entity.class, "flag", int.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public final int id;
	int index = -1; // main thread only
	private Transform transform;
	private Prefab prefab = Prefab.NONE; // effectively final
	private volatile int flag;

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
		if (!setFlag(Flag.DELETE, true))
			return;

		free();
		if (transform != null)
			transform.forChilds((transform) -> transform.getEntity().delete());
		scene.removeEntity(this);
		scene.getCommandQueue().add(() -> forEach((system, id, _) -> scene.getSystem(system).remove(id)));
	}

	public boolean isLocal() {
		return EntityStorage.isLocal(id);
	}

	public boolean isRemote() {
		return EntityStorage.isRemote(id);
	}

	public boolean isAlive() {
		return !getFlag(Flag.DELETE);
	}

	public boolean setFlag(int index, boolean value) {
		for (;;) {
			int flag = this.flag;
			if (Flag.get(flag, index) == value)
				return false;
			if (FLAG_HANDLE.compareAndSet(this, flag, Flag.set(flag, index, value)))
				return true;
		}
	}

	public boolean getFlag(int index) {
		return Flag.get(flag, index);
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

	@Override
	public void load(DataInput in) throws IOException {
		flag = in.readInt();
		prefab = Prefab.get(in.readInt());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(flag);
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

	@Nullable
	public static Entity getById(int id) {
		return EntityStorage.get(id);
	}
}
