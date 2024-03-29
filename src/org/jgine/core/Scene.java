package org.jgine.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;
import org.jgine.utils.math.spacePartitioning.SceneSpacePartitioning;
import org.jgine.utils.math.spacePartitioning.SpacePartitioning;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.Scheduler;

/**
 * A scene or world identified with id and name. Use this class to create a
 * {@link Entity}. Scenes store a list of entities, a list of top level entities
 * and the {@link EngineSystem}<code>s</code> they use. They also provide a
 * pause capability.
 * <p>
 * Change the update and render order of the given systems by setting a
 * {@link UpdateOrder}. Set a {@link SpacePartitioning} to optimize location
 * checks.
 */
public class Scene {

	public final Engine engine;
	public final int id;
	public final String name;
	private final Map<EngineSystem, SystemScene<?, ?>> systemMap;
	private final Map<Class<? extends EngineSystem>, SystemScene<?, ?>> systemClassMap;
	private final List<SystemScene<?, ?>> systemList;
	private final List<Entity> entities;
	private final List<Entity> topEntities;
	private UpdateOrder updateOrder;
	private UpdateOrder renderOrder;
	private SpacePartitioning spacePartitioning;
	private boolean paused;

	Scene(Engine engine, String name) {
		this.engine = engine;
		this.id = name.hashCode();
		this.name = name;
		systemMap = new ConcurrentHashMap<EngineSystem, SystemScene<?, ?>>();
		systemClassMap = new ConcurrentHashMap<Class<? extends EngineSystem>, SystemScene<?, ?>>();
		systemList = new IdentityArrayList<SystemScene<?, ?>>();
		entities = new UnorderedIdentityArrayList<Entity>();
		topEntities = Collections.synchronizedList(new UnorderedIdentityArrayList<Entity>());
		spacePartitioning = new SceneSpacePartitioning(this);
		paused = false;
	}

	final void free() {
		for (Entity entity : entities)
			Entity.freeId(entity.id);
		entities.clear();
		topEntities.clear();
		for (SystemScene<?, ?> systemScene : systemList)
			systemScene.free();
		systemMap.clear();
		systemClassMap.clear();
		systemList.clear();
	}

	public final void delete() {
		engine.deleteScene(this);
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
		Scheduler.runTaskSynchron(() -> addEntityIntern(entity));
	}

	public final void removeEntity(Entity entity) {
		if (entity.isAlive()) {
			entity.markDeath();
			Scheduler.runTaskSynchron(() -> {
				if (Entity.isUsed(entity.id)) {
					removeEntityIntern(entity);
					Entity.freeId(entity.id);
				}
			});
		}
	}

	private final void addEntityIntern(Entity entity) {
		entities.add(entity);
		entity.transform.spacePartitioning = spacePartitioning;
		spacePartitioning.add(entity);
	}

	private final void removeEntityIntern(Entity entity) {
		removeChildIntern(entity);
		entity.cleanupParent();
	}

	private final void removeChildIntern(Entity entity) {
		entities.remove(entity);
		Iterator<Entry<SystemScene<?, ?>, Integer>> entryIterator = entity.getIdEntryIterator();
		while (entryIterator.hasNext()) {
			Entry<SystemScene<?, ?>, Integer> entry = entryIterator.next();
			entry.getKey().removeObject(entry.getValue());
		}

		for (Entity child : entity.getChilds()) {
			removeChildIntern(child);
			Entity.freeId(child.id);
		}
		entity.cleanupChilds();
		entity.transform.cleanupEntity();
		spacePartitioning.remove(entity);
	}

	public final List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public final List<Entity> getTopEntities() {
		return Collections.unmodifiableList(topEntities);
	}

	public final void addTopEntity(Entity entity) {
		topEntities.add(entity);
	}

	public final void removeTopEntity(Entity entity) {
		topEntities.remove(entity);
	}

	@Nullable
	public final Entity getEntity(Vector2f pos) {
		return getEntity(pos.x, pos.y);
	}

	@Nullable
	public final Entity getEntity(float x, float y) {
		return spacePartitioning.get(x, y, 0.0f, null);
	}

	@Nullable
	public final Entity getEntity(Vector3f pos) {
		return getEntity(pos.x, pos.y, pos.z);
	}

	@Nullable
	public final Entity getEntity(float x, float y, float z) {
		return spacePartitioning.get(x, y, z, null);
	}

	public final Entity getEntity(Vector2f pos, Entity defaultValue) {
		return getEntity(pos.x, pos.y, defaultValue);
	}

	public final Entity getEntity(float x, float y, Entity defaultValue) {
		return spacePartitioning.get(x, y, 0.0f, defaultValue);
	}

	public final Entity getEntity(Vector3f pos, Entity defaultValue) {
		return getEntity(pos.x, pos.y, pos.z, defaultValue);
	}

	public final Entity getEntity(float x, float y, float z, Entity defaultValue) {
		return spacePartitioning.get(x, y, z, defaultValue);
	}

	public final void forEntity(Vector2f min, Vector2f max, Consumer<Entity> func) {
		forEntity(min.x, min.y, max.x, max.y, func);
	}

	public final void forEntity(float xMin, float yMin, float xMax, float yMax, Consumer<Entity> func) {
		spacePartitioning.forNear(xMin, yMin, 0.0f, xMax, yMax, 0.0f, (entity) -> {
			if (entity.transform.getX() >= xMin && entity.transform.getY() >= yMin && entity.transform.getX() <= xMax
					&& entity.transform.getY() <= yMax)
				func.accept(entity);
		});
	}

	public final void forEntity(Vector3f min, Vector3f max, Consumer<Entity> func) {
		forEntity(min.x, min.y, min.z, max.x, max.y, max.z, func);
	}

	public final void forEntity(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax,
			Consumer<Entity> func) {
		spacePartitioning.forNear(xMin, yMin, zMin, xMax, yMax, zMax, (entity) -> {
			if (entity.transform.getX() >= xMin && entity.transform.getY() >= yMin && entity.transform.getZ() >= zMin
					&& entity.transform.getX() <= xMax && entity.transform.getY() <= yMax
					&& entity.transform.getZ() <= zMax)
				func.accept(entity);
		});
	}

	public final List<Entity> getEntities(Vector2f min, Vector2f max) {
		return getEntities(min.x, min.y, max.x, max.y);
	}

	public final List<Entity> getEntities(float xMin, float yMin, float xMax, float yMax) {
		List<Entity> result = new UnorderedIdentityArrayList<Entity>();
		spacePartitioning.forNear(xMin, yMin, 0.0f, xMax, yMax, 0.0f, (entity) -> {
			if (entity.transform.getX() >= xMin && entity.transform.getY() >= yMin && entity.transform.getX() <= xMax
					&& entity.transform.getY() <= yMax)
				result.add(entity);
		});
		return result;
	}

	public final List<Entity> getEntities(Vector3f min, Vector3f max) {
		return getEntities(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public final List<Entity> getEntities(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
		List<Entity> result = new UnorderedIdentityArrayList<Entity>();
		spacePartitioning.forNear(xMin, yMin, zMin, xMax, yMax, zMax, (entity) -> {
			if (entity.transform.getX() >= xMin && entity.transform.getY() >= yMin && entity.transform.getZ() >= zMin
					&& entity.transform.getX() <= xMax && entity.transform.getY() <= yMax
					&& entity.transform.getZ() <= zMax)
				result.add(entity);
		});
		return result;
	}

	public final List<Entity> getEntitiesNear(Vector2f pos, float range) {
		return getEntitiesNear(pos.x, pos.y, range, range);
	}

	public final List<Entity> getEntitiesNear(Vector2f pos, Vector2f range) {
		return getEntitiesNear(pos.x, pos.y, range.x, range.y);
	}

	public final List<Entity> getEntitiesNear(float x, float y, float xRange, float yRange) {
		float xMin = x - xRange;
		float yMin = y - yRange;
		float xMax = x + xRange;
		float yMax = y + yRange;
		List<Entity> result = new UnorderedIdentityArrayList<Entity>();
		spacePartitioning.forNear(xMin, yMin, 0.0f, xMax, yMax, 0.0f, (entity) -> {
			if (entity.transform.getX() >= xMin && entity.transform.getY() >= yMin && entity.transform.getX() <= xMax
					&& entity.transform.getY() <= yMax)
				result.add(entity);
		});
		return result;
	}

	public final List<Entity> getEntitiesNear(Vector3f pos, float range) {
		return getEntitiesNear(pos.x, pos.y, pos.z, range, range, range);
	}

	public final List<Entity> getEntitiesNear(Vector3f pos, Vector3f range) {
		return getEntitiesNear(pos.x, pos.y, pos.z, range.x, range.y, range.z);
	}

	public final List<Entity> getEntitiesNear(float x, float y, float z, float xRange, float yRange, float zRange) {
		float xMin = x - xRange;
		float yMin = y - yRange;
		float zMin = z - zRange;
		float xMax = x + xRange;
		float yMax = y + yRange;
		float zMax = z + zRange;
		List<Entity> result = new UnorderedIdentityArrayList<Entity>();
		spacePartitioning.forNear(xMin, yMin, zMin, xMax, yMax, zMax, (entity) -> {
			if (entity.transform.getX() >= xMin && entity.transform.getY() >= yMin && entity.transform.getZ() >= zMin
					&& entity.transform.getX() <= xMax && entity.transform.getY() <= yMax
					&& entity.transform.getZ() <= zMax)
				result.add(entity);
		});
		return result;
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

	public void setSpacePartitioning(SpacePartitioning spacePartitioning) {
		this.spacePartitioning = spacePartitioning;
		for (Entity entity : entities) {
			entity.transform.spacePartitioning = spacePartitioning;
			spacePartitioning.add(entity);
		}
	}

	public SpacePartitioning getSpacePartitioning() {
		return spacePartitioning;
	}

	public void pause(boolean pause) {
		this.paused = pause;
	}

	public boolean isPaused() {
		return paused;
	}
}
