package org.jgine.system.systems.collision;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	public CollisionScene(CollisionSystem system, Scene scene) {
		super(system, scene, Collider.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Collider object) {
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
		if (!system.showHitBox())
			return;
		Renderer.setShader(Renderer.BASIC_SHADER);
		Renderer.enableDepthTest();

		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer.setCamera(camera);
		for (int i = 0; i < size; i++)
			objects[i].render(entities[i].transform.getPosition());

		Renderer.disableDepthTest();
	}
}
