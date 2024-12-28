package org.jgine.system.systems.physic;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.system.EngineSystem;
import org.jgine.utils.function.Property;

public class PhysicSystem extends EngineSystem<PhysicSystem, PhysicObject> {

	private float gravity = -1000.0f;
	private float airResistanceFactor = 0.99f;

	public PhysicSystem() {
		super("physic");
		ServiceManager.register("gravity", new Property<Float>() {

			@Override
			public void setValue(Float obj) {
				gravity = obj;
			}

			@Override
			public Float getValue() {
				return gravity;
			}
		});
		ServiceManager.register("airResistanceFactor", new Property<Float>() {

			@Override
			public void setValue(Float obj) {
				airResistanceFactor = obj;
			}

			@Override
			public Float getValue() {
				return airResistanceFactor;
			}
		});
	}

	@Override
	public PhysicScene createScene(Scene scene) {
		return new PhysicScene(this, scene);
	}

	@Override
	public PhysicObject load(Map<String, Object> data) {
		PhysicObject object = new PhysicObject();
		object.load(data);
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
}
