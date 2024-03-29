package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.utils.scheduler.TaskHelper;

public class PhysicScene extends EntityListSystemScene<PhysicSystem, PhysicObject> {

	static {
		UpdateManager.addTransformPosition((entity, dx, dy, dz) -> {
			PhysicObject physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
			if (physic != null)
				physic.movePosition(dx, dy, dz);
		});
	}

	private float gravity;
	private float airResistanceFactor;
	private float dt;

	public PhysicScene(PhysicSystem system, Scene scene) {
		super(system, scene, PhysicObject.class);
		this.gravity = system.getGravity();
		this.airResistanceFactor = system.getAirResistanceFactor();
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, PhysicObject object) {
		Transform transform = entity.transform;
		object.initPosition(transform.getX(), transform.getY(), transform.getZ());
	}

	@Override
	public void update(float dt) {
		this.dt = dt;
		TaskHelper.execute(size, this::updatePositions);
	}

	@Override
	public void render(float dt) {
	}

	@Override
	public PhysicObject load(DataInput in) throws IOException {
		PhysicObject object = new PhysicObject();
		object.load(in);
		return object;
	}

	@Override
	public void save(PhysicObject object, DataOutput out) throws IOException {
		object.save(out);
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public float getGravity() {
		return gravity;
	}

	public void setAirResistanceFactor(float airResistanceFactor) {
		this.airResistanceFactor = airResistanceFactor;
	}

	public float getAirResistanceFactor() {
		return airResistanceFactor;
	}

	private void updatePositions(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			PhysicObject object = objects[index];
			if (object.updatePosition(dt, gravity, airResistanceFactor)) {
				Entity entity = entities[index];
				Transform transform = entity.transform;
				transform.setPositionIntern(object.x, object.y, object.z);
				UpdateManager.getPhysicPosition().accept(entity, transform.getX() - object.getOldX(),
						transform.getY() - object.getOldY(), transform.getZ() - object.getOldZ());
			}
		}
	}
}