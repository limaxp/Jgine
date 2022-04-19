package org.jgine.system.systems.physic;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.other.Property;
import org.jgine.system.EngineSystem;

public class PhysicSystem extends EngineSystem {

	private float gravity = 0.01f;
	private float airResistance = 0.9f;

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
		ServiceManager.register("airResistance", new Property<Float>() {

			@Override
			public void setValue(Float obj) {
				airResistance = obj;
			}

			@Override
			public Float getValue() {
				return airResistance;
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
		
		Object velocity = data.get("velocity");
		if (velocity != null && velocity instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> velocityMap = (Map<String, Object>) velocity;
			object.setVelocity(new Vector3f(((Number) velocityMap.getOrDefault("x", 0)).floatValue(),
					((Number) velocityMap.getOrDefault(
							"y", 0)).floatValue(), ((Number) velocityMap.getOrDefault("z", 0)).floatValue()));
		}
		Object acceleration = data.get("acceleration");
		if (acceleration != null && acceleration instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> accelerationMap = (Map<String, Object>) acceleration;
			object.addVelocity(new Vector3f(((Number) accelerationMap.getOrDefault("x", 0)).floatValue(),
					((Number) accelerationMap
							.getOrDefault("y", 0)).floatValue(), ((Number) accelerationMap.getOrDefault("z", 0))
									.floatValue()));
		}
		return object;
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
