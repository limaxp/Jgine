package jgine.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import jgine.system.EngineSystem;
import jgine.system.SystemScene;
import jgine.system.transform.TransformScene;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.registry.Registry;
import jgine.utils.scheduler.Scheduler;
import jgine.utils.spacePartitioning.SpatialHashing2d;

/**
 * A scene or world identified with id and name. Use this class to create a
 * {@link Entity}. Scenes store a list of entities and the
 * {@link EngineSystem}<code>s</code> they use. They also provide a pause
 * capability.
 * <p>
 * Change the update order of the given systems by setting or modifying the
 * {@link UpdateOrder} instance. Same can be done with the render order List.
 */
public class Scene {

	public final int id;
	public final String name;
	private SystemScene<?, ?>[] systemMap;
	private List<SystemScene<?, ?>> systemList;
	private List<Entity> entities;
	private UpdateOrder updateOrder;
	private IntList renderOrder;
	private boolean paused;

	public static Scene create(String name) {
		Collection<EngineSystem<?, ?>> systems = Registry.SYSTEM.values();
		return create(name, Engine.UPDATE_ORDER.clone(), new IntArrayList(Engine.RENDER_ORDER),
				systems.toArray(new EngineSystem[systems.size()]));
	}

	public static Scene create(String name, UpdateOrder updateOrder, IntList renderOrder,
			EngineSystem<?, ?>... systems) {
		Scene scene = new Scene(name);
		scene.setSystems(systems);
		scene.setUpdateOrder(updateOrder);
		scene.setRenderOrder(renderOrder);
		return scene;
	}

	public Scene(String name) {
		this.id = name.hashCode();
		this.name = name;
		systemMap = new SystemScene<?, ?>[Registry.SYSTEM.size()];
		systemList = new UnorderedIdentityArrayList<SystemScene<?, ?>>();
		entities = new UnorderedIdentityArrayList<Entity>();
		paused = false;
		Engine.getInstance().addScene(this);
	}

	final void free() {
		for (Entity entity : entities)
			entity.free();
		for (SystemScene<?, ?> systemScene : systemList)
			systemScene.free();

		entities = null;
		systemMap = null;
		systemList = null;
	}

	public final void delete() {
		Engine.getInstance().deleteScene(this);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T setSystem(EngineSystem<?, ?> system) {
		int id = system.id;
		removeSystem(id);
		SystemScene<?, ?> systemScene = system.createScene(this);
		systemMap[id] = systemScene;
		Scheduler.runTask(() -> systemList.add(systemScene));
		return (T) systemScene;
	}

	@Nullable
	public final <T extends SystemScene<?, ?>> T removeSystem(EngineSystem<?, ?> system) {
		return removeSystem(system.id);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T removeSystem(int id) {
		SystemScene<?, ?> systemScene = systemMap[id];
		if (systemScene == null)
			return null;

		systemMap[id] = null;
		Scheduler.runTask(() -> {
			systemList.remove(systemScene);
			systemScene.free();
		});
		return (T) systemScene;
	}

	public final void setSystems(EngineSystem<?, ?>... systems) {
		for (EngineSystem<?, ?> system : systems)
			setSystem(system);
	}

	public final void setSystems(Collection<EngineSystem<?, ?>> systems) {
		for (EngineSystem<?, ?> system : systems)
			setSystem(system);
	}

	public final void removeSystems(EngineSystem<?, ?>... systems) {
		for (EngineSystem<?, ?> system : systems)
			removeSystem(system);
	}

	public final void removeSystems(int... systems) {
		for (int system : systems)
			removeSystem(system);
	}

	public final void removeSystems(Collection<EngineSystem<?, ?>> systems) {
		for (EngineSystem<?, ?> system : systems)
			removeSystem(system);
	}

	public final void removeSystems(IntCollection systems) {
		for (int system : systems)
			removeSystem(system);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(EngineSystem<?, ?> system) {
		return (T) systemMap[system.id];
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public final <T extends SystemScene<?, ?>> T getSystem(int id) {
		return (T) systemMap[id];
	}

	public final Collection<SystemScene<?, ?>> getSystems() {
		return systemList;
	}

	public final boolean hasSystem(EngineSystem<?, ?> system) {
		return systemMap[system.id] != null;
	}

	public final boolean hasSystem(int id) {
		return systemMap[id] != null;
	}

	final void addEntity(Entity entity) {
		entities.add(entity);
	}

	final void removeEntity(Entity entity) {
		entities.remove(entity);
	}

	public final List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public void setUpdateOrder(UpdateOrder updateOrder) {
		this.updateOrder = updateOrder;
	}

	public UpdateOrder getUpdateOrder() {
		return updateOrder;
	}

	public void setRenderOrder(IntList renderOrder) {
		this.renderOrder = renderOrder;
	}

	public IntList getRenderOrder() {
		return renderOrder;
	}

	public SpatialHashing2d<Entity> getSpacePartitioning() {
		TransformScene transformScene = getSystem(Engine.TRANSFORM);
		return transformScene.getSpacePartitioning();
	}

	public void pause(boolean pause) {
		this.paused = pause;
	}

	public boolean isPaused() {
		return paused;
	}
}
