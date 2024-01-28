package org.jgine.system.systems.camera;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;
import org.jgine.utils.function.Property;

public class CameraSystem extends EngineSystem {

	private Camera mainCamera;

	public CameraSystem() {
		super("camera");
		ServiceManager.register("camera", new Property<Camera>() {

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
	public SystemScene<?, ?> createScene(Scene scene) {
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
