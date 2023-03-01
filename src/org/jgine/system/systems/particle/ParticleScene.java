package org.jgine.system.systems.particle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.system.data.TransformListSystemScene;
import org.jgine.utils.scheduler.TaskHelper;

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
	public void update(float dt) {
		TaskHelper.execute(size, this::updateParticle);
	}

	private void updateParticle(int index, int size) {
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

	@Override
	public ParticleObject load(DataInput in) throws IOException {
		ParticleObject object = new ParticleObject();
		return object;
	}

	@Override
	public void save(ParticleObject object, DataOutput out) throws IOException {
	}
}
