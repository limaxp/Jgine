package org.jgine.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import org.jgine.core.gameLoop.FixedTickGameLoop;
import org.jgine.core.gameLoop.GameLoop;
import org.jgine.core.input.Input;
import org.jgine.core.manager.ResourceManager;
import org.jgine.core.manager.ServiceManager;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.manager.UpdateManager;
import org.jgine.core.window.DisplayManager;
import org.jgine.core.window.Window;
import org.jgine.misc.collection.list.IntList;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.misc.utils.options.OptionFile;
import org.jgine.misc.utils.options.Options;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.misc.utils.scheduler.TaskExecutor;
import org.jgine.net.game.ConnectionManager;
import org.jgine.render.RenderConfiguration;
import org.jgine.render.RenderTarget;
import org.jgine.render.Renderer;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.sound.SoundManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.collision.CollisionSystem;
import org.jgine.system.systems.graphic.Graphic2DSystem;
import org.jgine.system.systems.graphic.GraphicSystem;
import org.jgine.system.systems.input.InputSystem;
import org.jgine.system.systems.light.LightSystem;
import org.jgine.system.systems.particle.ParticleSystem;
import org.jgine.system.systems.physic.PhysicSystem;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.system.systems.tileMap.TileMapSystem;
import org.jgine.system.systems.ui.UISystem;

public abstract class Engine {

	public static final CameraSystem CAMERA_SYSTEM = SystemManager.register(new CameraSystem());
	public static final CollisionSystem COLLISION_SYSTEM = SystemManager.register(new CollisionSystem());
	public static final GraphicSystem GRAPHIC_SYSTEM = SystemManager.register(new GraphicSystem());
	public static final Graphic2DSystem GRAPHIC_2D_SYSTEM = SystemManager.register(new Graphic2DSystem());
	public static final InputSystem INPUT_SYSTEM = SystemManager.register(new InputSystem());
	public static final LightSystem LIGHT_SYSTEM = SystemManager.register(new LightSystem());
	public static final ParticleSystem PARTICLE_SYSTEM = SystemManager.register(new ParticleSystem());
	public static final PhysicSystem PHYSIC_SYSTEM = SystemManager.register(new PhysicSystem());
	public static final ScriptSystem SCRIPT_SYSTEM = SystemManager.register(new ScriptSystem());
	public static final TileMapSystem TILEMAP_SYSTEM = SystemManager.register(new TileMapSystem());
	public static final UISystem UI_SYSTEM = SystemManager.register(new UISystem());

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
		Input.setWindow(window);
		Renderer.init();
		renderConfigs.add(new RenderConfiguration());
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
		ResourceManager.terminate();
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

	public abstract void onUpdate();

	private final void update() {
		ConnectionManager.update();
		for (Scene scene : scenes)
			if (!scene.isPaused()) {
				UpdateManager.distributeChanges();
				updateScene(scene);
			}
		UpdateManager.distributeChanges();
		ServiceManager.distributeChanges();
		Scheduler.update();
		TaskExecutor.execute(Scheduler::updateAsync);
		Input.update();
		SoundManager.update();
		onUpdate();
	}

	private final void updateScene(Scene scene) {
		if (!scene.hasUpdateOrder()) {
			for (SystemScene<?, ?> systemScene : scene.getSystems())
				systemScene.update();
			return;
		}
		new SceneUpdate(scene, scene.getUpdateOrder()).start();
	}

	public abstract void onRender();

	private final void render() {
		renderCameras();
		renderFrames();
		window.swapBuffers();
		onRender();
	}

	private final void renderCameras() {
		// TODO this should not access systems!
		for (Camera camera : SystemManager.get(CameraSystem.class).getCameras()) {
			Scene scene = camera.getTransform().getEntity().scene;
			if (scene.isPaused())
				continue;
			Renderer.setCamera(camera);
			renderScene(scene);
		}
	}

	private final void renderFrames() {
		Renderer.setShader(Renderer.POST_PROCESS_SHADER);
		RenderTarget renderTarget = Renderer.getCamera().getRenderTarget();
		renderTarget.unbindRenderTarget();
		for (RenderConfiguration renderConfig : renderConfigs)
			UIRenderer.renderQuad(renderConfig.getMatrix(), new Material(renderConfig.getRenderTarget()));
		renderTarget.bindRenderTarget();
	}

	private final void renderScene(Scene scene) {
		if (!scene.hasRenderOrder()) {
			for (SystemScene<?, ?> systemScene : scene.getSystems())
				systemScene.render();
			return;
		}
		new SceneRender(scene, scene.getRenderOrder()).start();
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

	public List<RenderConfiguration> getRenderConfigs() {
		return renderConfigs;
	}

	public final RenderConfiguration getRenderConfig() {
		return renderConfigs.get(0);
	}

	private static class SceneUpdate extends SceneRender {

		protected int updatedSize;
		protected final CompletionService<Object> completionService;

		public SceneUpdate(Scene scene, UpdateOrder updateOrder) {
			super(scene, updateOrder);
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

		protected void update(int system) {
			if (Options.SYNCHRONIZED) {
				scene.getSystem(system).update();
				updated[system] = true;
				checkUpdate(updated, system);
				return;
			}
			completionService.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					scene.getSystem(system).update();
					boolean[] updatedCopy;
					synchronized (updated) {
						updated[system] = true;
						updatedCopy = updated.clone();
					}
					checkUpdate(updatedCopy, system);
					return system;
				}
			});
		}
	}

	private static class SceneRender {

		protected final Scene scene;
		protected final UpdateOrder updateOrder;
		protected final boolean[] updated;

		public SceneRender(Scene scene, UpdateOrder updateOrder) {
			this.scene = scene;
			this.updateOrder = updateOrder;
			updated = new boolean[SystemManager.getSize()];
		}

		public void start() {
			IntList start = updateOrder.getStart();
			for (int i = 0; i < start.size(); i++)
				update(start.getInt(i));
		}

		protected void update(int system) {
			scene.getSystem(system).render();
			updated[system] = true;
			checkUpdate(updated, system);
		}

		protected void checkUpdate(boolean[] updated, int system) {
			for (int currentAfter : updateOrder.getAfter(system)) {
				boolean finished = true;
				for (int before : updateOrder.getBefore(currentAfter)) {
					if (!updated[before]) {
						finished = false;
						break;
					}
				}
				if (finished)
					update(currentAfter);
			}
		}
	}
}
