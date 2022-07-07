package org.jgine.system.systems.particle;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.manager.TaskManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.ListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;

public class ParticleScene extends ListSystemScene<ParticleSystem, ParticleObject> {

	public ParticleScene(ParticleSystem system, Scene scene) {
		super(system, scene, ParticleObject.class);
	}

	@Override
	public void free() {
		for (int i = 0; i < size; i++)
			objects[i].close();
	}

	@Override
	public void initObject(Entity entity, ParticleObject object) {
		object.transform = entity.transform;
	}

	@Override
	@Nullable
	public ParticleObject removeObject(ParticleObject object) {
		object.close();
		return super.removeObject(object);
	}

	@Override
	public void update() {
		TaskManager.execute(size, this::update);
	}

	private void update(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			ParticleObject object = objects[index];
			if (object.isActive())
				object.update();
		}
	}

	@Override
	public void render() {
		Renderer.setShader(Renderer.PARTICLE_SHADER);
		Renderer.enableDepthTest();

		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer.setCamera(camera);
		for (int i = 0; i < size; i++) {
			ParticleObject object = objects[i];
			if (object.isActive())
				Renderer.render(object.transform.getMatrix(), object, object.material);
		}

		Renderer.disableDepthTest();
	}
}
