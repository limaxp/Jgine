package org.jgine.system.systems.physic;

import java.util.function.BiConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.TaskManager;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.script.EventManager;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.Collision;
import org.jgine.system.systems.collision.CollisionScene;
import org.jgine.system.systems.collision.CollisionSystem;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.script.IScript;
import org.jgine.system.systems.transform.Transform;
import org.jgine.system.systems.transform.TransformSystem;

public class PhysicScene extends EntityListSystemScene<PhysicSystem, PhysicObject> {

	private final BiConsumer<Entity, Object> positionUpdate = (entity, pos) -> entity.getSystem(this)
			.setPosition((Vector3f) pos);

	private float gravity;
	private float airResistanceFactor;

	public PhysicScene(PhysicSystem system, Scene scene) {
		super(system, scene, PhysicObject.class);
		this.gravity = system.getGravity();
		this.airResistanceFactor = system.getAirResistanceFactor();
		UpdateManager.register(scene, "physicPosition", positionUpdate);
	}

	@Override
	public void free() {
		UpdateManager.unregister(scene, "physicPosition", positionUpdate);
	}

	@Override
	public void initObject(Entity entity, PhysicObject object) {
		Transform transform = entity.getSystem(scene.getSystem(TransformSystem.class));
		Vector3f pos = transform.getPosition();
		object.setPosition(pos);
	}

	private long lastTime = System.currentTimeMillis();
	private float subDt;

	@Override
	public void update() {
		long time = System.currentTimeMillis();
		float dt = (time - lastTime) / 1000.0f;
		lastTime = time;
		int subSteps = 4;
		subDt = dt / subSteps;

		for (int i = 0; i < subSteps; i++) {
//			TaskManager.execute(size, this::applyConstraint);
			TaskManager.execute(size, this::solveCollisions);
			TaskManager.execute(size, this::updatePositions);
		}
	}

	private void updatePositions(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			PhysicObject object = objects[index];
			if (object.updatePosition(subDt, gravity, airResistanceFactor))
				UpdateManager.update(scene, "position", entities[index], new Vector3f(object.x, object.y, object.z));
		}
	}

	private void applyConstraint(int index, int size) {
		Vector3f pos = new Vector3f(0, 0, 0);
		float radius = 400.0f;
		size = index + size;
		for (; index < size; index++) {
			PhysicObject object = objects[index];
			Vector3f toObj = Vector3f.sub(object.getPosition(), pos);
			float dist = Vector3f.length(toObj);
			if (dist > radius) {
				Vector3f n = Vector3f.div(toObj, dist);
				object.x = pos.x + n.x * radius;
				object.y = pos.y + n.y * radius;
				object.z = pos.z + n.z * radius;
			}
		}
	}

	private void solveCollisions(int index, int size) {
		CollisionScene collisionSystem = scene.getSystem(CollisionSystem.class);
		size = index + size;
		for (; index < size; index++) {
			PhysicObject object = objects[index];
			Collider objectCollider = entities[index].getSystem(collisionSystem);
			for (int i = index + 1; i < this.size; ++i) {
				PhysicObject target = objects[i];
				Collider targetCollider = entities[i].getSystem(collisionSystem);

				Collision collision = null;
				if (objectCollider instanceof BoundingCircle && targetCollider instanceof BoundingCircle)
					collision = BoundingCirclevsBoundingCircle(object, (BoundingCircle) objectCollider, target,
							(BoundingCircle) targetCollider);

				else if (objectCollider instanceof AxisAlignedBoundingQuad
						&& targetCollider instanceof AxisAlignedBoundingQuad)
					collision = AxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(object,
							(AxisAlignedBoundingQuad) objectCollider, target, (AxisAlignedBoundingQuad) targetCollider);

				else if (objectCollider instanceof BoundingCircle && targetCollider instanceof AxisAlignedBoundingQuad)
					collision = BoundingCirclevsAxisAlignedBoundingQuad(object, (BoundingCircle) objectCollider, target,
							(AxisAlignedBoundingQuad) targetCollider);

				else if (objectCollider instanceof AxisAlignedBoundingQuad && targetCollider instanceof BoundingCircle)
					collision = BoundingCirclevsAxisAlignedBoundingQuad(object, (BoundingCircle) targetCollider, target,
							(AxisAlignedBoundingQuad) objectCollider);

				if (collision != null) {
					EventManager.callEvent(entities[index], collision, IScript::onCollision);
					EventManager.callEvent(entities[i], collision, IScript::onCollision);
				}
			}
		}
	}

	@Nullable
	public static Collision BoundingCirclevsBoundingCircle(PhysicObject object1, BoundingCircle collider1,
			PhysicObject object2, BoundingCircle collider2) {
		Vector2f collisionAxis = Vector2f.sub(object1.getPosition(), object2.getPosition());
		float dist = Vector2f.length(collisionAxis);
		float minDist = collider1.r + collider2.r;
		if (dist < minDist) {
			Vector2f axisNormal = Vector2f.div(collisionAxis, dist);
			float delta = minDist - dist;
			object1.x += 0.5f * delta * axisNormal.x;
			object1.y += 0.5f * delta * axisNormal.y;

			object2.x -= 0.5f * delta * axisNormal.x;
			object2.y -= 0.5f * delta * axisNormal.y;
			return new Collision(new Vector3f(collisionAxis), new Vector3f(object1.x, object1.y, 0), collider1,
					collider2);
		}
		return null;
	}

	@Nullable
	public static Collision AxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(PhysicObject object1,
			AxisAlignedBoundingQuad collider1, PhysicObject object2, AxisAlignedBoundingQuad collider2) {
		Vector2f collisionAxis = Vector2f.sub(object1.getPosition(), object2.getPosition());
		Vector2f axisNormal = Vector2f.normalize(collisionAxis);
		float distX = Math.abs(collisionAxis.x);
		float minDistX = collider1.w + collider2.w;
		float distY = Math.abs(collisionAxis.y);
		float minDistY = collider1.h + collider2.h;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			if (deltaX < deltaY) {
				object1.x += 0.5f * deltaX * axisNormal.x;
				object2.x -= 0.5f * deltaX * axisNormal.x;
			} else {
				object1.y += 0.5f * deltaY * axisNormal.y;
				object2.y -= 0.5f * deltaY * axisNormal.y;
			}
			return new Collision(new Vector3f(collisionAxis), new Vector3f(object1.x, object1.y, 0), collider1,
					collider2);
		}
		return null;
	}

	@Nullable
	public static Collision BoundingCirclevsAxisAlignedBoundingQuad(PhysicObject object1, BoundingCircle collider1,
			PhysicObject object2, AxisAlignedBoundingQuad collider2) {
		Vector2f collisionAxis = Vector2f.sub(object1.getPosition(), object2.getPosition());
		Vector2f axisNormal = Vector2f.normalize(collisionAxis);
		float distX = Math.abs(collisionAxis.x);
		float minDistX = collider1.r + collider2.w;
		float distY = Math.abs(collisionAxis.y);
		float minDistY = collider1.r + collider2.h;

		if (distX < minDistX && distY < minDistY) {
			float deltaX = minDistX - distX;
			float deltaY = minDistY - distY;
			if (deltaX < deltaY) {
				object1.x += 0.5f * deltaX * axisNormal.x;
				object2.x -= 0.5f * deltaX * axisNormal.x;
			} else {
				object1.y += 0.5f * deltaY * axisNormal.y;
				object2.y -= 0.5f * deltaY * axisNormal.y;
			}
			return new Collision(new Vector3f(collisionAxis), new Vector3f(object1.x, object1.y, 0), collider1,
					collider2);
		}
		return null;
	}

	@Override
	public void render() {
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
