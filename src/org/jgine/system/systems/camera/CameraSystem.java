package org.jgine.system.systems.camera;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.misc.other.Property;
import org.jgine.system.EngineSystem;
import org.jgine.system.SystemScene;

public class CameraSystem extends EngineSystem {

	private List<Camera> cameras;
	private List<Camera> cameras_view;
	private Camera mainCamera;

	public CameraSystem() {
		cameras = new FastArrayList<Camera>();
		cameras_view = Collections.unmodifiableList(cameras);
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

	protected void addCamera(Camera camera) {
		cameras.add(camera);
		if (mainCamera == null)
			mainCamera = camera;
	}

	protected void removeCamera(Camera camera) {
		cameras.remove(camera);
	}

	public List<Camera> getCameras() {
		return cameras_view;
	}

	public Camera getMainCamera() {
		return mainCamera;
	}
}
