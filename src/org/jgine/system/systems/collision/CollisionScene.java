package org.jgine.system.systems.collision;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.transform.TransformSystem;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	public CollisionScene(CollisionSystem system, Scene scene) {
		super(system, scene, Collider.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Collider object) {
		object.transform = entity.getSystem(scene.getSystem(TransformSystem.class));
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
		Renderer.enableWireframeMode();

		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer.setCamera(camera);
		for (int i = 0; i < size; i++)
			objects[i].render();

		Renderer.disableDepthTest();
		Renderer.disableWireframeMode();
	}
}
