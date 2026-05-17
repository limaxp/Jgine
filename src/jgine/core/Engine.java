package jgine.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.eclipse.jdt.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jgine.core.GameLoop.FixedTickGameLoop;
import jgine.core.input.Input;
import jgine.core.sound.SoundManager;
import jgine.core.window.DisplayManager;
import jgine.core.window.Window;
import jgine.net.game.ConnectionManager;
import jgine.render.OpenGL;
import jgine.render.RenderConfiguration;
import jgine.render.Renderer;
import jgine.system.ai.AiSystem;
import jgine.system.camera.CameraScene;
import jgine.system.camera.CameraSystem;
import jgine.system.collision.CollisionSystem;
import jgine.system.graphic.Graphic2DSystem;
import jgine.system.graphic.GraphicSystem;
import jgine.system.input.InputSystem;
import jgine.system.light.LightScene;
import jgine.system.light.LightSystem;
import jgine.system.particle.ParticleSystem;
import jgine.system.physic.PhysicSystem;
import jgine.system.script.ScriptSystem;
import jgine.system.tileMap.TileMapSystem;
import jgine.system.transform.TransformSystem;
import jgine.system.ui.UISystem;
import jgine.utils.Benchmark;
import jgine.utils.Options;
import jgine.utils.Options.OptionFile;
import jgine.utils.collection.list.IdentityArrayList;
import jgine.utils.loader.ResourceManager;
import jgine.utils.registry.Registry;
import jgine.utils.scheduler.Scheduler;
import jgine.utils.scheduler.ThreadPool;

/**
 * The Base Engine class. You can extend this class to override some methods.
 * After calling the constructor all internal systems are initialized and ready
 * to use. Call the start() method to start the game loop created by
 * createGameLoop(). Use shutdown() method to stop game loop and free engine
 * systems.
 */
public class Engine {

	public static final TransformSystem TRANSFORM_SYSTEM = new TransformSystem();
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

	public static final int TRANSFORM = TRANSFORM_SYSTEM.id;
	public static final int PHYSIC = PHYSIC_SYSTEM.id;
	public static final int CAMERA = CAMERA_SYSTEM.id;
	public static final int COLLISION = COLLISION_SYSTEM.id;
	public static final int GRAPHIC = GRAPHIC_SYSTEM.id;
	public static final int GRAPHIC_2D = GRAPHIC_2D_SYSTEM.id;
	public static final int INPUT = INPUT_SYSTEM.id;
	public static final int LIGHT = LIGHT_SYSTEM.id;
	public static final int PARTICLE = PARTICLE_SYSTEM.id;
	public static final int SCRIPT = SCRIPT_SYSTEM.id;
	public static final int TILEMAP = TILEMAP_SYSTEM.id;
	public static final int UI = UI_SYSTEM.id;
	public static final int AI = AI_SYSTEM.id;

	public static final UpdateOrder UPDATE_ORDER = new UpdateOrder();
	public static final IntList RENDER_ORDER = new IntArrayList();

	static {
		UPDATE_ORDER.add(TRANSFORM);
		UPDATE_ORDER.add(INPUT, TRANSFORM);
		UPDATE_ORDER.add(AI, INPUT);
		UPDATE_ORDER.add(SCRIPT, AI);
		UPDATE_ORDER.add(COLLISION, SCRIPT);
		UPDATE_ORDER.add(PHYSIC, COLLISION);

		RENDER_ORDER.add(TILEMAP);
		RENDER_ORDER.add(GRAPHIC);
		RENDER_ORDER.add(GRAPHIC_2D);
		RENDER_ORDER.add(PARTICLE);
		RENDER_ORDER.add(COLLISION);
		RENDER_ORDER.add(AI);
		RENDER_ORDER.add(UI);
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
	private long tick;
	private long drawCalls;

	public Engine(String name, boolean window) {
		this(name, window, new FixedTickGameLoop(20));
	}

	public Engine(String name, boolean window, GameLoop gameLoop) {
		instance = this;
		this.name = name;
		sceneMap = new ConcurrentHashMap<String, Scene>();
		sceneIdMap = new ConcurrentHashMap<Integer, Scene>();
		scenes = new IdentityArrayList<Scene>();
		renderConfigs = new IdentityArrayList<RenderConfiguration>();
		this.gameLoop = gameLoop;
		gameLoop.setUpdateFunction(this::update);
		SoundManager.init();
		DisplayManager.init();
		Registry.init();
		if (window)
			createWindow();
		ResourceManager.loadResource("assets");
	}

	private void terminate() {
		ConnectionManager.terminate();
		ThreadPool.shutdown();
		for (Scene scene : scenes)
			scene.free();
		ResourceManager.terminate();
		if (hasWindow())
			deleteWindow();
		DisplayManager.terminate();
		SoundManager.terminate();
		OptionFile.save();
		gameLoop = null;
	}

	private void createWindow() {
		window = new Window(name);
		Input.setWindow(window);
		OpenGL.init();
		renderConfigs.add(RenderConfiguration.create(0, 0, 1, 1));
		gameLoop.setRenderFunction(this::render);
	}

	private void deleteWindow() {
		Renderer.terminate();
		for (RenderConfiguration renderConfig : renderConfigs)
			renderConfig.close();
		OpenGL.terminate();
		window.delete();
	}

	public GameLoop getGameLoop() {
		return gameLoop;
	}

	public void start() {
		isRunning = true;
		while (checkStatus()) {
			gameLoop.run();
			DisplayManager.pollGLFWEvents();
			Input.poll();
		}
		terminate();
	}

	private boolean checkStatus() {
		if (window != null && window.shouldClose())
			shutdown();
		return isRunning;
	}

	private void update(float dt) {
		tick++;
		ConnectionManager.update();
		Benchmark.start("update");
		for (Scene scene : scenes)
			if (!scene.isPaused())
				updateScene(scene, dt);
		Benchmark.stop("update");
		Scheduler.update();
		Input.update();
		SoundManager.update();
	}

	private void render(float dt) {
		Renderer.update(dt);
		Benchmark.start("render");
		for (Scene scene : scenes)
			if (!scene.isPaused())
				renderScene(scene, dt);
		Renderer.draw(renderConfigs);
		drawCalls = Renderer.getDrawCalls();
		Benchmark.stop("render");
		window.swapBuffers();
	}

	private void updateScene(Scene scene, float dt) {
		new UpdateTask(scene, scene.updateOrder, dt).start();
	}

	private void renderScene(Scene scene, float dt) {
		LightScene lightScene = scene.getSystem(LIGHT);
		if (lightScene != null)
			Renderer.setLights(lightScene);

		((CameraScene) scene.getSystem(CAMERA)).forEach((camera) -> {
			Renderer.setCamera(camera);
			camera.getRenderTarget().clear();
			for (int system : scene.renderOrder)
				scene.getSystem(system).render(dt);
		});

		Renderer.setRenderTarget(getRenderConfig().getRenderTarget());
		for (int system : scene.renderOrder)
			scene.getSystem(system).onRender(dt);
		Renderer.setRenderTarget(null);
	}

	public @Nullable Window getWindow() {
		return window;
	}

	public boolean hasWindow() {
		return window != null;
	}

	Scene addScene(Scene scene) {
		sceneMap.put(scene.name, scene);
		sceneIdMap.put(scene.id, scene);
		Scheduler.runTask(() -> scenes.add(scene));
		return scene;
	}

	boolean deleteScene(Scene scene) {
		sceneMap.remove(scene.name);
		sceneIdMap.remove(scene.id);
		Scheduler.runTask(() -> {
			scenes.remove(scene);
			scene.free();
		});
		return true;
	}

	public Collection<Scene> getScenes() {
		return scenes;
	}

	public Scene getScene(String name) {
		return sceneMap.get(name);
	}

	public Scene getScene(int id) {
		return sceneIdMap.get(id);
	}

	public Scene getScenePerIndex(int index) {
		return scenes.get(index);
	}

	public int getFps() {
		return gameLoop.getFps();
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void shutdown() {
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

	public RenderConfiguration getRenderConfig() {
		return renderConfigs.get(0);
	}

	public RenderConfiguration getRenderConfig(int index) {
		return renderConfigs.get(index);
	}

	public int getRenderConfigSize() {
		return renderConfigs.size();
	}

	public long getTick() {
		return tick;
	}

	public long getDrawCalls() {
		return drawCalls;
	}

	public static class UpdateTask {

		public final Scene scene;
		public final float dt;
		private final UpdateOrder order;
		private final AtomicIntegerArray flags;
		private final AtomicInteger amount;
		private final Thread thread;

		public UpdateTask(Scene scene, UpdateOrder order, float dt) {
			this.scene = scene;
			this.order = order;
			this.dt = dt;
			this.flags = new AtomicIntegerArray(Registry.SYSTEM.size());
			this.amount = new AtomicInteger(order.size());
			this.thread = Thread.currentThread();
		}

		public void start() {
			for (int i : order.getStart())
				update(i);

			while (amount.get() > 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}

		private void update(int system) {
			if (Options.DEBUG)
				Benchmark.start(scene.getSystem(system));

			if (Options.SYNCHRONIZED)
				scene.getSystem(system).update(this);
			else
				ThreadPool.execute(() -> scene.getSystem(system).update(this));
		}

		public void finish(int system) {
			if (Options.DEBUG)
				Benchmark.stop(scene.getSystem(system));

			if (amount.decrementAndGet() <= 0) {
				if (!Options.SYNCHRONIZED)
					thread.interrupt();
				return;
			}

			flags.set(system, 2);
			for (int child : order.getChilds(system))
				check(child);
		}

		private void check(int system) {
			if (flags.get(system) != 0)
				return;

			for (int parent : order.getParents(system))
				if (flags.get(parent) != 2)
					return;

			if (flags.compareAndSet(system, 0, 1))
				update(system);
		}
	}
}
