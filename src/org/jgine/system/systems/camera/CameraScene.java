package org.jgine.system.systems.camera;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.data.ObjectSystemScene;

public class CameraScene extends ObjectSystemScene<CameraSystem, Camera> {

	public CameraScene(CameraSystem system, Scene scene) {
		super(system, scene, Camera.class, 32);
	}

	@Override
	public void free() {
		forEach(system::unregisterCamera);
	}

	@Override
	public void onInit(Entity entity, Camera object) {
		object.transform = entity.transform;
		if (object.getRenderTarget() == null)
			object.setRenderTarget(scene.engine.getRenderConfig().getRenderTarget());
		system.registerCamera(object);
	}

	@Override
	public void onRemove(Entity entity, Camera object) {
		system.unregisterCamera(object);
	}

	@Override
	public Entity getEntity(int index) {
		return getTransform(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return get(index).transform;
	}

	@Override
	protected void saveData(Camera object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected Camera loadData(DataInput in) throws IOException {
		Camera object = new Camera();
		object.load(in);
		return object;
	}
}
