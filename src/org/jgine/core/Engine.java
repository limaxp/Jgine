package org.jgine.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jgine.core.gameLoop.FixedTickGameLoop;
import org.jgine.core.gameLoop.GameLoop;
import org.jgine.core.input.Input;
import org.jgine.core.manager.ResourceManager;
import org.jgine.core.manager.ServiceManager;
import org.jgine.core.manager.TaskManager;
import org.jgine.core.manager.UpdateManager;
import org.jgine.core.window.DisplayManager;
import org.jgine.core.window.Window;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.utils.options.OptionFile;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.misc.utils.scheduler.TaskExecutor;
import org.jgine.net.game.ConnectionManager;
import org.jgine.render.Renderer;
import org.jgine.sound.SoundManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public abstract class Engine {

	private static Engine engine;

	public static Engine getInstance() {
		return engine;
	}

	public final String name;
	private boolean isRunning;
	private final Window window;
	private GameLoop gameLoop;
	private final Map<String, Scene> sceneMap;
	private final Map<Integer, Scene> sceneIdMap;
	private final List<Scene> scenes;

	public Engine(String name) {
		engine = this;
		this.name = name;
		sceneMap = new ConcurrentHashMap<String, Scene>();
		sceneIdMap = new ConcurrentHashMap<Integer, Scene>();
		scenes = new IdentityArrayList<Scene>();
		GLFWHelper.init();
		DisplayManager.init();
		window = new Window(name);
		Input.setWindow(window);
		Renderer.init();
		SoundManager.init();
		gameLoop = createGameLoop();
		gameLoop.setUpdateFunction(this::update);
		gameLoop.setRenderFunction(this::render);
		ConnectionManager.init();
	}

	private final void terminate() {
		for (Scene scene : scenes)
			scene.free();
		TaskExecutor.shutdown();
		ResourceManager.free();
		Renderer.terminate();
		SoundManager.terminate();
		window.delete();
		GLFWHelper.terminate();
		OptionFile.save();
		gameLoop = null;
		ConnectionManager.terminate();
	}

	protected GameLoop createGameLoop() {
		return new FixedTickGameLoop(20);
	}

	public final GameLoop getGameLoop() {
		return gameLoop;
	}

	public final void start() {
		isRunning = true;
		while (checkStatus()) {
			gameLoop.run();
			GLFWHelper.pollGLFWEvents();
			Input.poll();
		}
		terminate();
	}

	private final boolean checkStatus() {
		if (window.shouldClose())
			shutdown();
		return isRunning;
	}

	public abstract void onUpdate();

	private final void update() {
		ConnectionManager.update();
		for (Scene scene : scenes)
			if (!scene.isPaused())
				updateScene(scene);
		ServiceManager.distributeChanges();
		Scheduler.update();
		Input.update();
		SoundManager.update();
		onUpdate();
	}

	private final void updateScene(Scene scene) {
		UpdateManager.distributeChanges();
		if (scene.hasUpdateOrder()) {
			UpdateOrder updateOrder = scene.getUpdateOrder();
			for (int i = 0; i < updateOrder.size(); i++) {
				List<EngineSystem> updateOrderSystems = updateOrder.get(i);
				if (updateOrderSystems.size() == 1)
					scene.getSystem(updateOrderSystems.get(0)).update();
				else
					TaskManager.execute(updateOrderSystems.size(),
							(j) -> scene.getSystem(updateOrderSystems.get(j))::update);
				UpdateManager.distributeChanges();
			}
		} else {
			for (SystemScene<?, ?> systemScene : scene.getSystems()) {
				systemScene.update();
				UpdateManager.distributeChanges();
			}
		}
	}

	public abstract void onRender();

	private final void render() {
		Renderer.begin();
		for (Scene scene : scenes)
			if (!scene.isPaused())
				renderScene(scene);
		Renderer.end();
		onRender();
	}

	private final void renderScene(Scene scene) {
		if (scene.hasRenderOrder()) {
			UpdateOrder renderOrder = scene.getRenderOrder();
			for (int i = 0; i < renderOrder.size(); i++)
				for (EngineSystem system : renderOrder.get(i))
					scene.getSystem(system).render();
		} else {
			for (SystemScene<?, ?> systemScene : scene.getSystems())
				systemScene.render();
		}
	}

	public final Window getWindow() {
		return window;
	}

	public final Scene createScene(String name, EngineSystem... systems) {
		Scene scene = createScene(name);
		scene.addSystems(systems);
		return scene;
	}

	public final Scene createScene(String name) {
		Scene scene = new Scene(name);
		sceneMap.put(name, scene);
		sceneIdMap.put(scene.id, scene);
		Scheduler.runTaskSynchron(() -> scenes.add(scene));
		return scene;
	}

	public final boolean deleteScene(Scene scene) {
		sceneMap.remove(scene.name);
		sceneIdMap.remove(scene.id);
		Scheduler.runTaskSynchron(() -> {
			scenes.remove(scene);
			scene.free();
		});
		return true;
	}

	public final Collection<Scene> getScenes() {
		return scenes;
	}

	public final Scene getScene(String name) {
		return sceneMap.get(name);
	}

	public final Scene getScene(int id) {
		return sceneIdMap.get(id);
	}

	public final Scene getScenePerIndex(int index) {
		return scenes.get(index);
	}

	public final int getFps() {
		return gameLoop.getFps();
	}

	public final boolean isRunning() {
		return isRunning;
	}

	public final void shutdown() {
		isRunning = false;
	}
}
