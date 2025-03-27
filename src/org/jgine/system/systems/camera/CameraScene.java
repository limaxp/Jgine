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
		for (int i = 0; i < size; i++)
			system.unregisterCamera(objects[i]);
	}

	@Override
	public void initObject(Entity entity, Camera object) {
		object.transform = entity.transform;
		if (object.getRenderTarget() == null)
			object.setRenderTarget(scene.engine.getRenderConfig().getRenderTarget());
		system.registerCamera(object);
	}

	@Override
	public Camera removeObject(int index) {
		Camera object = super.removeObject(index);
		system.unregisterCamera(object);
		return object;
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
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		for (int i = 0; i < size; i++) {
			Camera object = new Camera();
			object.load(in);
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			objects[i].save(out);
	}
}
