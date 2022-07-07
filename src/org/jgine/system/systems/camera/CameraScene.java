package org.jgine.system.systems.camera;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ListSystemScene;

public class CameraScene extends ListSystemScene<CameraSystem, Camera> {

	public CameraScene(CameraSystem system, Scene scene) {
		super(system, scene, Camera.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Camera object) {
		if (system.getCamera() == null)
			system.setCamera(object);
		object.transform = entity.transform;
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
	}
}
