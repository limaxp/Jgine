package org.jgine.system.systems.particle;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.TaskManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.TransformListSystemScene;

public class ParticleScene extends TransformListSystemScene<ParticleSystem, ParticleObject> {

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
		for (int i = 0; i < size; i++) {
			ParticleObject object = objects[i];
			if (object.isActive())
				Renderer.render(transforms[i].getMatrix(), object, object.material);
		}
		Renderer.disableDepthTest();
	}
}
