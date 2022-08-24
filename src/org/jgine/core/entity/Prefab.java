package org.jgine.core.entity;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.TransformData;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.Reflection;
import org.jgine.misc.utils.loader.PrefabLoader;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;

public class Prefab {

	public final String name;
	public final int id;
	public final TransformData transform;
	private final List<EngineSystem> systems;
	private final List<SystemObject> objects;
	private final Map<Object, Object> data;
	private final List<Prefab> childs;

	public Prefab(String name) {
		this.name = name;
		id = name.hashCode();
		systems = new IdentityArrayList<EngineSystem>();
		objects = new IdentityArrayList<SystemObject>();
		data = new HashMap<Object, Object>();
		childs = new UnorderedIdentityArrayList<Prefab>();
		transform = new TransformData();
	}

	public final void set(EngineSystem system, SystemObject object) {
		int index = systems.indexOf(system);
		if (index >= 0) {
			objects.set(index, object);
			return;
		}
		systems.add(system);
		objects.add(object);
	}

	@Nullable
	public final SystemObject remove(EngineSystem system) {
		int index = systems.indexOf(system);
		if (index >= 0) {
			systems.remove(index);
			return objects.remove(index);
		}
		return null;
	}

	@Nullable
	public final SystemObject get(EngineSystem system) {
		int index = systems.indexOf(system);
		if (index >= 0)
			return objects.get(index);
		return null;
	}

	public final boolean has(EngineSystem system) {
		return systems.contains(system);
	}

	public final List<EngineSystem> getSystems() {
		return Collections.unmodifiableList(systems);
	}

	public final List<SystemObject> getObjects() {
		return Collections.unmodifiableList(objects);
	}

	public final void addParent(Prefab parent) {
		List<EngineSystem> parentSystems = parent.systems;
		List<SystemObject> parentObjects = parent.objects;
		for (int i = 0; i < parentSystems.size(); i++)
			set(parentSystems.get(i), parentObjects.get(i).copy());
		setData(parent.data);
	}

	public final void removeParent(Prefab parent) {
		List<EngineSystem> parentSystems = parent.systems;
		for (int i = 0; i < parentSystems.size(); i++)
			remove(parentSystems.get(i));
		removeData(parent.data);
	}

	public final void addChild(Prefab child) {
		childs.add(child);
	}

	public final void removeChild(Prefab child) {
		childs.remove(child);
	}

	public final void isChild(Prefab child) {
		childs.contains(child);
	}

	public final void clearChilds() {
		childs.clear();
	}

	public final void clear() {
		systems.clear();
		objects.clear();
		data.clear();
		childs.clear();
	}

	@Nullable
	public final Object setData(Object identifier, Object data) {
		return this.data.put(identifier, data);
	}

	public final void setData(Map<? extends Object, ? extends Object> map) {
		this.data.putAll(map);
	}

	@Nullable
	public final Object removeData(Object identifier) {
		return data.remove(identifier);
	}

	public final void removeData(Map<? extends Object, ? extends Object> map) {
		for (Object identifier : map.keySet())
			data.remove(identifier);
	}

	@Nullable
	public final Object getData(Object identifier) {
		return data.get(identifier);
	}

	public final Entity create(Scene scene) {
		return create_(scene, new Entity(scene, transform));
	}

	public final Entity create(Scene scene, Vector2f position) {
		return create_(scene, new Entity(scene, position.x, position.y, 0, transform.rotX, transform.rotY,
				transform.rotZ, transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, Vector2f position, Vector2f rotation, Vector2f scale) {
		return create_(scene, new Entity(scene, position, rotation, scale));
	}

	public final Entity create(Scene scene, float posX, float posY) {
		return create_(scene, new Entity(scene, posX, posY, 0, transform.rotX, transform.rotY, transform.rotZ,
				transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, float posX, float posY, float rotX, float rotY, float scaleX,
			float scaleY) {
		return create_(scene, new Entity(scene, posX, posY, 0, rotX, rotY, 0, scaleX, scaleY, 0));
	}

	public final Entity create(Scene scene, Vector3f position) {
		return create_(scene, new Entity(scene, position.x, position.y, position.z, transform.rotX, transform.rotY,
				transform.rotZ, transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, Vector3f position, Vector3f rotation, Vector3f scale) {
		return create_(scene, new Entity(scene, position, rotation, scale));
	}

	public final Entity create(Scene scene, float posX, float posY, float posZ) {
		return create_(scene, new Entity(scene, posX, posY, posZ, transform.rotX, transform.rotY, transform.rotZ,
				transform.scaleX, transform.scaleY, transform.scaleZ));
	}

	public final Entity create(Scene scene, float posX, float posY, float posZ, float rotX, float rotY, float rotZ,
			float scaleX, float scaleY, float scaleZ) {
		return create_(scene, new Entity(scene, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ));
	}

	private final Entity create_(Scene scene, Entity entity) {
		entity.setPrefab(this);
		for (int i = 0; i < systems.size(); i++)
			entity.addSystem(systems.get(i), objects.get(i).copy());
		for (Prefab child : childs)
			child.create(scene).setParent(entity);
		return entity;
	}

	public final <T extends Entity> T create(Scene scene, T entity) {
		entity.setPrefab(this);
		for (int i = 0; i < systems.size(); i++)
			entity.addSystem(systems.get(i), objects.get(i).copy());
		if (!childs.isEmpty()) {
			Constructor<? extends Entity> constructor = Reflection.getDeclaredConstructor(entity.getClass(),
					new Class[] { Scene.class });
			for (Prefab child : childs)
				child.create(scene, Reflection.newInstance(constructor, scene)).setParent(entity);
		}
		return entity;
	}

	@Nullable
	public static Prefab get(String name) {
		return PrefabLoader.get(name);
	}

	@Nullable
	public static Prefab get(int id) {
		return PrefabLoader.get(id);
	}
}
