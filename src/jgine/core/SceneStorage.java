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
 * Storage for {@link Scene}<code>s</code> with the following specification:
 * 
 * <pre>
 *- get(id) and get(name) reflect additions/removals immediately.
 *- values()/view() are updated during the next update() call.
 *- Iteration observes a stable snapshot.
 * </pre>
 */
public final class SceneStorage {
	public static final int MAX_SCENES = 65536;
	private static final VarHandle ID_MAP_HANDLE = MethodHandles.arrayElementVarHandle(Scene[].class);

	private static final IdGenerator ID_GENERATOR = new IdGenerator(MAX_SCENES);
	private static final Scene[] ID_MAP = new Scene[MAX_SCENES];
	private static final Map<String, Scene> NAME_MAP = new ConcurrentHashMap<>();
	private static volatile List<Scene> LIST = new UnorderedArrayList<>();
	private static volatile List<Scene> VIEW = Collections.unmodifiableList(LIST);
	private static final Queue<Scene> ADD_QUEUE = new ConcurrentLinkedQueue<>();
	private static final Queue<Scene> REMOVE_QUEUE = new ConcurrentLinkedQueue<>();

	static int add(Scene scene) {
		int id = ID_GENERATOR.generate();
		ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(id), scene);
		NAME_MAP.put(scene.name, scene);
		ADD_QUEUE.add(scene);
		return id;
	}

	static void remove(Scene scene) {
		ID_MAP_HANDLE.setVolatile(ID_MAP, IdGenerator.index(scene.id), null);
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

	public static boolean isAlive(int id) {
		return ID_GENERATOR.isAlive(id);
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
