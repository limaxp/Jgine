package org.jgine.system.systems.script;

import org.jgine.core.entity.Entity;
import org.jgine.system.systems.collision.Collision;

public interface IScript {

	public default void setEntity(Entity entity) {
	}

	public default Entity getEntity() {
		return null;
	}

	public abstract void onEnable();

	public abstract void onDisable();

	public abstract void update();

	public abstract void onCollision(Collision collision);
}
