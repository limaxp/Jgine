package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jgine.system.EngineSystem;
import jgine.system.SystemScene;
import jgine.system.transform.TransformScene;
import jgine.utils.Flag;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.registry.Registry;
import jgine.utils.spacePartitioning.SpatialHashing2d;

/**
 * A scene or world identified with id and name. Use this class to create a
 * {@link Entity}. Scenes store a list of entities and the
 * {@link EngineSystem}<code>s</code> they use. They also provide a pause
 * capability.
 * <p>
 * Change the update order of the given systems by setting the
 * {@link UpdateOrder} instance. Same can be done with the render order List.
 */
public final class Scene {

	private static final VarHandle FLAG_HANDLE;

	static {
		try {
			FLAG_HANDLE = MethodHandles.lookup().findVarHandle(Scene.class, "flag", int.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private static final UpdateOrder EMPTY_UPDATE_ORDER = new UpdateOrder();
	private static final IntList EMPTY_RENDER_ORDER = new IntArrayList(0);

	public final int id;
	public final String name;
	volatile UpdateOrder updateOrder;
	volatile IntList renderOrder;
	private final SystemScene<?, ?>[] systems;
	private final SystemScene<?, ?>[] systemMap;
	private final List<Entity> entities;
	private volatile int flag;

	public Scene(String name) {
		this(name, Registry.SYSTEM.values());
		setUpdateOrder(Engine.UPDATE_ORDER);
		setRenderOrder(Engine.RENDER_ORDER);
	}

	public Scene(DataInput in) throws IOException {
		this(in, Registry.SYSTEM.values());
	}

	public Scene(DataInput in, Collection<EngineSystem<?, ?>> systems) throws IOException {
		this(in.readUTF(), systems);
		load(in);
	}

	public Scene(String name, Collection<EngineSystem<?, ?>> systems) {
		this.name = name;
		this.updateOrder = EMPTY_UPDATE_ORDER;
		this.renderOrder = EMPTY_RENDER_ORDER;
		this.entities = new UnorderedIdentityArrayList<Entity>();
		this.systems = new SystemScene[systems.size()];
		this.systemMap = new SystemScene<?, ?>[Registry.SYSTEM.size()];
		int i = 0;
		for (EngineSystem<?, ?> system : systems) {
			SystemScene<?, ?> systemScene = system.createScene(this);
			this.systems[i++] = systemScene;
			this.systemMap[systemScene.id] = systemScene;
		}
		this.id = SceneMap.add(this);
	}

	void free() {
		for (Entity entity : entities)
			entity.free();
		for (SystemScene<?, ?> systemScene : systems)
			systemScene.free();
	}

	public void delete() {
		if (setFlag(Flag.DELETE, true))
			SceneMap.remove(this);
	}

	/**
	 * <b>Never Modify!</b> Returns internal data!
	 */
	public SystemScene<?, ?>[] getSystems() {
		return systems;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends SystemScene<?, ?>> T getSystem(int id) {
		return (T) systemMap[id];
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

	public boolean isDeleted() {
		return getFlag(Flag.DELETE);
	}

	public void pause(boolean pause) {
		setFlag(Flag.PAUSE, pause);
	}

	public boolean isPaused() {
		return getFlag(Flag.PAUSE);
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

	public void save(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeInt(flag);

		out.writeInt(systems.length);
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
		flag = in.readInt();
		int systemSize = in.readInt();
		for (int i = 0; i < systemSize; i++)
			getSystem(in.readInt()).load(in);

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

	@Override
	public String toString() {
		return "[id=" + id + ", name=" + name + ", entities=" + entities.size() + ", pause="
				+ (isPaused() ? "true" : "false") + ", systems=" + systemsToString() + "]";
	}

	public String systemsToString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int size = systems.length;
		if (size != 0) {
			for (int i = 0; i < size; i++) {
				sb.append(systems[i].name);
				sb.append(',');
				sb.append(' ');
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(']');
		return sb.toString();
	}

	public static List<Scene> values() {
		return SceneMap.view();
	}

	public static Scene get(int id) {
		return SceneMap.get(id);
	}

	public static Scene get(String name) {
		return SceneMap.get(name);
	}
}
