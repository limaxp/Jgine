package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.UpdateManager;
import org.jgine.system.data.ObjectSystemScene.EntitySystemScene;
import org.jgine.utils.scheduler.Job;

public class PhysicScene extends EntitySystemScene<PhysicSystem, PhysicObject> {

	static {
		UpdateManager.addTransformPosition((entity, dx, dy, dz) -> {
			entity.forSystems(Engine.PHYSIC_SYSTEM, (PhysicObject physic) -> physic.movePosition(dx, dy, dz));
		});
	}

	private float gravity;
	private float airResistanceFactor;
	private float dt;

	public PhysicScene(PhysicSystem system, Scene scene) {
		super(system, scene, PhysicObject.class, 100000);
		this.gravity = system.getGravity();
		this.airResistanceFactor = system.getAirResistanceFactor();
	}

	@Override
	public void free() {
	}

	@Override
	public void onInit(Entity entity, PhysicObject object) {
		Transform transform = entity.transform;
		object.initPosition(transform.getX(), transform.getY(), transform.getZ());
	}

	@Override
	public void onAdd(Entity entity, PhysicObject object) {
		onInit(entity, object);
	}

	@Override
	public void update(UpdateTask update) {
		this.dt = update.dt;
		Job.region(size(), this::updatePosition, () -> update.finish(system));
	}

	@Override
	protected void saveData(PhysicObject object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected PhysicObject loadData(DataInput in) throws IOException {
		PhysicObject object = new PhysicObject();
		object.load(in);
		return object;
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

	private void updatePosition(int index) {
		PhysicObject object = get(index);
		if (object.updatePosition(dt, gravity, airResistanceFactor)) {
			Entity entity = getEntity(index);
			Transform transform = entity.transform;
			transform.setPositionIntern(object.x, object.y, object.z);
			UpdateManager.getPhysicPosition().accept(entity, transform.getX() - object.getOldX(),
					transform.getY() - object.getOldY(), transform.getZ() - object.getOldZ());
		}
	}
}