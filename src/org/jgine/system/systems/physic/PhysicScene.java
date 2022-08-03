package org.jgine.system.systems.physic;

import java.util.function.BiConsumer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.entity.Transform;
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
import org.jgine.system.systems.collision.collider.LineCollider;
import org.jgine.system.systems.collision.collider.PolygonCollider;
import org.jgine.system.systems.script.IScript;

public class PhysicScene extends EntityListSystemScene<PhysicSystem, PhysicObject> {

	private final BiConsumer<Entity, Object> positionUpdate = (entity, pos) -> {
		PhysicObject physic = entity.getSystem(this);
		if (physic != null)
			physic.setPosition((Vector3f) pos);
	};

	private float gravity;
	private float airResistanceFactor;

	public PhysicScene(PhysicSystem system, Scene scene) {
		super(system, scene, PhysicObject.class);
		this.gravity = system.getGravity();
		this.airResistanceFactor = system.getAirResistanceFactor();
		UpdateManager.register(scene, "transformPosition", positionUpdate);
	}

	@Override
	public void free() {
		UpdateManager.unregister(scene, "transformPosition", positionUpdate);
	}

	@Override
	public void initObject(Entity entity, PhysicObject object) {
		Transform transform = entity.transform;
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
			if (object.updatePosition(subDt, gravity, airResistanceFactor)) {
				Entity entity = entities[index];
				entity.transform.setPositionIntern(object.x, object.y, object.z);
				UpdateManager.update(entity, "physicPosition", new Vector3f(object.x, object.y, object.z));
			}
		}
	}

	private void applyConstraint(int index, int size) {
		Vector3f pos = new Vector3f(0, 0, 0);
		float radius = 600.0f;
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
			Collider[] objectColliders = entities[index].getSystems(collisionSystem);
			for (int objectIndex = 0; objectIndex < objectColliders.length; objectIndex++) {
				Collider objectCollider = objectColliders[objectIndex];
				for (int i = index + 1; i < this.size; ++i) {
					PhysicObject target = objects[i];
					Collider[] targetColliders = entities[i].getSystems(collisionSystem);
					for (int targetIndex = 0; targetIndex < targetColliders.length; targetIndex++) {
						Collider targetCollider = targetColliders[targetIndex];
						Collision collision = resolveCollision(object, objectCollider, target, targetCollider);
						if (collision != null) {
							EventManager.callEvent(entities[index], collision, IScript::onCollision);
							EventManager.callEvent(entities[i], collision, IScript::onCollision);
						}
					}
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

	@Nullable
	public static Collision resolveCollision(PhysicObject object1, Collider collider1, PhysicObject object2,
			Collider collider2) {
		if (collider1.noResolve || collider2.noResolve)
			return collider1.resolveCollision(new Vector3f(object1.x, object1.y, object1.z), collider2,
					new Vector3f(object2.x, object2.y, object2.z));

		if (collider1 instanceof BoundingCircle && collider2 instanceof BoundingCircle)
			return resolveBoundingCirclevsBoundingCircle(object1, (BoundingCircle) collider1, object2,
					(BoundingCircle) collider2);

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof AxisAlignedBoundingQuad)
			return resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(object1, (AxisAlignedBoundingQuad) collider1,
					object2, (AxisAlignedBoundingQuad) collider2);

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof BoundingCircle)
			return resolveAxisAlignedBoundingQuadvsBoundingCircle(object1, (AxisAlignedBoundingQuad) collider1, object2,
					(BoundingCircle) collider2);

		else if (collider1 instanceof BoundingCircle && collider2 instanceof AxisAlignedBoundingQuad)
			return resolveAxisAlignedBoundingQuadvsBoundingCircle(object2, (AxisAlignedBoundingQuad) collider2, object1,
					(BoundingCircle) collider1);

		else if (collider1 instanceof LineCollider && collider2 instanceof BoundingCircle)
			return resolveLinevsBoundingCircle(object1, (LineCollider) collider1, object2, (BoundingCircle) collider2);

		else if (collider1 instanceof BoundingCircle && collider2 instanceof LineCollider)
			return resolveLinevsBoundingCircle(object2, (LineCollider) collider2, object1, (BoundingCircle) collider1);

		else if (collider1 instanceof LineCollider && collider2 instanceof AxisAlignedBoundingQuad)
			return resolveLinevsAxisAlignedBoundingQuad(object1, (LineCollider) collider1, object2,
					(AxisAlignedBoundingQuad) collider2);

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof LineCollider)
			return resolveLinevsAxisAlignedBoundingQuad(object2, (LineCollider) collider2, object1,
					(AxisAlignedBoundingQuad) collider1);

		else if (collider1 instanceof LineCollider && collider2 instanceof LineCollider)
			return resolveLinevsLine(object2, (LineCollider) collider2, object1, (LineCollider) collider1);

		else if (collider1 instanceof PolygonCollider && collider2 instanceof PolygonCollider)
			return resolvePolygonvsPolygon(object1, (PolygonCollider) collider1, object2, (PolygonCollider) collider2);

		return null;
	}

	@Nullable
	public static Collision resolveBoundingCirclevsBoundingCircle(PhysicObject object1, BoundingCircle collider1,
			PhysicObject object2, BoundingCircle collider2) {
		Collision collision = CollisionChecks2D.resolveBoundingCirclevsBoundingCircle(object1.x, object1.y, collider1,
				object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapX * axisNormal.y;
		object2.x -= object2.stiffness * collision.overlapY * axisNormal.x;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		return collision;
	}

	@Nullable
	public static Collision resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(PhysicObject object1,
			AxisAlignedBoundingQuad collider1, PhysicObject object2, AxisAlignedBoundingQuad collider2) {
		Collision collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(object1.x,
				object1.y, collider1, object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		if (collision.overlapX < collision.overlapY) {
			object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
			object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		} else {
			object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
			object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		}
		return collision;
	}

	@Nullable
	public static Collision resolveAxisAlignedBoundingQuadvsBoundingCircle(PhysicObject object1,
			AxisAlignedBoundingQuad collider1, PhysicObject object2, BoundingCircle collider2) {
		Collision collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(object1.x, object1.y,
				collider1, object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		if (collision.overlapX < collision.overlapY) {
			object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
			object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		} else {
			object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
			object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		}
		return collision;
	}

	@Nullable
	public static Collision resolveLinevsBoundingCircle(PhysicObject object1, LineCollider collider1,
			PhysicObject object2, BoundingCircle collider2) {
		Collision collision = CollisionChecks2D.resolveLinevsBoundingCircle(object1.x, object1.y, collider1, object2.x,
				object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapX * axisNormal.y;
		object2.x -= object2.stiffness * collision.overlapY * axisNormal.x;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		return collision;
	}

	@Nullable
	public static Collision resolveLinevsAxisAlignedBoundingQuad(PhysicObject object1, LineCollider collider1,
			PhysicObject object2, AxisAlignedBoundingQuad collider2) {
		Collision collision = CollisionChecks2D.resolveLinevsAxisAlignedBoundingQuad(object1.x, object1.y, collider1,
				object2.x, object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		return collision;
	}

	@Nullable
	public static Collision resolveLinevsLine(PhysicObject object1, LineCollider collider1, PhysicObject object2,
			LineCollider collider2) {
		Collision collision = CollisionChecks2D.resolveLinevsLine(object1.x, object1.y, collider1, object2.x, object2.y,
				collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		return collision;
	}

	@Nullable
	public static Collision resolvePolygonvsPolygon(PhysicObject object1, PolygonCollider collider1,
			PhysicObject object2, PolygonCollider collider2) {
		Collision collision = CollisionChecks2D.resolvePolygonvsPolygon(object1.x, object1.y, collider1, object2.x,
				object2.y, collider2);
		if (collision == null)
			return null;

		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		return collision;
	}
}
