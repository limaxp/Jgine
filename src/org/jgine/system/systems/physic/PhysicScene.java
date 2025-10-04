package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.Scene;
import org.jgine.system.data.ObjectSystemScene.EntitySystemScene;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.scheduler.Job;

public class PhysicScene extends EntitySystemScene<PhysicSystem, PhysicObject> {

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
		object.velX = object.velX + object.motX * dt * dt;
		object.velY = object.velY + (object.motY + object.getGravity() * gravity) * dt * dt;
		object.velZ = object.velZ + object.motZ * dt * dt;

		object.velX *= airResistanceFactor;
		object.velY *= airResistanceFactor;
		object.velZ *= airResistanceFactor;

		object.motX = 0;
		object.motY = 0;
		object.motZ = 0;

		if (FastMath.abs(object.velX) + FastMath.abs(object.velY) + FastMath.abs(object.velZ) > 0.001f) {
			getEntity(index).transform.movePosition(object.velX, object.velY, object.velZ);
			object.setMoving(true);
		} else
			object.setMoving(false);
	}
}