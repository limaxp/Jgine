package jgine.core;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jgine.utils.IdGenerator;
import jgine.utils.collection.list.UnorderedArrayList;

/**
 * get(id) and get(name) reflect additions/removals immediately.
 * <p>
 * values()/view() are updated during the next update() call.
 * <p>
 * Iteration observes a stable snapshot.
 */
public final class SceneStorage {

	public static final int MAX_SCENES = 65535;
	private static final IdGenerator ID_GENERATOR = new IdGenerator(1, MAX_SCENES + 2);
	private static final Scene[] ID_MAP = new Scene[MAX_SCENES + 1];
	private static final VarHandle ID_MAP_HANDLE = MethodHandles.arrayElementVarHandle(Scene[].class);
	private static final Object ID_LOCK = new Object();
	private static final Map<String, Scene> NAME_MAP = new ConcurrentHashMap<>();
	private static volatile List<Scene> LIST = new UnorderedArrayList<>();
	private static volatile List<Scene> VIEW = Collections.unmodifiableList(LIST);
	private static final Queue<Scene> ADD_QUEUE = new ConcurrentLinkedQueue<>();
	private static final Queue<Scene> REMOVE_QUEUE = new ConcurrentLinkedQueue<>();

	static int add(Scene scene) {
		int id;
		synchronized (ID_LOCK) {
			id = ID_GENERATOR.generate();
			ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(id), scene);
		}
		NAME_MAP.put(scene.name, scene);
		ADD_QUEUE.add(scene);
		return id;
	}

	static void remove(Scene scene) {
		synchronized (ID_LOCK) {
			ID_GENERATOR.free(scene.id);
			ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(scene.id), null);
		}
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
			newScenes.remove(scene);
			scene.free();
		}

		queue = ADD_QUEUE;
		while ((scene = queue.poll()) != null) {
			newScenes.add(scene);
		}
		LIST = newScenes;
		VIEW = Collections.unmodifiableList(newScenes);
	}

	/**
	 * <b>Never Modify!</b> Returns internal data!
	 */
	static List<Scene> values() {
		return LIST;
	}

	public static List<Scene> view() {
		return VIEW;
	}

	public static Scene get(int id) {
		return (Scene) ID_MAP_HANDLE.getVolatile(ID_MAP, IdGenerator.index(id));
	}

	public static Scene get(String name) {
		return NAME_MAP.get(name);
	}
}
