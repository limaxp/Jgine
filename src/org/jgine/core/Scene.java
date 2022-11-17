package org.jgine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedArrayList;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.spacePartitioning.SpacePartitioning;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public class Scene {

	public final Engine engine;
	public final int id;
	public final String name;
	private final Map<EngineSystem, SystemScene<?, ?>> systemMap;
	private final Map<Class<? extends EngineSystem>, SystemScene<?, ?>> systemClassMap;
	private final List<SystemScene<?, ?>> systemList;
	private final List<Entity> entities;
	private final Map<Object, List<BiConsumer<Entity, Object>>> recieverMap;
	private UpdateOrder updateOrder;
	private UpdateOrder renderOrder;
	private SpacePartitioning<Entity> spacePartitioning;
	private boolean paused;

	Scene(Engine engine, String name) {
		this.engine = engine;
		this.id = name.hashCode();
		this.name = name;
		systemMap = new ConcurrentHashMap<EngineSystem, SystemScene<?, ?>>();
		systemClassMap = new ConcurrentHashMap<Class<? extends EngineSystem>, SystemScene<?, ?>>();
		systemList = new IdentityArrayList<SystemScene<?, ?>>();
		entities = new UnorderedIdentityArrayList<Entity>();
		recieverMap = new ConcurrentHashMap<Object, List<BiConsumer<Entity, Object>>>();
		paused = false;
	}

	final void free() {
		UpdateManager.unregister(this);
		for (Entity entity : entities)
			Entity.freeId(entity.id);
		entities.clear();
		for (SystemScene<?, ?> systemScene : systemList)
			systemScene.free();
		systemMap.clear();
		systemClassMap.clear();
		systemList.clear();
	}

	public final <T extends SystemScene<?, ?>> T addSystem(String name) {
		return addSystem(SystemManager.get(name));
	}

	public final <T extends SystemScene<?, ?>> T addSystem(Class<? extends EngineSystem> clazz) {
		return addSystem(SystemManager.get(clazz));
	}

	public final <T extends SystemScene<?, ?>> T addSystem(int id) {
		return addSystem(SystemManager.get(id));
	}

	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T addSystem(EngineSystem system) {
		SystemScene<?, ?> systemScene = system.createScene(this);
		systemMap.put(system, systemScene);
		systemClassMap.put(system.getClass(), systemScene);
		Scheduler.runTaskSynchron(() -> systemList.add(systemScene));
		return (T) systemScene;
	}

	public final <T extends SystemScene<?, ?>> T removeSystem(String name) {
		return removeSystem(SystemManager.get(name));
	}

	public final <T extends SystemScene<?, ?>> T removeSystem(Class<? extends EngineSystem> clazz) {
		return removeSystem(SystemManager.get(clazz));
	}

	public final <T extends SystemScene<?, ?>> T removeSystem(int id) {
		return removeSystem(SystemManager.get(id));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T removeSystem(EngineSystem system) {
		SystemScene<?, ?> systemScene = systemMap.remove(system);
		systemClassMap.remove(system.getClass());
		Scheduler.runTaskSynchron(() -> {
			systemList.remove(systemScene);
			systemScene.free();
		});
		return (T) systemScene;
	}

	public final void addSystems(String... names) {
		for (String name : names)
			addSystem(SystemManager.get(name));
	}

	@SafeVarargs
	public final void addSystems(Class<? extends EngineSystem>... classes) {
		for (Class<? extends EngineSystem> clazz : classes)
			addSystem(SystemManager.get(clazz));
	}

	public final void addSystems(int... ids) {
		for (int id : ids)
			addSystem(SystemManager.get(id));
	}

	public final void addSystems(EngineSystem... systems) {
		for (EngineSystem system : systems)
			addSystem(system);
	}

	public final void removeSystems(String... names) {
		for (String name : names)
			removeSystem(SystemManager.get(name));
	}

	@SafeVarargs
	public final void removeSystems(Class<? extends EngineSystem>... classes) {
		for (Class<? extends EngineSystem> clazz : classes)
			removeSystem(SystemManager.get(clazz));
	}

	public final void removeSystems(int... ids) {
		for (int id : ids)
			removeSystem(SystemManager.get(id));
	}

	public final void removeSystems(EngineSystem... systems) {
		for (EngineSystem system : systems)
			removeSystem(system);
	}

	public final void addSystems(Collection<EngineSystem> systems) {
		for (EngineSystem system : systems)
			addSystem(system);
	}

	public final void removeSystems(Collection<EngineSystem> systems) {
		for (EngineSystem system : systems)
			removeSystem(system);
	}

	public final Collection<EngineSystem> getEngineSystems() {
		return systemMap.keySet();
	}

	public final Collection<SystemScene<?, ?>> getSystems() {
		return systemList;
	}

	public final Set<Entry<EngineSystem, SystemScene<?, ?>>> getSystemEntries() {
		return systemMap.entrySet();
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(String name) {
		return (T) systemMap.get(SystemManager.get(name));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(Class<? extends EngineSystem> clazz) {
		return (T) systemClassMap.get(clazz);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(int id) {
		return (T) systemMap.get(SystemManager.get(id));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(EngineSystem system) {
		return (T) systemMap.get(system);
	}

	public final boolean hasSystem(String name) {
		return systemMap.containsKey(SystemManager.get(name));
	}

	public final boolean hasSystem(Class<? extends EngineSystem> clazz) {
		return systemMap.containsKey(SystemManager.get(clazz));
	}

	public final boolean hasSystem(int id) {
		return systemMap.containsKey(SystemManager.get(id));
	}

	public final boolean hasSystem(EngineSystem system) {
		return systemMap.containsKey(system);
	}

	public final void addEntity(Entity entity) {
		Scheduler.runTaskSynchron(() -> entities.add(entity));
	}

	public final void removeEntity(Entity entity) {
		if (entity.isAlive()) {
			Scheduler.runTaskSynchron(() -> removeEntityIntern(entity));
			Entity.freeId(entity.id);
		}
	}

	private final void removeEntityIntern(Entity entity) {
		entities.remove(entity);
		Iterator<Entry<SystemScene<?, ?>, SystemObject[]>> entryIterator = entity.getEntryIterator();
		while (entryIterator.hasNext()) {
			Entry<SystemScene<?, ?>, SystemObject[]> entry = entryIterator.next();
			SystemScene<?, ?> system = entry.getKey();
			SystemObject[] objects = entry.getValue();
			for (int i = 0; i < objects.length; i++)
				system.removeObject_(objects[i]);
		}

		for (Entity child : entity.getChilds()) {
			removeEntityIntern(child);
			Entity.freeId(child.id);
		}
		entity.setParent(null);
		entity.clearChilds();
		entity.transform.cleanupEntity();
	}

	public final List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public final Entity getEntity(float x, float y, Entity defaultValue) {
		return spacePartitioning.get(x, y, 0, defaultValue);
	}

	public final Entity getEntity(float x, float y, float z, Entity defaultValue) {
		return spacePartitioning.get(x, y, z, defaultValue);
	}

	public final List<Entity> getEntitiesNear(Vector2f pos, float range) {
		return getEntitiesNear(pos.x, pos.y, range);
	}

	public final List<Entity> getEntitiesNear(float x, float y, float range) {
		List<Entity> result = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			Vector2f entityPos = entity.transform.getPosition();
			if (Vector2f.distance(entityPos.x, entityPos.y, x, y) <= range)
				result.add(entity);
		}
		return result;
	}

	public final List<Entity> getEntitiesNear(Vector3f pos, float range) {
		return getEntitiesNear(pos.x, pos.y, pos.z, range);
	}

	public final List<Entity> getEntitiesNear(float x, float y, float z, float range) {
		List<Entity> result = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			Vector3f entityPos = entity.transform.getPosition();
			if (Vector3f.distance(entityPos.x, entityPos.y, entityPos.z, x, y, z) <= range)
				result.add(entity);
		}
		return result;
	}

	public final List<BiConsumer<Entity, Object>> getUpdateReciever(Object identifier) {
		List<BiConsumer<Entity, Object>> reciever = recieverMap.get(identifier);
		if (reciever == null)
			recieverMap.put(identifier, reciever = new UnorderedArrayList<BiConsumer<Entity, Object>>());
		return reciever;
	}

	public final Set<Object> getUpdateIdentifiers() {
		return recieverMap.keySet();
	}

	public void setUpdateOrder(@Nullable UpdateOrder updateOrder) {
		this.updateOrder = updateOrder;
	}

	@Nullable
	public UpdateOrder getUpdateOrder() {
		return updateOrder;
	}

	public boolean hasUpdateOrder() {
		return updateOrder != null;
	}

	public void setRenderOrder(@Nullable UpdateOrder renderOrder) {
		this.renderOrder = renderOrder;
	}

	@Nullable
	public UpdateOrder getRenderOrder() {
		return renderOrder;
	}

	public boolean hasRenderOrder() {
		return renderOrder != null;
	}

	public void setSpacePartitioning(SpacePartitioning<Entity> spacePartitioning) {
		this.spacePartitioning = spacePartitioning;

	}

	public SpacePartitioning<Entity> getSpacePartitioning() {
		return spacePartitioning;
	}

	public void pause(boolean pause) {
		this.paused = pause;
	}

	public boolean isPaused() {
		return paused;
	}
}
