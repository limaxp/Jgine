package org.jgine.system.systems.collision;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.core.manager.ServiceManager;
import org.jgine.system.EngineSystem;
import org.jgine.utils.function.Property;

public class CollisionSystem extends EngineSystem {

	private boolean showHitBox = false;

	public CollisionSystem() {
		super("collider");
		ServiceManager.register("showHitBox", new Property<Boolean>() {

			@Override
			public void setValue(Boolean obj) {
				showHitBox = obj;
			}

			@Override
			public Boolean getValue() {
				return showHitBox;
			}
		});
	}

	@Override
	public CollisionScene createScene(Scene scene) {
		return new CollisionScene(this, scene);
	}

	public void setShowHitBox(boolean showHitBox) {
		this.showHitBox = showHitBox;
	}

	public boolean showHitBox() {
		return showHitBox;
	}

	@Override
	public Collider load(Map<String, Object> data) {
		Collider collider;
		ColliderType<?> colliderType;
		Object type = data.get("type");
		if (type != null && type instanceof String) {
			colliderType = ColliderTypes.get((String) type);
			if (colliderType == null)
				colliderType = ColliderTypes.BOX;
		} else
			colliderType = ColliderTypes.BOX;
		collider = colliderType.get();

		Object noResolve = data.get("noResolve");
		if (noResolve != null && noResolve instanceof Boolean)
			collider.noResolve = (Boolean) noResolve;

		collider.load(data);
		return collider;
	}
}
