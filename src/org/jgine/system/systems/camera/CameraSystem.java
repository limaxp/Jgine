package org.jgine.system.systems.camera;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.other.Property;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public class CameraSystem extends EngineSystem {

	private Camera mainCamera;

	public CameraSystem() {
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
		return new Camera(data);
	}

	public void setCamera(Camera Camera) {
		this.mainCamera = Camera;
	}

	public Camera getCamera() {
		return mainCamera;
	}
}
