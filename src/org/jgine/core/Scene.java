package org.jgine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemObject;
import org.jgine.system.SystemScene;

public class Scene {

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

	Scene(String name) {
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
		entities.clear();
		for (SystemScene<?, ?> systemScene : systemList)
			systemScene.free();
		systemMap.clear();
		systemClassMap.clear();
		systemList.clear();
	}

	public final <T extends SystemScene<?, ?>> T addSystem(String system) {
		return addSystem(SystemManager.get(system));
	}

	public final <T extends SystemScene<?, ?>> T addSystem(Class<? extends EngineSystem> clazz) {
		return addSystem(SystemManager.get(clazz));
	}

	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T addSystem(EngineSystem system) {
		SystemScene<?, ?> systemScene = system.createScene(this);
		systemMap.put(system, systemScene);
		if (!systemClassMap.containsKey(system.getClass()))
			systemClassMap.put(system.getClass(), systemScene);
		Scheduler.runTaskSynchron(() -> systemList.add(systemScene));
		return (T) systemScene;
	}

	public final <T extends SystemScene<?, ?>> T removeSystem(String system) {
		return removeSystem(SystemManager.get(system));
	}

	public final <T extends SystemScene<?, ?>> T removeSystem(Class<? extends EngineSystem> clazz) {
		return removeSystem(SystemManager.get(clazz));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T removeSystem(EngineSystem system) {
		SystemScene<?, ?> systemScene = systemMap.remove(system);
		removeFromClassMap(system, systemScene);
		Scheduler.runTaskSynchron(() -> {
			systemList.remove(systemScene);
			systemScene.free();
		});
		return (T) systemScene;
	}

	private final void removeFromClassMap(EngineSystem system, SystemScene<?, ?> systemScene) {
		Class<? extends EngineSystem> clazz = system.getClass();
		if (systemClassMap.get(clazz) != systemScene)
			return;
		systemClassMap.remove(clazz);
		for (SystemScene<?, ?> activeSystemScene : getSystems()) {
			if (activeSystemScene.system.getClass() == clazz) {
				systemClassMap.put(clazz, activeSystemScene);
				break;
			}
		}
	}

	public final void addSystems(String... systems) {
		for (String system : systems)
			addSystem(SystemManager.get(system));
	}

	@SafeVarargs
	public final void addSystems(Class<? extends EngineSystem>... classes) {
		for (Class<? extends EngineSystem> clazz : classes)
			addSystem(SystemManager.get(clazz));
	}

	public final void addSystems(EngineSystem... systems) {
		for (EngineSystem system : systems)
			addSystem(system);
	}

	public final void removeSystems(String... systems) {
		for (String system : systems)
			removeSystem(SystemManager.get(system));
	}

	@SafeVarargs
	public final void removeSystems(Class<? extends EngineSystem>... classes) {
		for (Class<? extends EngineSystem> clazz : classes)
			removeSystem(SystemManager.get(clazz));
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
	public final <T extends SystemScene<?, ?>> T getSystem(String system) {
		return (T) systemMap.get(SystemManager.get(system));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(Class<? extends EngineSystem> clazz) {
		return (T) systemClassMap.get(clazz);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(EngineSystem system) {
		return (T) systemMap.get(system);
	}

	public final boolean hasSystem(String system) {
		return systemMap.containsKey(SystemManager.get(system));
	}

	public final boolean hasSystem(Class<? extends EngineSystem> clazz) {
		return systemMap.containsKey(SystemManager.get(clazz));
	}

	public final boolean hasSystem(EngineSystem system) {
		return systemMap.containsKey(system);
	}

	public final void addEntity(Entity entity) {
		Scheduler.runTaskSynchron(() -> entities.add(entity));
	}

	public final void removeEntity(Entity entity) {
		Scheduler.runTaskSynchron(() -> removeEntityIntern(entity));
	}

	private final void removeEntityIntern(Entity entity) {
		entities.remove(entity);
		for (Entry<SystemScene<?, ?>, SystemObject[]> entry : entity.getSystemEntries()) {
			SystemScene<?, ?> system = entry.getKey();
			SystemObject[] objects = entry.getValue();
			for (int i = 0; i < objects.length; i++)
				system.removeObject_(objects[i]);
		}
		for (Entity child : entity.getChilds())
			removeEntityIntern(child);
		entity.setParent(null);
		entity.clearChilds();
		entity.transform.cleanupEntity();
	}

	public final List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public final Entity getEntity(float x, float y, float z, Entity defaultValue) {
		return spacePartitioning.get(x, y, z, defaultValue);
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
