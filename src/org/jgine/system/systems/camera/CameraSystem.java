package org.jgine.system.systems.camera;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;
import org.jgine.utils.Service;
import org.jgine.utils.function.Property;

public class CameraSystem extends EngineSystem<CameraSystem, Camera> {

	private Camera mainCamera;

	public CameraSystem() {
		super("camera");
		Service.register("camera", new Property<Camera>() {

			@Override
			public void setValue(Camera obj) {
				mainCamera = obj;
			}

			@Override
			public Camera getValue() {
				return mainCamera;
			}
		});
	}

	@Override
	public CameraScene createScene(Scene scene) {
		return new CameraScene(this, scene);
	}

	@Override
	public Camera load(Map<String, Object> data) {
		Camera camera = new Camera();
		camera.load(data);
		return camera;
	}

	protected void registerCamera(Camera camera) {
		if (mainCamera == null)
			mainCamera = camera;
	}

	public Camera getMainCamera() {
		return mainCamera;
	}
}
