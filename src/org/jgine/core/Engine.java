package org.jgine.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

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
import org.jgine.utils.Benchmark;
import org.jgine.utils.Service;
import org.jgine.utils.collection.list.IdentityArrayList;
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
	static final List<EngineSystem<?, ?>> DEFAULT_RENDER_ORDER = new IdentityArrayList<EngineSystem<?, ?>>();

	static {
		DEFAULT_UPDATE_ORDER.add(INPUT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(PHYSIC_SYSTEM, INPUT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(COLLISION_SYSTEM, PHYSIC_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(AI_SYSTEM, COLLISION_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(SCRIPT_SYSTEM, AI_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(TILEMAP_SYSTEM, SCRIPT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(GRAPHIC_SYSTEM, SCRIPT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(GRAPHIC_2D_SYSTEM, SCRIPT_SYSTEM);
		DEFAULT_UPDATE_ORDER.add(UI_SYSTEM, SCRIPT_SYSTEM);

		DEFAULT_RENDER_ORDER.add(TILEMAP_SYSTEM);
		DEFAULT_RENDER_ORDER.add(GRAPHIC_SYSTEM);
		DEFAULT_RENDER_ORDER.add(GRAPHIC_2D_SYSTEM);
		DEFAULT_RENDER_ORDER.add(PARTICLE_SYSTEM);
		DEFAULT_RENDER_ORDER.add(COLLISION_SYSTEM);
		DEFAULT_RENDER_ORDER.add(AI_SYSTEM);
		DEFAULT_RENDER_ORDER.add(UI_SYSTEM);
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
		Benchmark.start("update");
		for (Scene scene : scenes)
			if (!scene.isPaused())
				updateScene(scene, dt);
		Benchmark.stop("update");
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
		Benchmark.start("render");
		for (Scene scene : scenes)
			if (!scene.isPaused())
				renderScene(scene, dt);
		Renderer.draw(renderConfigs);
		Benchmark.stop("render");
		window.swapBuffers();
		onRender();
	}

	protected void onRender() {
	}

	private final void updateScene(Scene scene, float dt) {
		if (!scene.hasUpdateOrder())
			return;

		if (Options.SYNCHRONIZED)
			new UpdateTask(scene, scene.getUpdateOrder(), dt).start();
		else
			new ConcurrentUpdateTask(scene, scene.getUpdateOrder(), dt).start();
	}

	private final void renderScene(Scene scene, float dt) {
		if (!scene.hasRenderOrder())
			return;

		LightScene lightScene = scene.getSystem(LIGHT_SYSTEM);
		if (lightScene != null)
			Renderer.setLights(lightScene);

		for (EngineSystem<?, ?> system : scene.getRenderOrder())
			scene.getSystem(system).preRender(dt);

		((CameraScene) scene.getSystem(CAMERA_SYSTEM)).forEach((camera) -> {
			Renderer.setCamera(camera);
			camera.getRenderTarget().clear();
			for (EngineSystem<?, ?> system : scene.getRenderOrder())
				scene.getSystem(system).render(dt);
		});
		Renderer.setRenderTarget(null);
	}

	public final Window getWindow() {
		return window;
	}

	public final Scene createScene(String name) {
		return createScene(name, DEFAULT_UPDATE_ORDER, DEFAULT_RENDER_ORDER, EngineSystem.values());
	}

	public final Scene createScene(String name, UpdateOrder updateOrder, List<EngineSystem<?, ?>> renderOrder,
			EngineSystem<?, ?>... systems) {
		Scene scene = initScene(name);
		scene.setSystems(systems);
		scene.setUpdateOrder(updateOrder);
		scene.setRenderOrder(renderOrder);
		return scene;
	}

	public final Scene createScene(String name, UpdateOrder updateOrder, List<EngineSystem<?, ?>> renderOrder,
			Collection<EngineSystem<?, ?>> systems) {
		Scene scene = initScene(name);
		scene.setSystems(systems);
		scene.setUpdateOrder(updateOrder);
		scene.setRenderOrder(renderOrder);
		return scene;
	}

	private final Scene initScene(String name) {
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

	public static class UpdateTask {

		public final Scene scene;
		protected final UpdateOrder order;
		protected final AtomicIntegerArray flags;
		public final float dt;

		public UpdateTask(Scene scene, UpdateOrder order, float dt) {
			this.scene = scene;
			this.order = order;
			flags = new AtomicIntegerArray(EngineSystem.size());
			this.dt = dt;
		}

		public void finish(EngineSystem<?, ?> system) {
		}

		public void start() {
			List<EngineSystem<?, ?>> start = order.getStart();
			for (int i = 0; i < start.size(); i++)
				update(start.get(i));
		}

		protected void update(EngineSystem<?, ?> system) {
			if (Options.DEBUG)
				Benchmark.start(scene.getSystem(system));
			scene.getSystem(system).update(this);
			if (Options.DEBUG)
				Benchmark.stop(scene.getSystem(system));
			check(system);
		}

		protected final void check(EngineSystem<?, ?> system) {
			flags.set(system.id, 1);
			for (EngineSystem<?, ?> after : order.getAfter(system))
				subCheck(after);
		}

		private final void subCheck(EngineSystem<?, ?> system) {
			if (flags.get(system.id) == 1)
				return;

			for (EngineSystem<?, ?> before : order.getBefore(system))
				if (flags.get(before.id) == 0)
					return;
			update(system);
		}

		public final boolean isDone(EngineSystem<?, ?> system) {
			return flags.get(system.id) == 1;
		}
	}

	public static class ConcurrentUpdateTask extends UpdateTask {

		protected final AtomicInteger amount;
		protected final Thread thread;

		public ConcurrentUpdateTask(Scene scene, UpdateOrder order, float dt) {
			super(scene, order, dt);
			amount = new AtomicInteger(order.size());
			thread = Thread.currentThread();
		}

		@Override
		public final void start() {
			super.start();
			while (amount.get() > 1) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
			}
		}

		@Override
		protected final void update(EngineSystem<?, ?> system) {
			if (Options.DEBUG)
				Benchmark.start(scene.getSystem(system));
			ThreadPool.execute(() -> scene.getSystem(system).update(this));
		}

		@Override
		public final void finish(EngineSystem<?, ?> system) {
			if (Options.DEBUG)
				Benchmark.stop(scene.getSystem(system));
			if (amount.decrementAndGet() <= 0)
				thread.interrupt();
			else
				check(system);
		}
	}
}
