package org.jgine.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.core.UpdateOrder.SynchronizedRenderTask;
import org.jgine.core.UpdateOrder.SynchronizedUpdateTask;
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
import org.jgine.utils.options.OptionFile;
import org.jgine.utils.options.Options;
import org.jgine.utils.scheduler.Scheduler;
import org.jgine.utils.scheduler.ThreadPool;
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
	 * RenderQueue needs optimization!
	 * 
	 * Implement multi thread system update!
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

	static final UpdateOrder DEFAULT_UPDATE_ORDER = new UpdateOrder();
	static final UpdateOrder DEFAULT_RENDER_ORDER = new UpdateOrder();

	static {
		DEFAULT_UPDATE_ORDER.add(INPUT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(PHYSIC_SYSTEM, INPUT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(COLLISION_SYSTEM, PHYSIC_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(SCRIPT_SYSTEM, COLLISION_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(AI_SYSTEM, SCRIPT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(GRAPHIC_SYSTEM, COLLISION_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(GRAPHIC_2D_SYSTEM, COLLISION_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(PARTICLE_SYSTEM, COLLISION_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(TILEMAP_SYSTEM, COLLISION_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(UI_SYSTEM, COLLISION_SYSTEM);

		DEFAULT_RENDER_ORDER.add(TILEMAP_SYSTEM);
		DEFAULT_RENDER_ORDER.add(GRAPHIC_SYSTEM, TILEMAP_SYSTEM);
		DEFAULT_RENDER_ORDER.add(GRAPHIC_2D_SYSTEM, TILEMAP_SYSTEM);
		DEFAULT_RENDER_ORDER.add(PARTICLE_SYSTEM, GRAPHIC_SYSTEM, GRAPHIC_2D_SYSTEM);
		DEFAULT_RENDER_ORDER.add(COLLISION_SYSTEM, PARTICLE_SYSTEM);
		DEFAULT_RENDER_ORDER.add(AI_SYSTEM, PARTICLE_SYSTEM);
		DEFAULT_RENDER_ORDER.add(UI_SYSTEM, COLLISION_SYSTEM, AI_SYSTEM);
	}

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
		gameLoop.setRenderFunction(this::render);
	}

	private final void terminate() {
		for (Scene scene : scenes)
			scene.free();
		ThreadPool.shutdown();
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

	private final void update(float dt) {
		ConnectionManager.update();
		for (Scene scene : scenes)
			if (!scene.isPaused())
				updateScene(scene, dt);
		Service.distributeChanges();
		Scheduler.update();
		ThreadPool.execute(Scheduler::updateAsync);
		Input.update();
		SoundManager.update();
		onUpdate();
	}

	protected void onUpdate() {
	}

	private final void render(float dt) {
		Renderer.update(dt);
		for (Scene scene : scenes)
			if (!scene.isPaused())
				renderScene(scene, dt);
		Renderer.draw(renderConfigs);
		window.swapBuffers();
		onRender();
	}

	protected void onRender() {
	}

	private final void updateScene(Scene scene, float dt) {
		if (!scene.hasUpdateOrder()) {
			for (SystemScene<?, ?> systemScene : scene.getSystems())
				systemScene.update(dt);

		} else {
			if (Options.SYNCHRONIZED)
				new SynchronizedUpdateTask(scene, scene.getUpdateOrder(), dt);
			else {
				new SynchronizedUpdateTask(scene, scene.getUpdateOrder(), dt);
			}
		}
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
			if (Options.SYNCHRONIZED)
				((CameraScene) scene.getSystem(CAMERA_SYSTEM)).forEach((camera) -> {
					Renderer.setCamera_UNSAFE(camera);
					new SynchronizedRenderTask(scene, scene.getRenderOrder(), dt);
				});
			else {
				((CameraScene) scene.getSystem(CAMERA_SYSTEM)).forEach((camera) -> {
					Renderer.setCamera_UNSAFE(camera);
					new SynchronizedRenderTask(scene, scene.getRenderOrder(), dt);
				});
			}
		}
	}

	public final Window getWindow() {
		return window;
	}

	public final Scene createScene(String name) {
		Scene scene = initScene(name, DEFAULT_UPDATE_ORDER, DEFAULT_RENDER_ORDER);
		scene.setSystems(EngineSystem.values());
		return scene;
	}

	public final Scene createScene(String name, EngineSystem<?, ?>... systems) {
		Scene scene = initScene(name, null, null);
		scene.setSystems(systems);
		return scene;
	}

	public final Scene createScene(String name, UpdateOrder updateOrder, UpdateOrder renderOrder,
			EngineSystem<?, ?>... systems) {
		Scene scene = initScene(name, updateOrder, renderOrder);
		scene.setSystems(systems);
		return scene;
	}

	private final Scene initScene(String name, UpdateOrder updateOrder, UpdateOrder renderOrder) {
		Scene scene = new Scene(this, name);
		sceneMap.put(name, scene);
		sceneIdMap.put(scene.id, scene);
		scene.setUpdateOrder(updateOrder);
		scene.setRenderOrder(renderOrder);
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

		public static int availableProcessors() {
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
