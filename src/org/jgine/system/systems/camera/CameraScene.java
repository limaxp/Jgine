package org.jgine.system.systems.camera;

import org.eclipse.jdt.annotation.Nullable;
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
		object.transform = entity.transform;
		system.addCamera(object);
	}

	@Override
	@Nullable
	public Camera removeObject(Camera object) {
		system.removeCamera(object);
		return super.removeObject(object);
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
	}
}
