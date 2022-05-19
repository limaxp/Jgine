package org.jgine.system.systems.physic;

import java.util.function.BiConsumer;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.TaskManager;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.transform.Transform;
import org.jgine.system.systems.transform.TransformSystem;

public class PhysicScene extends EntityListSystemScene<PhysicSystem, PhysicObject> {

	private final BiConsumer<Entity, Object> positionUpdate = (entity, pos) -> entity.getSystem(this)
			.setPosition((Vector3f) pos);

	private float gravity;
	private float airResistance;

	public PhysicScene(PhysicSystem system, Scene scene) {
		super(system, scene, PhysicObject.class);
		this.gravity = system.getGravity();
		this.airResistance = system.getAirResistance();
		UpdateManager.register(scene, "physicPosition", positionUpdate);
	}

	@Override
	public void free() {
		UpdateManager.unregister(scene, "physicPosition", positionUpdate);
	}

	@Override
	public void initObject(Entity entity, PhysicObject object) {
		Transform transform = entity.getSystem(scene.getSystem(TransformSystem.class));
		Vector3f pos = transform.getPosition();
		object.x = pos.x;
		object.y = pos.y;
		object.z = pos.z;
	}

	@Override
	public void update() {
		TaskManager.execute(size, this::update);
	}

	private void update(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			boolean update = false;
			PhysicObject object = objects[index];
			object.velX += object.motX;
			if (Math.abs(object.velX) > 0.01) {
				object.x += object.velX;
				object.velX *= airResistance;
				update = true;
			} else
				object.velX = 0;
			object.motX = 0;

			object.velY += object.motY;
			if (object.hasGravity)
				object.velY -= gravity;
			if (Math.abs(object.velY) > 0.01) {
				object.y += object.velY;
				object.velY *= airResistance;
				update = true;
			} else
				object.velY = 0;
			object.motY = 0;

			object.velZ += object.motZ;
			if (Math.abs(object.velZ) > 0.01) {
				object.z += object.velZ;
				object.velZ *= airResistance;
				update = true;
			} else
				object.velZ = 0;
			object.motZ = 0;

			if (update)
				UpdateManager.update(scene, "position", entities[index], new Vector3f(object.x, object.y, object.z));
		}
	}

	@Override
	public void render() {
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public float getGravity() {
		return gravity;
	}

	public void setAirResistance(float airResistance) {
		this.airResistance = airResistance;
	}

	public float getAirResistance() {
		return airResistance;
	}
}
