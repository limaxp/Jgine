package jgine.core;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jgine.utils.IdGenerator;
import jgine.utils.collection.list.UnorderedArrayList;

public class SceneMap {

	public static final int MAX_SCENES = 65535;
	private static final IdGenerator ID_GENERATOR = new IdGenerator(1, MAX_SCENES + 2);
	private static final Scene[] ID_MAP = new Scene[MAX_SCENES + 1];
	private static final Map<String, Scene> NAME_MAP = new ConcurrentHashMap<>();
	private static volatile List<Scene> LIST = new UnorderedArrayList<>();
	private static final Queue<Scene> ADD_QUEUE = new ConcurrentLinkedQueue<>();
	private static final Queue<Scene> REMOVE_QUEUE = new ConcurrentLinkedQueue<>();

	static int add(Scene scene) {
		int id;
		synchronized (ID_GENERATOR) {
			id = ID_GENERATOR.generate();
			ID_MAP[IdGenerator.index(id)] = scene;
		}
		NAME_MAP.put(scene.name, scene);
		ADD_QUEUE.add(scene);
		return id;
	}

	static void remove(Scene scene) {
		synchronized (ID_GENERATOR) {
			ID_GENERATOR.free(scene.id);
			ID_MAP[IdGenerator.index(scene.id)] = null;
		}
		NAME_MAP.remove(scene.name);
		REMOVE_QUEUE.add(scene);
	}

	static void update() {
		if (ADD_QUEUE.size() + REMOVE_QUEUE.size() < 1)
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
	}

	/**
	 * <b>Never Modify!</b> Returns internal data!
	 */
	static List<Scene> values() {
		return LIST;
	}

	static Scene get(int id) {
		return ID_MAP[id];
	}

	static Scene get(String name) {
		return NAME_MAP.get(name);
	}
}
