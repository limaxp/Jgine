package org.jgine.core.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.bitSet.IntBitSet;
import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.TransformData;
import org.jgine.core.manager.SystemManager;
import org.jgine.net.game.ConnectionManager;
import org.jgine.net.game.GameServer;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.id.IdGenerator;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.Scheduler;

/**
 * A container for game entity data. Stores info about id, {@link Scene},
 * {@link Transform}, {@link Prefab}, used {@link EngineSystem}<code>s</code>,
 * the scene graph and a 32 bit flag. This is supposed to link all together in
 * an easy to use way.
 * 
 * <pre>
Currently used flags:

	0 - If entity is dead
 * </pre>
 */
public class Entity {

	public static final int MAX_ENTITIES = IdGenerator.MAX_ID - GameServer.MAX_ENTITIES - 1;
	public static final int MAX_OBJECTS_PER_SYSTEMS = 8;

	private static final IdGenerator ID_GENERATOR = new IdGenerator(1, MAX_ENTITIES + 1);
	private static final Entity[] ID_MAP = new Entity[IdGenerator.MAX_ID];

	public static final byte DEATH_FLAG = 0;

	private static int generateId() {
		int id;
		synchronized (ID_GENERATOR) {
			id = ID_GENERATOR.generate();
		}
		return id;
	}

	public static void freeId(int id) {
		int index = IdGenerator.index(id);
		if (index <= MAX_ENTITIES + 1) {
			synchronized (ID_GENERATOR) {
				ID_GENERATOR.free(id);
			}
		} else {
			ConnectionManager.freeEntityId(id);
		}
		ID_MAP[index] = null;
	}

	public static boolean isUsed(int id) {
		return ID_GENERATOR.isAlive(id);
	}

	public static boolean isFree(int id) {
		return !ID_GENERATOR.isAlive(id);
	}

	public static boolean isLocal(int id) {
		return IdGenerator.index(id) <= MAX_ENTITIES + 1;
	}

	public static boolean isRemote(int id) {
		return IdGenerator.index(id) > MAX_ENTITIES + 1;
	}

	public final int id;
	public final Scene scene;
	public final Transform transform;
	private final SystemMap systems;
	private Prefab prefab;
	private Entity parent;
	private List<Entity> childs;
	public int flag;
	public boolean flag2;

	public Entity(Scene scene) {
		this(scene, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	public Entity(Scene scene, TransformData transform) {
		this(scene, transform.posX, transform.posY, transform.posZ, transform.rotX, transform.rotY, transform.rotZ,
				transform.scaleX, transform.scaleY, transform.scaleZ);
	}

	public Entity(Scene scene, Vector2f position) {
		this(scene, position, Vector2f.NULL, Vector2f.FULL);
	}

	public Entity(Scene scene, Vector2f position, Vector2f rotation, Vector2f scale) {
		this(scene, position.x, position.y, 0.0f, rotation.x, rotation.y, 0.0f, scale.x, scale.y, 1.0f);
	}

	public Entity(Scene scene, float posX, float posY) {
		this(scene, posX, posY, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	public Entity(Scene scene, float posX, float posY, float rotX, float rotY, float scaleX, float scaleY) {
		this(scene, posX, posY, 0.0f, rotX, rotY, 0.0f, scaleX, scaleY, 1.0f);
	}

	public Entity(Scene scene, Vector3f position) {
		this(scene, position, Vector3f.NULL, Vector3f.FULL);
	}

	public Entity(Scene scene, Vector3f position, Vector3f rotation, Vector3f scale) {
		this(scene, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x, scale.y, scale.z);
	}

	public Entity(Scene scene, float posX, float posY, float posZ) {
		this(scene, posX, posY, posZ, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	public Entity(Scene scene, float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX,
			float scaleY, float scaleZ) {
		this(generateId(), scene, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
	}

	public Entity(int id, Scene scene, float posX, float posY, float posZ, float rotX, float rotY, float rotZ,
			float scaleX, float scaleY, float scaleZ) {
		this.id = id;
		this.scene = scene;
		systems = new SystemMap();
		childs = Collections.synchronizedList(new UnorderedIdentityArrayList<Entity>());
		transform = new Transform(this, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
		scene.addEntity(this);
		scene.addTopEntity(this);
		ID_MAP[IdGenerator.index(id)] = this;
	}

	public void delete() {
		scene.removeEntity(this);
	}

	public final boolean isLocal() {
		return isLocal(id);
	}

	public final boolean isRemote() {
		return isRemote(id);
	}

	public final boolean isAlive() {
		return !getFlag(DEATH_FLAG);
	}

	public final boolean isDeath() {
		return getFlag(DEATH_FLAG);
	}

	public final void markDeath() {
		setFlag(DEATH_FLAG, true);
	}

	public final void setFlag(int index, boolean bit) {
		IntBitSet.set(flag, index, bit);
	}

	public final boolean getFlag(int index) {
		return IntBitSet.get(flag, index);
	}

	public final <T extends SystemObject> T addSystem(String name, T object) {
		return addSystem(scene.getSystem(name), object);
	}

	public final <T extends SystemObject> T addSystem(Class<? extends EngineSystem> clazz, T object) {
		return addSystem(scene.getSystem(clazz), object);
	}

	public final <T extends SystemObject> T addSystem(int id, T object) {
		return addSystem(scene.getSystem(id), object);
	}

	public final <T extends SystemObject> T addSystem(EngineSystem system, T object) {
		return addSystem(scene.getSystem(system), object);
	}

	public final <T extends SystemObject> T addSystem(SystemScene<?, T> systemScene, T object) {
		synchronized (systems) {
			systems.add(systemScene, object);
		}
		systemScene.initObject(this, object);
		Scheduler.runTaskSynchron(() -> systems.setId(systemScene, object, systemScene.addObject(this, object)));
		return object;
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(String name, T... objects) {
		addSystem(scene.getSystem(SystemManager.get(name)), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(Class<? extends EngineSystem> clazz, T... objects) {
		addSystem(scene.getSystem(clazz), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(int id, T... objects) {
		addSystem(scene.getSystem(SystemManager.get(id)), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(EngineSystem system, T... objects) {
		addSystem(scene.getSystem(system), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(SystemScene<?, T> systemScene, T... objects) {
		synchronized (systems) {
			systems.add(systemScene, objects);
		}
		for (int i = 0; i < objects.length; i++)
			systemScene.initObject(this, objects[i]);
		Scheduler.runTaskSynchron(() -> {
			for (int i = 0; i < objects.length; i++) {
				T object = objects[i];
				systems.setId(systemScene, object, systemScene.addObject(this, object));
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(String name) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(SystemManager.get(name)));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(Class<? extends EngineSystem> clazz) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(clazz));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(int id) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(SystemManager.get(id)));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(EngineSystem system) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(system));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(SystemScene<?, T> systemScene) {
		T[] objects;
		int[] indexes;
		synchronized (systems) {
			objects = (T[]) systems.get(systemScene);
			indexes = systems.remove(systemScene);
		}
		Scheduler.runTaskSynchron(() -> {
			for (int i = 0; i < indexes.length; i++)
				systemScene.removeObject(indexes[i]);
		});
		return objects;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T removeSystem(String name, T object) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(SystemManager.get(name)), object);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T removeSystem(Class<? extends EngineSystem> clazz, T object) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(clazz), object);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T removeSystem(int id, T object) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(id), object);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T removeSystem(EngineSystem system, T object) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(system), object);
	}

	@Nullable
	public final <T extends SystemObject> T removeSystem(SystemScene<?, T> systemScene, T object) {
		int index;
		synchronized (systems) {
			index = systems.remove(systemScene, object);
		}
		Scheduler.runTaskSynchron(() -> systemScene.removeObject(index));
		return object;
	}

	public final <T extends SystemObject> void setSystemId(SystemScene<?, T> systemScene, T object, int objectId) {
		systems.setId(systemScene, object, objectId);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(String name) {
		return getSystem(scene.getSystem(SystemManager.get(name)), 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(Class<? extends EngineSystem> clazz) {
		return getSystem(scene.getSystem(clazz), 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(int id) {
		return getSystem(scene.getSystem(SystemManager.get(id)), 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(EngineSystem system) {
		return getSystem(scene.getSystem(system), 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(SystemScene<?, T> systemScene) {
		return getSystem(systemScene, 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(String name, int index) {
		return getSystem(scene.getSystem(SystemManager.get(name)), index);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(Class<? extends EngineSystem> clazz, int index) {
		return getSystem(scene.getSystem(clazz), index);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(int id, int index) {
		return getSystem(scene.getSystem(SystemManager.get(id)), index);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(EngineSystem system, int index) {
		return getSystem(scene.getSystem(system), index);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemObject> T getSystem(SystemScene<?, T> systemScene, int index) {
		if (systemScene == null)
			return null;
		synchronized (systems) {
			return (T) systems.get(systemScene, index);
		}
	}

	@Nullable
	public final SystemObject[] getSystems(String name) {
		return getSystems(scene.getSystem(SystemManager.get(name)));
	}

	@Nullable
	public final SystemObject[] getSystems(Class<? extends EngineSystem> clazz) {
		return getSystems(scene.getSystem(clazz));
	}

	@Nullable
	public final SystemObject[] getSystems(int id) {
		return getSystems(scene.getSystem(SystemManager.get(id)));
	}

	@Nullable
	public final SystemObject[] getSystems(EngineSystem system) {
		return getSystems(scene.getSystem(system));
	}

	@Nullable
	public final SystemObject[] getSystems(SystemScene<?, ?> systemScene) {
		if (systemScene == null)
			return null;
		synchronized (systems) {
			return systems.get(systemScene);
		}
	}

	public SystemMap getSystemMap() {
		return systems;
	}

	public final Iterator<SystemObject> getSystemIterator() {
		return systems.getSystemIterator();
	}

	public final Iterator<Integer> getIdIterator() {
		return systems.getIdIterator();
	}

	public final Iterator<Entry<SystemScene<?, ?>, SystemObject>> getSystemEntryIterator() {
		return systems.getSystemEntryIterator(scene);
	}

	public final Iterator<Entry<SystemScene<?, ?>, Integer>> getIdEntryIterator() {
		return systems.getIdEntryIterator(scene);
	}

	final void setPrefab(Prefab prefab) {
		this.prefab = prefab;
	}

	@Nullable
	public final Prefab getPrefab() {
		return prefab;
	}

	public final void setParent(@Nullable Entity parent) {
		if (parent == this.parent)
			return;
		if (this.parent != null)
			this.parent.childs.remove(this);
		else
			scene.removeTopEntity(this);
		if (parent != null)
			parent.childs.add(this);
		else
			scene.addTopEntity(this);
		this.parent = parent;
		updateChild();
	}

	@Nullable
	public final Entity getParent() {
		return parent;
	}

	public final boolean hasParent() {
		return parent != null;
	}

	public final void addChild(Entity child) {
		if (child.parent != null)
			child.parent.childs.remove(this);
		else
			scene.removeTopEntity(this);
		child.parent = this;
		childs.add(child);
		child.updateChild();
	}

	public final void removeChild(Entity child) {
		child.parent = null;
		childs.remove(child);
		scene.addTopEntity(this);
		child.updateChild();
	}

	public final void isChild(Entity child) {
		childs.contains(child);
	}

	public final void addChilds(Collection<Entity> childs) {
		for (Entity child : childs)
			addChild(child);
	}

	public final void removeChilds(Collection<Entity> childs) {
		for (Entity child : childs)
			removeChild(child);
	}

	public final void setChilds(Collection<Entity> childs) {
		if (!childs.isEmpty())
			clearChilds();
		for (Entity child : childs)
			addChild(child);
	}

	public final void clearChilds() {
		for (Entity child : childs) {
			child.parent = null;
			scene.addTopEntity(this);
			child.updateChild();
		}
		childs.clear();
	}

	public final void cleanupParent() {
		if (this.parent != null)
			this.parent.childs.remove(this);
		else
			scene.removeTopEntity(this);
	}

	public final void cleanupChilds() {
		childs.clear();
	}

	public final List<Entity> getChilds() {
		return Collections.unmodifiableList(childs);
	}

	private final void updateChild() {
		transform.calculateMatrix();
	}

	@Nullable
	public final Object getData(Object identifier) {
		if (prefab == null)
			return null;
		return prefab.getData(identifier);
	}

	public final long getTag() {
		if (prefab == null)
			return 0L;
		return prefab.getTagBits();
	}

	public final boolean getTag(String name) {
		if (prefab == null)
			return false;
		return prefab.getTag(name);
	}

	public final boolean getTag(int id) {
		if (prefab == null)
			return false;
		return prefab.getTag(id);
	}

	public final boolean getTag(String... names) {
		if (prefab == null)
			return false;
		return prefab.getTag(names);
	}

	public final boolean getTag(int... ids) {
		if (prefab == null)
			return false;
		return prefab.getTag(ids);
	}

	@SuppressWarnings("unchecked")
	public void load(DataInput in) throws IOException {
		int prefabId = in.readInt();
		if (prefabId != -1)
			prefab = Prefab.get(prefabId);
		transform.load(in);

		int systemSize = in.readInt();
		for (int i = 0; i < systemSize; i++) {
			@SuppressWarnings("rawtypes")
			SystemScene systemScene = scene.getSystem(in.readInt());
			addSystem(systemScene, systemScene.load(in));
		}

		int childSize = in.readInt();
		for (int i = 0; i < childSize; i++) {
			Entity entity = new Entity(scene);
			entity.load(in);
			addChild(entity);
		}
	}

	public void save(DataOutput out) throws IOException {
		if (prefab != null)
			out.writeInt(prefab.id);
		else
			out.writeInt(-1);
		transform.save(out);

		synchronized (systems) {
			out.writeInt(systems.size());
			Iterator<Entry<SystemScene<?, ?>, SystemObject>> entryIterator = getSystemEntryIterator();
			while (entryIterator.hasNext()) {
				Entry<SystemScene<?, ?>, SystemObject> entry = entryIterator.next();
				SystemScene<?, ?> systemScene = entry.getKey();
				out.writeInt(systemScene.system.getId());
				systemScene.save_(entry.getValue(), out);
			}
		}

		out.writeInt(childs.size());
		for (Entity child : childs)
			child.save(out);
	}

	@Nullable
	public static Entity getById(int id) {
		return ID_MAP[IdGenerator.index(id)];
	}
}
