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
import org.jgine.system.systems.collision.CollisionChecks2D;
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
			TaskManager.execute(size, this::applyConstraint);
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
				Collision collision = resolveCollision(object, objectCollider, target, targetCollider);
				if (collision != null) {
					EventManager.callEvent(entities[index], collision, IScript::onCollision);
					EventManager.callEvent(entities[i], collision, IScript::onCollision);
				}
			}
		}
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

	private static Collision resolveCollision(PhysicObject object1, Collider collider1, PhysicObject object2,
			Collider collider2) {
//		Collision collision = objectCollider.resolveCollision(new Vector3f(object.x, object.y, 0),
//				targetCollider, new Vector3f(target.x, target.y, 0));

		Collision collision = null;
		if (collider1 instanceof BoundingCircle && collider2 instanceof BoundingCircle)
			collision = resolveBoundingCirclevsBoundingCircle(object1, (BoundingCircle) collider1, object2,
					(BoundingCircle) collider2);

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof AxisAlignedBoundingQuad)
			collision = resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(object1,
					(AxisAlignedBoundingQuad) collider1, object2, (AxisAlignedBoundingQuad) collider2);

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof BoundingCircle)
			collision = resolveAxisAlignedBoundingQuadvsBoundingCircle(object1, (AxisAlignedBoundingQuad) collider1,
					object2, (BoundingCircle) collider2);

		else if (collider1 instanceof BoundingCircle && collider2 instanceof AxisAlignedBoundingQuad)
			collision = resolveAxisAlignedBoundingQuadvsBoundingCircle(object2, (AxisAlignedBoundingQuad) collider2,
					object1, (BoundingCircle) collider1);
		return collision;
	}

	@Nullable
	private static Collision resolveBoundingCirclevsBoundingCircle(PhysicObject object1, BoundingCircle collider1,
			PhysicObject object2, BoundingCircle collider2) {
		Collision collision = CollisionChecks2D.resolveBoundingCirclevsBoundingCircle(object1.x, object1.y, collider1,
				object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.collisionAxis);
		object1.x += 0.5f * collision.overlap.x * axisNormal.x;
		object1.y += 0.5f * collision.overlap.x * axisNormal.y;
		object2.x -= 0.5f * collision.overlap.y * axisNormal.x;
		object2.y -= 0.5f * collision.overlap.y * axisNormal.y;
		return collision;
	}

	@Nullable
	private static Collision resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(PhysicObject object1,
			AxisAlignedBoundingQuad collider1, PhysicObject object2, AxisAlignedBoundingQuad collider2) {
		Collision collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(object1.x,
				object1.y, collider1, object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.collisionAxis);
		if (collision.overlap.x < collision.overlap.y) {
			object1.x += 0.5f * collision.overlap.x * axisNormal.x;
			object2.x -= 0.5f * collision.overlap.x * axisNormal.x;
		} else {
			object1.y += 0.5f * collision.overlap.y * axisNormal.y;
			object2.y -= 0.5f * collision.overlap.y * axisNormal.y;
		}
		return collision;
	}

	@Nullable
	private static Collision resolveAxisAlignedBoundingQuadvsBoundingCircle(PhysicObject object1,
			AxisAlignedBoundingQuad collider1, PhysicObject object2, BoundingCircle collider2) {
		Collision collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(object1.x, object1.y,
				collider1, object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.collisionAxis);
		if (collision.overlap.x < collision.overlap.y) {
			object1.x += 0.5f * collision.overlap.x * axisNormal.x;
			object2.x -= 0.5f * collision.overlap.x * axisNormal.x;
		} else {
			object1.y += 0.5f * collision.overlap.y * axisNormal.y;
			object2.y -= 0.5f * collision.overlap.y * axisNormal.y;
		}
		return collision;
	}

}
