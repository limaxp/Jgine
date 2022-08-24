package org.jgine.core.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.TransformData;
import org.jgine.core.manager.SystemManager;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.collection.map.ConcurrentArrayHashMap;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.id.IdGenerator;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public class Entity {

	private static final IdGenerator ID_GENERATOR = new IdGenerator();
	private static final Entity[] ID_MAP = new Entity[ID_GENERATOR.getMaxId()];

	public final int id;
	public final Scene scene;
	public final Transform transform;
	private final ConcurrentArrayHashMap<SystemScene<?, ?>, SystemObject> systems;
	private Prefab prefab;
	private Entity parent;
	private List<Entity> childs;

	public Entity(Scene scene) {
		this(scene, Vector3f.NULL, Vector3f.NULL, Vector3f.FULL);
	}

	public Entity(Scene scene, TransformData transform) {
		this(scene, transform.posX, transform.posY, transform.posZ, transform.rotX, transform.rotY, transform.rotZ,
				transform.scaleX, transform.scaleY, transform.scaleZ);
	}

	public Entity(Scene scene, Vector2f position) {
		this(scene, position, Vector2f.NULL, Vector2f.FULL);
	}

	public Entity(Scene scene, Vector2f position, Vector2f rotation, Vector2f scale) {
		this(scene, position.x, position.y, 0, rotation.x, rotation.y, 0, scale.x, scale.y, 0);
	}

	public Entity(Scene scene, float posX, float posY) {
		this(scene, posX, posY, 0, 0, 0, 0, 1, 1, 1);
	}

	public Entity(Scene scene, float posX, float posY, float rotX, float rotY, float scaleX, float scaleY) {
		this(scene, posX, posY, 0, rotX, rotY, 0, scaleX, scaleY, 0);
	}

	public Entity(Scene scene, Vector3f position) {
		this(scene, position, Vector3f.NULL, Vector3f.FULL);
	}

	public Entity(Scene scene, Vector3f position, Vector3f rotation, Vector3f scale) {
		this(scene, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x, scale.y, scale.z);
	}

	public Entity(Scene scene, float posX, float posY, float posZ) {
		this(scene, posX, posY, posZ, 0, 0, 0, 1, 1, 1);
	}

	public Entity(Scene scene, float posX, float posY, float posZ, float rotX, float rotY, float rotZ, float scaleX,
			float scaleY, float scaleZ) {
		synchronized (ID_GENERATOR) {
			this.id = ID_GENERATOR.generate();
		}
		ID_MAP[IdGenerator.index(id)] = this;
		this.scene = scene;
		systems = new ConcurrentArrayHashMap<SystemScene<?, ?>, SystemObject>();
		childs = Collections.synchronizedList(new UnorderedIdentityArrayList<Entity>());
		transform = new Transform(this, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
		scene.addEntity(this);
	}

	public void delete() {
		if (!isAlive())
			return;
		scene.removeEntity(this);
		int idIndex;
		synchronized (ID_GENERATOR) {
			idIndex = ID_GENERATOR.free(id);
		}
		ID_MAP[idIndex] = null;
	}

	public final boolean isAlive() {
		return ID_GENERATOR.isAlive(id);
	}

	public final <T extends SystemObject> T addSystem(String system, T object) {
		return addSystem(scene.getSystem(SystemManager.get(system)), object);
	}

	public final <T extends SystemObject> T addSystem(Class<? extends EngineSystem> clazz, T object) {
		return addSystem(scene.getSystem(clazz), object);
	}

	public final <T extends SystemObject> T addSystem(EngineSystem system, T object) {
		return addSystem(scene.getSystem(system), object);
	}

	public final <T extends SystemObject> T addSystem(SystemScene<?, T> systemScene, T object) {
		systems.add(systemScene, object);
		systemScene.initObject(this, object);
		Scheduler.runTaskSynchron(() -> systemScene.addObject(this, object));
		return object;
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(String system, T... objects) {
		addSystem(scene.getSystem(SystemManager.get(system)), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(Class<? extends EngineSystem> clazz, T... objects) {
		addSystem(scene.getSystem(clazz), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(EngineSystem system, T... objects) {
		addSystem(scene.getSystem(system), objects);
	}

	@SafeVarargs
	public final <T extends SystemObject> void addSystem(SystemScene<?, T> systemScene, T... objects) {
		systems.add(systemScene, objects);
		for (int i = 0; i < objects.length; i++)
			systemScene.initObject(this, objects[i]);
		Scheduler.runTaskSynchron(() -> {
			for (int i = 0; i < objects.length; i++)
				systemScene.addObject(this, objects[i]);
		});
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(String system) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(SystemManager.get(system)));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(Class<? extends EngineSystem> clazz) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(clazz));
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public final <T extends SystemObject> T[] removeSystem(EngineSystem system) {
		return removeSystem((SystemScene<?, T>) scene.getSystem(system));
	}

	@Nullable
	public final <T extends SystemObject> T[] removeSystem(SystemScene<?, T> systemScene) {
		@SuppressWarnings("unchecked")
		T[] objects = (T[]) systems.remove(systemScene);
		Scheduler.runTaskSynchron(() -> {
			for (int i = 0; i < objects.length; i++)
				systemScene.removeObject(objects[i]);
		});
		return objects;
	}

	@Nullable
	public final <T extends SystemObject> SystemScene<?, T> removeSystem(T object) {
		@SuppressWarnings("unchecked")
		SystemScene<?, T> systemScene = (SystemScene<?, T>) systems.rem(object);
		Scheduler.runTaskSynchron(() -> systemScene.removeObject(object));
		return systemScene;
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(String system) {
		return getSystem(scene.getSystem(SystemManager.get(system)));
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(Class<? extends EngineSystem> clazz) {
		return getSystem(scene.getSystem(clazz));
	}

	@Nullable
	public final <T extends SystemObject> T getSystem(EngineSystem system) {
		return getSystem(scene.getSystem(system));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemObject> T getSystem(SystemScene<?, T> systemScene) {
		return (T) systems.get(systemScene, 0);
	}

	@Nullable
	public final <T extends SystemObject> T[] getSystems(String system) {
		return getSystems(scene.getSystem(SystemManager.get(system)));
	}

	@Nullable
	public final <T extends SystemObject> T[] getSystems(Class<? extends EngineSystem> clazz) {
		return getSystems(scene.getSystem(clazz));
	}

	@Nullable
	public final <T extends SystemObject> T[] getSystems(EngineSystem system) {
		return getSystems(scene.getSystem(system));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemObject> T[] getSystems(SystemScene<?, T> systemScene) {
		return (T[]) systems.get(systemScene);
	}

	public final Collection<SystemScene<?, ?>> getSystemScenes() {
		return systems.keySet();
	}

	public final Collection<SystemObject[]> getSystems() {
		return systems.values();
	}

	public final Set<Entry<SystemScene<?, ?>, SystemObject[]>> getSystemEntries() {
		return systems.entrySet();
	}

	final void setPrefab(Prefab prefab) {
		this.prefab = prefab;
	}

	@Nullable
	public final Prefab getPrefab() {
		return prefab;
	}

	public final void setParent(@Nullable Entity parent) {
		if (this.parent != null)
			this.parent.childs.remove(this);
		this.parent = parent;
		if (parent != null)
			parent.childs.add(this);
		updateChild();
	}

	@Nullable
	public final Entity getParent() {
		return parent;
	}

	public final boolean hasParent() {
		return parent != null;
	}

	public final boolean isParent(Entity parent) {
		return this.parent != parent;
	}

	public final void addChild(Entity child) {
		if (child.parent != null)
			child.parent.childs.remove(this);
		child.parent = this;
		childs.add(child);
		child.updateChild();
	}

	public final void removeChild(Entity child) {
		child.parent = null;
		childs.remove(child);
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

	public final void clearChilds() {
		for (Entity child : childs) {
			child.parent = null;
			child.updateChild();
		}
		childs.clear();
	}

	public final void setChilds(Collection<Entity> childs) {
		if (!childs.isEmpty())
			clearChilds();
		for (Entity child : childs)
			addChild(child);
	}

	public final List<Entity> getChilds() {
		return Collections.unmodifiableList(childs);
	}

	private final void updateChild() {
		transform.calculateMatrix();
	}

	@Nullable
	public static Entity getById(int id) {
		return ID_MAP[id];
	}
}
