package jgine.system.camera;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Scene;
import jgine.system.EngineSystem;
import jgine.utils.Handle;
import jgine.utils.scheduler.Service;

public class CameraSystem extends EngineSystem<CameraSystem, Camera> {

	private Camera mainCamera;

	public CameraSystem() {
		super("camera");
		Service.register("camera", this, Handle.var(CameraSystem.class, "mainCamera", Camera.class));
	}

	@Override
	public CameraScene createScene(Scene scene) {
		return new CameraScene(this, scene);
	}

	@Override
	public Camera load(Map<String, Object> data) {
		Camera camera = new Camera(true);
		camera.load(data);
		return camera;
	}

	void registerCamera(Camera camera) {
		if (mainCamera == null)
			mainCamera = camera;
	}

	void unregisterCamera(Camera camera) {
		if (mainCamera == camera)
			mainCamera = null;
	}

	@Nullable
	public Camera getMainCamera() {
		return mainCamera;
	}
}
