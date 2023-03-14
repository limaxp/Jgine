package org.jgine.system.systems.ai.navigation;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

public class Navigation implements Cloneable {

	protected AiObject ai;
	protected Transform transform;
	protected PhysicObject physic;

	public Navigation() {
	}

	public void init(AiObject ai) {
		this.ai = ai;
		Entity entity = ai.getEntity();
		this.transform = entity.transform;
		this.physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
	}

	public void attack(Entity target) {
		Vector2f dirToTarget = Vector2f
				.normalize(Vector2f.sub(target.transform.getPosition(), transform.getPosition()));
		physic.accelerate(Vector2f.mult(dirToTarget, 20000.0f));
	}

	public void move(float x, float y) {
		Vector2f pos = transform.getPosition();
		Vector2f dirToTarget = Vector2f.normalize(Vector2f.sub(x, y, pos.x, pos.y));
		physic.accelerate(Vector2f.mult(dirToTarget, 1000.0f));
	}

	public void move(float x, float y, float z) {
		Vector3f pos = transform.getPosition();
		Vector3f dirToTarget = Vector3f.normalize(Vector3f.sub(x, y, z, pos.x, pos.y, pos.z));
		physic.accelerate(Vector3f.mult(dirToTarget, 1000.0f));
	}

	public Vector2f getPosition(float deltaX, float deltaY) {
		Vector2f pos = transform.getPosition();
		return getPosition(pos.x, pos.y, deltaX, deltaY);
	}

	public Vector3f getPosition(float deltaX, float deltaY, float deltaZ) {
		Vector3f pos = transform.getPosition();
		return getPosition(pos.x, pos.y, pos.z, deltaX, deltaY, deltaZ);
	}

	public Vector2f getPosition(float x, float y, float deltaX, float deltaY) {
		return new Vector2f(x + FastMath.random(-deltaX, deltaX), y + FastMath.random(-deltaY, deltaY));
	}

	public Vector3f getPosition(float x, float y, float z, float deltaX, float deltaY, float deltaZ) {
		return new Vector3f(x + FastMath.random(-deltaX, deltaX), y + FastMath.random(-deltaY, deltaY),
				z + FastMath.random(-deltaZ, deltaZ));
	}

	@Override
	public Navigation clone() {
		try {
			return (Navigation) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
