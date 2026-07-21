package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jgine.system.EngineSystem;
import jgine.system.SystemScene;
import jgine.system.transform.TransformScene;
import jgine.utils.Flag;
import jgine.utils.IdGenerator;
import jgine.utils.collection.list.UnorderedArrayList;
import jgine.utils.concurrent.ConcurrentRingBuffer;
import jgine.utils.registry.Registry;
import jgine.utils.spacePartitioning.SpatialHashing2d;

/**
 * A scene or world identified with id and name.
 * 
 * <pre>
 * Stores:
 * - int id 
 * - String name
 * - {@link Entity} list
 * - {@link EngineSystem}<code>s</code>
 * - {@link UpdateOrder}
 * - RenderOrder
 * - {@link Flag}
 * </pre>
 */
public final class Scene {

	public static final int MAX_SCENES = 65536;

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
	private int index = -1; // main thread only
	private volatile UpdateOrder updateOrder;
	private volatile IntList renderOrder;
	private final SystemScene<?, ?>[] systems;
	private final List<SystemScene<?, ?>> systems_view;
	private final SystemScene<?, ?>[] systemMap;
	private final List<Entity> entities;
	private final List<Entity> entities_view;
	private final ConcurrentRingBuffer<Runnable> commandQueue;
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
		this.entities = new UnorderedArrayList<>();
		this.entities_view = Collections.unmodifiableList(entities);
		this.commandQueue = new ConcurrentRingBuffer<>(1048576);
		this.systems = new SystemScene[systems.size()];
		this.systems_view = Collections.unmodifiableList(Arrays.asList(this.systems));
		this.systemMap = new SystemScene<?, ?>[Registry.SYSTEM.size()];
		int i = 0;
		for (EngineSystem<?, ?> system : systems) {
			SystemScene<?, ?> systemScene = system.createScene(this);
			this.systems[i++] = systemScene;
			this.systemMap[systemScene.id] = systemScene;
		}
		this.id = SceneStorage.add(this);
	}

	void free() {
		for (Entity entity : entities)
			entity.free();
		for (SystemScene<?, ?> systemScene : systems)
			systemScene.free();
	}

	public void delete() {
		if (setFlag(Flag.DELETE, true))
			SceneStorage.remove(this);
	}

	void pollCommands() {
		ConcurrentRingBuffer<Runnable> queue = commandQueue;
		Runnable cmd;
		while ((cmd = queue.poll()) != null) {
			cmd.run();
		}
	}

	void addEntity(Entity entity) {
		commandQueue.add(() -> {
			entity.setIndex(entities.size());
			entities.add(entity);
		});
	}

	void removeEntity(Entity entity) {
		commandQueue.add(() -> {
			Entity last = entities.getLast();
			entities.remove(entity.getIndex());
			if (entity != last)
				last.setIndex(entity.getIndex());
			entity.setIndex(-1);
		});
	}

	public List<Entity> getEntities() {
		return entities_view;
	}

	ConcurrentRingBuffer<Runnable> getCommandQueue() {
		return commandQueue;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends SystemScene<?, ?>> T getSystem(int id) {
		return (T) systemMap[id];
	}

	public List<SystemScene<?, ?>> getSystems() {
		return systems_view;
	}

	public void setUpdateOrder(UpdateOrder updateOrder) {
		this.updateOrder = updateOrder.clone();
	}

	public UpdateOrder getUpdateOrder() {
		return updateOrder.clone();
	}

	/**
	 * <b>Never Modify!</b> Returns internal data!
	 */
	UpdateOrder updateOrder() {
		return updateOrder;
	}

	public void setRenderOrder(IntList renderOrder) {
		this.renderOrder = new IntArrayList(renderOrder);
	}

	public IntList getRenderOrder() {
		return new IntArrayList(renderOrder);
	}

	/**
	 * <b>Never Modify!</b> Returns internal data!
	 */
	IntList renderOrder() {
		return renderOrder;
	}

	public SpatialHashing2d<Entity> getSpacePartitioning() {
		TransformScene transformScene = getSystem(Engine.TRANSFORM);
		return transformScene.getSpacePartitioning();
	}

	public boolean isDeleted() {
		return getFlag(Flag.DELETE);
	}

	public boolean pause(boolean pause) {
		return setFlag(Flag.PAUSE, pause);
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

	public static boolean isAlive(int id) {
		return SceneStorage.isAlive(id);
	}

	public static List<Scene> values() {
		return SceneStorage.view();
	}

	public static Scene get(int id) {
		return SceneStorage.get(id);
	}

	public static Scene get(String name) {
		return SceneStorage.get(name);
	}

	/**
	 * Storage for {@link Scene}<code>s</code> with the following specification:
	 * 
	 * <pre>
	 *- get(id) reflects additions immediately and will return old data until id index is recycled.
	 *- get(name) reflects additions/removals immediately.
	 *- values()/view() are updated during the next update() call.
	 *- Iteration observes a stable snapshot.
	 * </pre>
	 */
	static final class SceneStorage {

		private static final VarHandle ID_MAP_HANDLE = MethodHandles.arrayElementVarHandle(Scene[].class);

		private static final IdGenerator ID_GENERATOR = new IdGenerator(MAX_SCENES);
		private static final Scene[] ID_MAP = new Scene[MAX_SCENES];
		private static final Map<String, Scene> NAME_MAP = new ConcurrentHashMap<>();
		private static volatile List<Scene> LIST = new UnorderedArrayList<>();
		private static volatile List<Scene> VIEW = Collections.unmodifiableList(LIST);
		private static final Queue<Scene> ADD_QUEUE = new ConcurrentLinkedQueue<>();
		private static final Queue<Scene> REMOVE_QUEUE = new ConcurrentLinkedQueue<>();

		private static int add(Scene scene) {
			int id = ID_GENERATOR.generate();
			ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(id), scene);
			NAME_MAP.put(scene.name, scene);
			ADD_QUEUE.add(scene);
			return id;
		}

		private static void remove(Scene scene) {
			ID_GENERATOR.free(scene.id);
			NAME_MAP.remove(scene.name);
			REMOVE_QUEUE.add(scene);
		}

		static void update() {
			if (ADD_QUEUE.isEmpty() && REMOVE_QUEUE.isEmpty())
				return;

			List<Scene> newScenes = new UnorderedArrayList<>(LIST);
			Queue<Scene> queue = REMOVE_QUEUE;
			Scene scene;
			while ((scene = queue.poll()) != null) {
				Scene last = newScenes.getLast();
				newScenes.remove(scene.index);
				if (scene != last)
					last.index = scene.index;
				scene.index = -1;
				scene.free();
			}

			queue = ADD_QUEUE;
			while ((scene = queue.poll()) != null) {
				scene.index = newScenes.size();
				newScenes.add(scene);
			}
			LIST = newScenes;
			VIEW = Collections.unmodifiableList(newScenes);
		}

		private static boolean isAlive(int id) {
			return ID_GENERATOR.isAlive(id);
		}

		/**
		 * <b>Never Modify!</b> Returns internal data!
		 */
		static List<Scene> values() {
			return LIST;
		}

		private static List<Scene> view() {
			return VIEW;
		}

		private static Scene get(int id) {
			return (Scene) ID_MAP_HANDLE.getVolatile(ID_MAP, IdGenerator.index(id));
		}

		private static Scene get(String name) {
			return NAME_MAP.get(name);
		}
	}
}
