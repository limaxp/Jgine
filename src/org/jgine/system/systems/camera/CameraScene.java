package org.jgine.system.systems.camera;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
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
		if (object.getRenderTarget() == null)
			object.setRenderTarget(scene.engine.getRenderConfig().getRenderTarget());
		system.addCamera(object);
	}

	@Override
	public Camera removeObject(int index) {
		Camera object = super.removeObject(index);
		system.removeCamera(object);
		return object;
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render(float dt) {
	}

	@Override
	public Entity getEntity(int index) {
		return getTransform(index).getEntity();
	}

	@Override
	public Transform getTransform(int index) {
		return objects[index].transform;
	}

	@Override
	public Camera load(DataInput in) throws IOException {
		Camera object = new Camera();
		object.load(in);
		return object;
	}

	@Override
	public void save(Camera object, DataOutput out) throws IOException {
		object.save(out);
	}
}
