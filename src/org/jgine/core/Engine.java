package org.jgine.core;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import org.jgine.collection.list.IntList;
import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.core.entity.Entity;
import org.jgine.core.gameLoop.FixedTickGameLoop;
import org.jgine.core.gameLoop.GameLoop;
import org.jgine.core.input.Input;
import org.jgine.core.manager.ResourceManager;
import org.jgine.core.manager.ServiceManager;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.sound.SoundManager;
import org.jgine.core.window.DisplayManager;
import org.jgine.core.window.Window;
import org.jgine.net.game.ConnectionManager;
import org.jgine.render.RenderConfiguration;
import org.jgine.render.Renderer;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;
import org.jgine.system.systems.ai.AiSystem;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraScene;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.collision.CollisionSystem;
import org.jgine.system.systems.graphic.Graphic2DSystem;
import org.jgine.system.systems.graphic.GraphicSystem;
import org.jgine.system.systems.input.InputSystem;
import org.jgine.system.systems.light.LightScene;
import org.jgine.system.systems.light.LightSystem;
import org.jgine.system.systems.particle.ParticleSystem;
import org.jgine.system.systems.physic.PhysicSystem;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.system.systems.tileMap.TileMapSystem;
import org.jgine.system.systems.ui.UISystem;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.options.OptionFile;
import org.jgine.utils.options.Options;
import org.jgine.utils.scheduler.Scheduler;
import org.jgine.utils.scheduler.TaskExecutor;

/**
 * The Base Engine class. You can extend this class to override some methods.
 * After calling the constructor all internal systems are initialized and ready
 * to use. Call the start() method to start the game loop created by
 * createGameLoop(). Use shutdown() method to stop game loop and free engine
 * systems.
 * <p>
 * Use this class to create and destroy a {@link Scene} with whom you can create
 * a {@link Entity}.
 */
public class Engine {

	public static final PhysicSystem PHYSIC_SYSTEM = SystemManager.register(new PhysicSystem());
	public static final CameraSystem CAMERA_SYSTEM = SystemManager.register(new CameraSystem());
	public static final CollisionSystem COLLISION_SYSTEM = SystemManager.register(new CollisionSystem());
	public static final GraphicSystem GRAPHIC_SYSTEM = SystemManager.register(new GraphicSystem());
	public static final Graphic2DSystem GRAPHIC_2D_SYSTEM = SystemManager.register(new Graphic2DSystem());
	public static final InputSystem INPUT_SYSTEM = SystemManager.register(new InputSystem());
	public static final LightSystem LIGHT_SYSTEM = SystemManager.register(new LightSystem());
	public static final ParticleSystem PARTICLE_SYSTEM = SystemManager.register(new ParticleSystem());
	public static final ScriptSystem SCRIPT_SYSTEM = SystemManager.register(new ScriptSystem());
	public static final TileMapSystem TILEMAP_SYSTEM = SystemManager.register(new TileMapSystem());
	public static final UISystem UI_SYSTEM = SystemManager.register(new UISystem());
	public static final AiSystem AI_SYSTEM = SystemManager.register(new AiSystem());

	private static Engine instance;

	public static Engine getInstance() {
		return instance;
	}

	public final String name;
	private boolean isRunning;
	private final Window window;
	private GameLoop gameLoop;
	private final Map<String, Scene> sceneMap;
	private final Map<Integer, Scene> sceneIdMap;
	private final List<Scene> scenes;
	private final List<RenderConfiguration> renderConfigs;

	public Engine(String name) {
		instance = this;
		this.name = name;
		sceneMap = new ConcurrentHashMap<String, Scene>();
		sceneIdMap = new ConcurrentHashMap<Integer, Scene>();
		scenes = new IdentityArrayList<Scene>();
		renderConfigs = new IdentityArrayList<RenderConfiguration>();
		DisplayManager.init();
		window = new Window(name);
		window.setWindowPosCallback((id, x, y) -> gameLoop.run());
		window.setWindowSizeCallback((id, width, height) -> gameLoop.run());
		Input.setWindow(window);
		Renderer.init();
		renderConfigs.add(new RenderConfiguration());
		SoundManager.init();
		gameLoop = createGameLoop();
		gameLoop.setUpdateFunction(this::update);
		gameLoop.setRenderFunction(this::render);
	}

	private final void terminate() {
		for (Scene scene : scenes)
			scene.free();
		TaskExecutor.shutdown();
		ResourceManager.terminate();
		for (RenderConfiguration renderConfig : renderConfigs)
			renderConfig.close();
		Renderer.terminate();
		SoundManager.terminate();
		window.delete();
		DisplayManager.terminate();
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
			DisplayManager.pollGLFWEvents();
			Input.poll();
		}
		terminate();
	}

	private final boolean checkStatus() {
		if (window.shouldClose())
			shutdown();
		return isRunning;
	}

	protected void onUpdate() {
	}

	private final void update(float dt) {
		ConnectionManager.update();
		for (Scene scene : scenes)
			if (!scene.isPaused())
				updateScene(scene, dt);
		ServiceManager.distributeChanges();
		Scheduler.update();
		TaskExecutor.execute(Scheduler::updateAsync);
		Input.update();
		SoundManager.update();
		onUpdate();
	}

	private final void updateScene(Scene scene, float dt) {
		if (!scene.hasUpdateOrder()) {
			for (SystemScene<?, ?> systemScene : scene.getSystems())
				systemScene.update(dt);
		} else
			new SceneUpdate(scene, scene.getUpdateOrder(), dt).start();
	}

	protected void onRender() {
	}

	private final void render() {
		Renderer.start();
		for (Scene scene : scenes)
			if (!scene.isPaused())
				renderScene(scene);
		Renderer.finish(renderConfigs);
		window.swapBuffers();
		onRender();
	}

	private final void renderScene(Scene scene) {
		LightScene lightScene = scene.getSystem(LIGHT_SYSTEM);
		if (lightScene != null)
			Renderer.setLights(lightScene);

		if (!scene.hasRenderOrder()) {
			for (Camera camera : ((CameraScene) scene.getSystem(CAMERA_SYSTEM)).getObjects()) {
				Renderer.setCamera_UNSAFE(camera);
				for (SystemScene<?, ?> systemScene : scene.getSystems())
					systemScene.render();
			}
		} else {
			for (Camera camera : ((CameraScene) scene.getSystem(CAMERA_SYSTEM)).getObjects()) {
				Renderer.setCamera_UNSAFE(camera);
				new SceneRender(scene, scene.getRenderOrder()).start();
			}
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
		Scene scene = new Scene(this, name);
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

	public void addRenderConfig(RenderConfiguration renderConfig) {
		renderConfigs.add(renderConfig);
	}

	public void removeRenderConfig(RenderConfiguration renderConfig) {
		renderConfigs.remove(renderConfig);
		renderConfig.close();
	}

	public void removeRenderConfig(int index) {
		renderConfigs.remove(index).close();
	}

	public final RenderConfiguration getRenderConfig() {
		return renderConfigs.get(0);
	}

	public final RenderConfiguration getRenderConfig(int index) {
		return renderConfigs.get(index);
	}

	public final int getRenderConfigSize() {
		return renderConfigs.size();
	}

	private static class SceneUpdate extends SceneRender {

		protected int updatedSize;
		protected final CompletionService<Object> completionService;
		protected final float dt;

		public SceneUpdate(Scene scene, UpdateOrder updateOrder, float dt) {
			super(scene, updateOrder);
			completionService = new ExecutorCompletionService<Object>(TaskExecutor.getExecutor());
			this.dt = dt;
		}

		@Override
		public void start() {
			super.start();
			if (Options.SYNCHRONIZED)
				return;
			while (updatedSize != updateOrder.size()) {
				try {
					completionService.take().get();
				} catch (InterruptedException | ExecutionException e) {
					Logger.err("SceneUpdate: Error on getting future!", e);
					break;
				}
				updatedSize++;
			}
		}

		@Override
		protected void update(int system) {
			if (Options.SYNCHRONIZED) {
				scene.getSystem(system).update(dt);
				updated.set(system, true);
				checkUpdates(system);
				return;
			}
			completionService.submit(() -> {
				scene.getSystem(system).update(dt);
				synchronized (updated) {
					updated.set(system, true);
					checkUpdates(system);
				}
				return system;
			});
		}
	}

	private static class SceneRender {

		protected final Scene scene;
		protected final UpdateOrder updateOrder;
		protected final BitSet updated;

		public SceneRender(Scene scene, UpdateOrder updateOrder) {
			this.scene = scene;
			this.updateOrder = updateOrder;
			updated = new BitSet(SystemManager.getSize());
		}

		public void start() {
			IntList start = updateOrder.getStart();
			for (int i = 0; i < start.size(); i++)
				update(start.getInt(i));
		}

		protected void update(int system) {
			scene.getSystem(system).render();
			updated.set(system, true);
			checkUpdates(system);
		}

		protected void checkUpdates(int system) {
			for (int currentAfter : updateOrder.getAfter(system))
				checkUpdate(currentAfter);
		}

		protected void checkUpdate(int system) {
			for (int before : updateOrder.getBefore(system))
				if (!updated.get(before))
					return;
			update(system);
		}
	}
}
