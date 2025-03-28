package org.jgine.core.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.TransformData;
import org.jgine.core.entity.SystemMap.SystemMapConsumer;
import org.jgine.net.game.ConnectionManager;
import org.jgine.net.game.GameServer;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;
import org.jgine.utils.collection.bitSet.IntBitSet;
import org.jgine.utils.collection.list.UnorderedIdentityArrayList;
import org.jgine.utils.id.IdGenerator;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

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

	public final <T extends SystemObject> T addSystem(int id, T object) {
		return addSystem(scene.getSystem(id), object);
	}

	public final <T extends SystemObject> T addSystem(EngineSystem<?, ?> system, T object) {
		return addSystem(scene.getSystem(system), object);
	}

	public final <T extends SystemObject> T addSystem(SystemScene<?, T> systemScene, T object) {
		systemScene.init(this, object);
		systems.add(systemScene.id, systemScene.add(this, object));
		return object;
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(int id, T... objects) {
		addSystem(scene.getSystem(id), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(EngineSystem<?, ?> system, T... objects) {
		addSystem(scene.getSystem(system), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(SystemScene<?, T> systemScene, T... objects) {
		for (int i = 0; i < objects.length; i++)
			systemScene.init(this, objects[i]);
		for (int i = 0; i < objects.length; i++) {
			T object = objects[i];
			systems.add(systemScene.id, systemScene.add(this, object));
		}
	}

	public final <T extends SystemObject> void removeSystem(int id) {
		removeSystem(scene.getSystem(id));
	}

	public final <T extends SystemObject> void removeSystem(EngineSystem<?, ?> system) {
		removeSystem(scene.getSystem(system));
	}

	public final <T extends SystemObject> void removeSystem(SystemScene<?, T> systemScene) {
		systems.forEach(systemScene.id, (i) -> systemScene.remove(i));
		systems.remove(systemScene.id);
	}

	public final <T extends SystemObject> void removeSystem(int id, T object) {
		removeSystem(scene.getSystem(id), object);
	}

	public final <T extends SystemObject> void removeSystem(EngineSystem<?, ?> system, T object) {
		removeSystem(scene.getSystem(system), object);
	}

	public final <T extends SystemObject> void removeSystem(SystemScene<?, T> systemScene, T object) {
		systemScene.remove(systems.remove(systemScene, object));
	}

	public final <T extends SystemObject> void removeSystem(int id, int objectId) {
		removeSystem(scene.getSystem(id), objectId);
	}

	public final <T extends SystemObject> void removeSystem(EngineSystem<?, ?> system, int objectId) {
		removeSystem(scene.getSystem(system), objectId);
	}

	public final <T extends SystemObject> void removeSystem(SystemScene<?, T> systemScene, int objectId) {
		systems.remove(systemScene.id, objectId);
		systemScene.remove(objectId);
	}

	public final <T extends SystemObject> void setSystemId(SystemScene<?, T> systemScene, int oldId, int newId) {
		systems.set(systemScene.id, oldId, newId);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(int id) {
		return getSystem(scene.getSystem(id), 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(EngineSystem<?, ?> system) {
		return getSystem(scene.getSystem(system), 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(SystemScene<?, T> systemScene) {
		return getSystem(systemScene, 0);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(int id, int index) {
		return getSystem(scene.getSystem(id), index);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(EngineSystem<?, ?> system, int index) {
		return getSystem(scene.getSystem(system), index);
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(SystemScene<?, T> systemScene, int index) {
		int id = systems.get(systemScene.id, index);
		if (id < 0)
			return null;
		return systemScene.get(id);
	}

	public final <T extends SystemObject> List<T> getSystems(int id) {
		return getSystems(scene.getSystem(id));
	}

	public final <T extends SystemObject> List<T> getSystems(EngineSystem<?, ?> system) {
		return getSystems(scene.getSystem(system));
	}

	public final <T extends SystemObject> List<T> getSystems(SystemScene<?, T> systemScene) {
		List<T> result = new ArrayList<T>(systems.size(systemScene.id));
		systems.forEach(systemScene.id, (index) -> result.add(systemScene.get(index)));
		return result;
	}

	public final <T extends SystemObject> void forSystems(int id, Consumer<T> func) {
		forSystems(scene.getSystem(id), func);
	}

	public final <T extends SystemObject> void forSystems(EngineSystem<?, ?> system, Consumer<T> func) {
		forSystems(scene.getSystem(system), func);
	}

	public final <T extends SystemObject> void forSystems(SystemScene<?, T> systemScene, Consumer<T> func) {
		systems.forEach(systemScene.id, (index) -> func.accept(systemScene.get(index)));
	}

	public final void forSystems(String name, IntConsumer func) {
		forSystems(EngineSystem.get(name).id, func);
	}

	public final void forSystems(int id, IntConsumer func) {
		systems.forEach(id, func);
	}

	public final void forSystems(EngineSystem<?, ?> system, IntConsumer func) {
		forSystems(system.id, func);
	}

	public final void forSystems(SystemScene<?, ?> systemScene, IntConsumer func) {
		forSystems(systemScene.id, func);
	}

	public final void forSystems(IntConsumer func) {
		systems.forEach(func);
	}

	public final void forSystems(SystemMapConsumer func) {
		systems.forEach(scene, func);
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

	public final String getName() {
		if (prefab == null)
			return "unnamed";
		return prefab.name;
	}

	public void load(DataInput in) throws IOException {
		int prefabId = in.readInt();
		if (prefabId != -1)
			prefab = Prefab.get(prefabId);
		flag = in.readInt();
		transform.load(in);
		systems.load(in, this);

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
		out.writeInt(flag);
		transform.save(out);
		systems.save(out);

		out.writeInt(childs.size());
		for (Entity child : childs)
			child.save(out);
	}

	@Nullable
	public static Entity getById(int id) {
		return ID_MAP[IdGenerator.index(id)];
	}

	@Override
	public String toString() {
		return super.toString() + "[id=" + id + ", prefab=" + (prefab == null ? "none" : prefab.name) + ", scene="
				+ scene.name + ", pos=" + transform.getPosition() + ", systems=" + systemsToString() + "]";
	}

	public String systemsToString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		systems.forEach(scene, (systemScene, i) -> {
			sb.append(systemScene.name);
			sb.append(',');
			sb.append(' ');
		});
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(']');
		return sb.toString();
	}
}
