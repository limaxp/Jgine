package jgine.system.collision;

import java.util.Map;

import jgine.core.Registry;
import jgine.core.Scene;
import jgine.system.EngineSystem;
import jgine.utils.Handle;
import jgine.utils.scheduler.Service;

public class CollisionSystem extends EngineSystem<CollisionSystem, Collider> {

	public boolean showHitBox = false;
	public int steps = 1;

	public CollisionSystem() {
		super("collider");
		Service.register("showHitBox", this, Handle.var(CollisionSystem.class, "showHitBox", boolean.class));
		Service.register("collisionSteps", this, Handle.var(CollisionSystem.class, "steps", int.class));
	}

	@Override
	public CollisionScene createScene(Scene scene) {
		return new CollisionScene(this, scene);
	}

	@Override
	public Collider load(Map<String, Object> data) {
		Collider collider;
		ColliderType<?> colliderType;
		Object type = data.get("type");
		if (type != null && type instanceof String) {
			colliderType = Registry.COLLIDER.get((String) type);
			if (colliderType == null)
				colliderType = ColliderType.BOX;
		} else
			colliderType = ColliderType.BOX;
		collider = colliderType.get();

		Object noResolve = data.get("noResolve");
		if (noResolve != null && noResolve instanceof Boolean)
			collider.noResolve = (Boolean) noResolve;

		collider.load(data);
		return collider;
	}
}
