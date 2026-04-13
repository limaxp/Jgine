package jgine.system.physic;

import java.util.Map;

import jgine.core.Scene;
import jgine.system.EngineSystem;
import jgine.utils.Handle;
import jgine.utils.scheduler.Service;

public class PhysicSystem extends EngineSystem<PhysicSystem, PhysicObject> {

	public float gravity = -3000.0f;
	public float airResistanceFactor = 0.95f;

	public PhysicSystem() {
		super("physic");
		Service.register("gravity", this, Handle.var(PhysicSystem.class, "gravity", float.class));
		Service.register("airResistanceFactor", this,
				Handle.var(PhysicSystem.class, "airResistanceFactor", float.class));
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
}
