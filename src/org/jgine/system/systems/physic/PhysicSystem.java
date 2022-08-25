package org.jgine.system.systems.physic;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.other.Property;
import org.jgine.system.EngineSystem;

public class PhysicSystem extends EngineSystem {

	private float gravity = -1000.0f;
	private float airResistanceFactor = 0.99f;

	public PhysicSystem() {
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
		Object hasGravity = data.get("hasGravity");
		if (hasGravity != null && hasGravity instanceof Boolean)
			object.hasGravity = (boolean) hasGravity;

		Object stiffness = data.get("stiffness");
		if (stiffness != null && stiffness instanceof Number)
			object.stiffness = ((Number) stiffness).floatValue();

		Object acceleration = data.get("acceleration");
		if (acceleration != null && acceleration instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> accelerationMap = (Map<String, Object>) acceleration;
			object.accelerate(((Number) accelerationMap.getOrDefault("x", 0)).floatValue(),
					((Number) accelerationMap.getOrDefault("y", 0)).floatValue(),
					((Number) accelerationMap.getOrDefault("z", 0)).floatValue());
		}
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
