package org.jgine.system.systems.collision;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.manager.TaskManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.script.EventManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.physic.PhysicSystem;
import org.jgine.system.systems.script.IScript;
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
		TaskManager.execute(size, this::update);
	}

	public void update(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			Collider object = objects[index];
			for (int j = 0; j < this.size; j++) {
				Collider target = objects[j];
				if (object == target)
					continue;
				if (object.checkCollision(target)) {
					Entity entity = entities[index];
					Collision collision = new Collision();
					collision.entity = entities[j];
					EventManager.callEvent(entity, collision, IScript::onCollision);
					PhysicObject physicObject = entity.getSystem(PhysicSystem.class);
					physicObject.setVelocity(Vector3f.mult(physicObject.getVelocity(), -2));
				}
			}
		}
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
