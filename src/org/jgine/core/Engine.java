package org.jgine.core;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.core.entity.Entity;
import org.jgine.core.gameLoop.FixedTickGameLoop;
import org.jgine.core.gameLoop.GameLoop;
import org.jgine.core.input.Input;
import org.jgine.core.sound.SoundManager;
import org.jgine.core.window.DisplayManager;
import org.jgine.core.window.Window;
import org.jgine.net.game.ConnectionManager;
import org.jgine.render.RenderConfiguration;
import org.jgine.render.Renderer;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;
import org.jgine.system.systems.ai.AiSystem;
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
import org.jgine.system.systems.script.Script;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.system.systems.tileMap.TileMapSystem;
import org.jgine.system.systems.ui.UISystem;
import org.jgine.utils.Service;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.options.OptionFile;
import org.jgine.utils.options.Options;
import org.jgine.utils.scheduler.Scheduler;
import org.jgine.utils.scheduler.TaskExecutor;
import org.lwjgl.system.MemoryUtil;

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

	/*
	 * NOTE
	 * 
	 * Each system must declare a java object! if outside of memory its a pointer!
	 * 
	 * Main thread should only render and trigger updates on worker threads!
	 * 
	 * 
	 * TODO
	 * 
	 * Transfrom is completely unusable! Does not get correct world coordinates!
	 * 
	 * System update code in Engine is horrible!
	 * 
	 * RenderQueue needs optimization!
	 * 
	 * Adding Systems in update loop might block threads! Remove synchronized block
	 * from update in system classes!
	 * 
	 */

	public static final PhysicSystem PHYSIC_SYSTEM = new PhysicSystem();
	public static final CameraSystem CAMERA_SYSTEM = new CameraSystem();
	public static final CollisionSystem COLLISION_SYSTEM = new CollisionSystem();
	public static final GraphicSystem GRAPHIC_SYSTEM = new GraphicSystem();
	public static final Graphic2DSystem GRAPHIC_2D_SYSTEM = new Graphic2DSystem();
	public static final InputSystem INPUT_SYSTEM = new InputSystem();
	public static final LightSystem LIGHT_SYSTEM = new LightSystem();
	public static final ParticleSystem PARTICLE_SYSTEM = new ParticleSystem();
	public static final ScriptSystem SCRIPT_SYSTEM = new ScriptSystem();
	public static final TileMapSystem TILEMAP_SYSTEM = new TileMapSystem();
	public static final UISystem UI_SYSTEM = new UISystem();
	public static final AiSystem AI_SYSTEM = new AiSystem();

	private static Engine instance;

	public static Engine getInstance() {
		return instance;
	}

	public final String name;
	private boolean isRunning;
	private GameLoop gameLoop;
	private final Map<String, Scene> sceneMap;
	private final Map<Integer, Scene> sceneIdMap;
	private final List<Scene> scenes;
	private Window window;
	private final List<RenderConfiguration> renderConfigs;

	public Engine(String name, boolean window) {
		instance = this;
		this.name = name;
		sceneMap = new ConcurrentHashMap<String, Scene>();
		sceneIdMap = new ConcurrentHashMap<Integer, Scene>();
		scenes = new IdentityArrayList<Scene>();
		renderConfigs = new IdentityArrayList<RenderConfiguration>();
		SoundManager.init();
		Script.register(getClass().getPackage());
		gameLoop = createGameLoop();
		gameLoop.setUpdateFunction(this::update);
		gameLoop.setRenderFunction(this::draw);
		if (window)
			createWindow();
	}

	private final void createWindow() {
		DisplayManager.init();
		window = new Window(name);
		window.setWindowPosCallback((id, x, y) -> gameLoop.run());
		window.setWindowSizeCallback((id, width, height) -> gameLoop.run());
		Input.setWindow(window);
		Renderer.init();
		renderConfigs.add(new RenderConfiguration());
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
		if (window != null && window.shouldClose())
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
		Service.distributeChanges();
		Scheduler.update();
		TaskExecutor.execute(Scheduler::updateAsync);
		Input.update();
		SoundManager.update();
		onUpdate();
		render(dt);
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

	private final void render(float dt) {
		if (window == null)
			return;
		Renderer.update(dt);
		for (Scene scene : scenes)
			if (!scene.isPaused())
				renderScene(scene, dt);
		onRender();
	}

	private final void draw() {
		if (window == null)
			return;
		Renderer.draw(renderConfigs);
		window.swapBuffers();
	}

	private final void renderScene(Scene scene, float dt) {
		LightScene lightScene = scene.getSystem(LIGHT_SYSTEM);
		if (lightScene != null)
			Renderer.setLights(lightScene);

		if (!scene.hasRenderOrder()) {
			((CameraScene) scene.getSystem(CAMERA_SYSTEM)).forEach((camera) -> {
				Renderer.setCamera_UNSAFE(camera);
				for (SystemScene<?, ?> systemScene : scene.getSystems())
					systemScene.render(dt);
			});
		} else {
			((CameraScene) scene.getSystem(CAMERA_SYSTEM)).forEach((camera) -> {
				Renderer.setCamera_UNSAFE(camera);
				new SceneRender(scene, scene.getRenderOrder(), dt).start();
			});
		}
	}

	public final Window getWindow() {
		return window;
	}

	public final Scene createScene(String name, EngineSystem<?, ?>... systems) {
		Scene scene = createScene(name);
		scene.setSystems(systems);
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

		public SceneUpdate(Scene scene, UpdateOrder updateOrder, float dt) {
			super(scene, updateOrder, dt);
			completionService = new ExecutorCompletionService<Object>(TaskExecutor.getExecutor());
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
		protected void update(EngineSystem<?, ?> system) {
			if (Options.SYNCHRONIZED) {
				scene.getSystem(system).update(dt);
				updated.set(system.id, true);
				checkUpdates(system);
				return;
			}
			completionService.submit(() -> {
				scene.getSystem(system).update(dt);
				synchronized (updated) {
					updated.set(system.id, true);
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
		protected final float dt;

		public SceneRender(Scene scene, UpdateOrder updateOrder, float dt) {
			this.scene = scene;
			this.updateOrder = updateOrder;
			this.dt = dt;
			updated = new BitSet(EngineSystem.size());
		}

		public void start() {
			List<EngineSystem<?, ?>> start = updateOrder.getStart();
			for (int i = 0; i < start.size(); i++)
				update(start.get(i));
		}

		protected void update(EngineSystem<?, ?> system) {
			scene.getSystem(system).render(dt);
			updated.set(system.id, true);
			checkUpdates(system);
		}

		protected void checkUpdates(EngineSystem<?, ?> system) {
			for (EngineSystem<?, ?> currentAfter : updateOrder.getAfter(system))
				checkUpdate(currentAfter);
		}

		protected void checkUpdate(EngineSystem<?, ?> system) {
			for (EngineSystem<?, ?> before : updateOrder.getBefore(system))
				if (!updated.get(before.id))
					return;
			update(system);
		}
	}

	public static class Info {

		public static String operatingSystem() {
			return System.getProperty("os.name");
		}

		public static String version() {
			return System.getProperty("os.version");
		}

		public static String architecture() {
			return System.getProperty("os.arch");
		}

		public static int bitArchitecture() {
			return Integer.parseInt(System.getProperty("sun.arch.data.model"));
		}

		public static int processorsSize() {
			return Runtime.getRuntime().availableProcessors();
		}

		public static long pageSize() {
			return MemoryUtil.PAGE_SIZE;
		}

		public static long cacheLineSize() {
			return MemoryUtil.CACHE_LINE_SIZE;
		}

		public static long freeMemory() {
			return Runtime.getRuntime().freeMemory();
		}

		public static long totalMemory() {
			return Runtime.getRuntime().totalMemory();
		}

		public static long maxMemory() {
			return Runtime.getRuntime().maxMemory();
		}
	}
}
