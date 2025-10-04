package org.jgine.system.systems.camera;

import java.util.Map;

import javax.annotation.Nullable;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;
import org.jgine.utils.Service;
import org.jgine.utils.collection.function.Property;

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
