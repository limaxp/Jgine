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

	@SuppressWarnings("unchecked")
	@Override
	public Camera load(Map<String, Object> data) {
		Camera object = new Camera();
		Object perspective = data.get("perspective");
		if (perspective != null && perspective instanceof Map)
			object.setPerspective(new Perspective((Map<String, Object>) perspective));
		Object orthographic = data.get("orthographic");
		if (orthographic != null && orthographic instanceof Map)
			object.setOrthographic(new Orthographic((Map<String, Object>) orthographic));
		return object;
	}

	public void setCamera(Camera Camera) {
		this.mainCamera = Camera;
	}

	public Camera getCamera() {
		return mainCamera;
	}
}
