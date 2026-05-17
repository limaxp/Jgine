package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
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
public final class Scene {

	public final int id;
	public final String name;
	UpdateOrder updateOrder;
	IntList renderOrder;
	private SystemScene<?, ?>[] systemMap;
	private List<SystemScene<?, ?>> systemList;
	private List<Entity> entities;
	private boolean paused;

	public Scene(String name) {
		this.id = name.hashCode();
		this.name = name;
		systemMap = new SystemScene<?, ?>[Registry.SYSTEM.size()];
		systemList = new UnorderedIdentityArrayList<SystemScene<?, ?>>();
		entities = new UnorderedIdentityArrayList<Entity>();
		paused = false;
		Engine.getInstance().addScene(this);
	}

	void free() {
		for (Entity entity : entities)
			entity.free();
		for (SystemScene<?, ?> systemScene : systemList)
			systemScene.free();

		entities = null;
		systemMap = null;
		systemList = null;
	}

	public void delete() {
		Engine.getInstance().deleteScene(this);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends SystemScene<?, ?>> T setSystem(EngineSystem<?, ?> system) {
		int id = system.id;
		removeSystem(id);
		SystemScene<?, ?> systemScene = system.createScene(this);
		systemMap[id] = systemScene;
		Scheduler.runTask(() -> systemList.add(systemScene));
		return (T) systemScene;
	}

	@Nullable
	public <T extends SystemScene<?, ?>> T removeSystem(EngineSystem<?, ?> system) {
		return removeSystem(system.id);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends SystemScene<?, ?>> T removeSystem(int id) {
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

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends SystemScene<?, ?>> T getSystem(EngineSystem<?, ?> system) {
		return (T) systemMap[system.id];
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends SystemScene<?, ?>> T getSystem(int id) {
		return (T) systemMap[id];
	}

	public Collection<SystemScene<?, ?>> getSystems() {
		return systemList;
	}

	public final boolean hasSystem(EngineSystem<?, ?> system) {
		return systemMap[system.id] != null;
	}

	public final boolean hasSystem(int id) {
		return systemMap[id] != null;
	}

	void addEntity(Entity entity) {
		entities.add(entity);
	}

	void removeEntity(Entity entity) {
		entities.remove(entity);
	}

	public List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public void setUpdateOrder(UpdateOrder updateOrder) {
		this.updateOrder = updateOrder.clone();
	}

	public UpdateOrder getUpdateOrder() {
		return updateOrder.clone();
	}

	public void setRenderOrder(IntList renderOrder) {
		this.renderOrder = new IntArrayList(renderOrder);
	}

	public IntList getRenderOrder() {
		return new IntArrayList(renderOrder);
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

	public void save(DataOutput out) throws IOException {
		out.writeUTF(name);

		Collection<SystemScene<?, ?>> systems = systemList;
		out.writeInt(systems.size());
		for (SystemScene<?, ?> systemScene : systems) {
			out.writeInt(systemScene.id);
			systemScene.save(out);
		}

		int entitySize = entities.size();
		out.writeInt(entitySize);
		for (int i = 0; i < entitySize; i++) {
			Entity entity = entities.get(i);
			entity.save(out);
			entity.saveMap(out);
		}

		updateOrder.save(out);

		int renderOrderSize = renderOrder.size();
		out.writeInt(renderOrderSize);
		for (int i = 0; i < renderOrderSize; i++)
			out.writeInt(renderOrder.getInt(i));
	}

	private void load(DataInput in) throws IOException {
		int systemSize = in.readInt();
		for (int i = 0; i < systemSize; i++) {
			SystemScene<?, ?> systemScene = setSystem(Registry.SYSTEM.get(in.readInt()));
			systemScene.load(in);
		}

		int entitySize = in.readInt();
		for (int i = 0; i < entitySize; i++) {
			Entity entity = new Entity(this);
			entity.load(in);
			entity.loadMap(in);
		}

		updateOrder = new UpdateOrder();
		updateOrder.load(in);

		int renderOrderSize = in.readInt();
		renderOrder = new IntArrayList(renderOrderSize);
		for (int i = 0; i < renderOrderSize; i++)
			renderOrder.add(in.readInt());
	}

	public static Scene create(String name) {
		Collection<EngineSystem<?, ?>> systems = Registry.SYSTEM.values();
		return create(name, Engine.UPDATE_ORDER, Engine.RENDER_ORDER,
				systems.toArray(new EngineSystem[systems.size()]));
	}

	public static Scene create(String name, UpdateOrder updateOrder, IntList renderOrder,
			EngineSystem<?, ?>... systems) {
		Scene scene = new Scene(name);
		for (EngineSystem<?, ?> system : systems)
			scene.setSystem(system);
		scene.setUpdateOrder(updateOrder);
		scene.setRenderOrder(renderOrder);
		return scene;
	}

	public static Scene create(DataInput in) throws IOException {
		Scene scene = new Scene(in.readUTF());
		scene.load(in);
		return scene;
	}
}
