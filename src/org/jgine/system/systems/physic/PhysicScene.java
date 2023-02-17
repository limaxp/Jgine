package org.jgine.system.systems.physic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.scheduler.TaskHelper;
import org.jgine.misc.utils.script.EventManager;
import org.jgine.system.SystemObject;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.Collision;
import org.jgine.system.systems.collision.CollisionChecks2D;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.system.systems.collision.CollisionScene;
import org.jgine.system.systems.collision.CollisionSystem;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.collision.collider.LineCollider;
import org.jgine.system.systems.collision.collider.PolygonCollider;
import org.jgine.system.systems.script.IScript;

public class PhysicScene extends EntityListSystemScene<PhysicSystem, PhysicObject> {

	static {
		UpdateManager.addTransformPosition((entity, pos) -> {
			PhysicObject physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
			if (physic != null)
				physic.setPosition(pos);
		});
	}

	private float gravity;
	private float airResistanceFactor;

	public PhysicScene(PhysicSystem system, Scene scene) {
		super(system, scene, PhysicObject.class);
		this.gravity = system.getGravity();
		this.airResistanceFactor = system.getAirResistanceFactor();
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, PhysicObject object) {
		Transform transform = entity.transform;
		Vector3f pos = transform.getPosition();
		object.initPosition(pos);
	}

	private float subDt;

	@Override
	public void update(float dt) {
		int subSteps = 4;
		subDt = dt / subSteps;

		for (int i = 0; i < subSteps; i++) {
//			TaskHelper.execute(size, this::applyConstraint);
			TaskHelper.execute(size, this::solveCollisions);
			TaskHelper.execute(size, this::updatePositions);
		}
	}

	private void updatePositions(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			PhysicObject object = objects[index];
			if (object.updatePosition(subDt, gravity, airResistanceFactor)) {
				Entity entity = entities[index];
				UpdateManager.getPhysicPosition().accept(entity,
						entity.transform.setPositionIntern(object.x, object.y, object.z));
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
		if (collisionSystem == null)
			return;

		size = index + size;
		for (; index < size; index++) {
			PhysicObject object = objects[index];
			SystemObject[] objectColliders = entities[index].getSystems(collisionSystem);
			for (int objectIndex = 0; objectIndex < objectColliders.length; objectIndex++) {
				Collider objectCollider = (Collider) objectColliders[objectIndex];
				for (int i = index + 1; i < this.size; ++i) {
					PhysicObject target = objects[i];
					SystemObject[] targetColliders = entities[i].getSystems(collisionSystem);
					for (int targetIndex = 0; targetIndex < targetColliders.length; targetIndex++) {
						Collider targetCollider = (Collider) targetColliders[targetIndex];
						CollisionData collision = resolveCollision(object, objectCollider, target, targetCollider);
						if (collision != null)
							callCollisionEvent(entities[index], entities[i], objectCollider, targetCollider, collision);
					}
				}
			}

		}
	}

	private static void callCollisionEvent(Entity object, Entity target, Collider objectCollider,
			Collider targetCollider, CollisionData collision) {
		EventManager.callEvent(object, new Collision(collision, target, objectCollider, targetCollider),
				IScript::onCollision);
		EventManager.callEvent(target, new Collision(collision, object, targetCollider, objectCollider),
				IScript::onCollision);
	}

	@Override
	public void render() {
	}

	@Override
	public PhysicObject load(DataInput in) throws IOException {
		PhysicObject object = new PhysicObject();
		object.load(in);
		return object;
	}

	@Override
	public void save(PhysicObject object, DataOutput out) throws IOException {
		object.save(out);
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
	public static CollisionData resolveCollision(PhysicObject object1, Collider collider1, PhysicObject object2,
			Collider collider2) {
		if (collider1.noResolve || collider2.noResolve)
			return collider1.resolveCollision(new Vector3f(object1.x, object1.y, object1.z), collider2,
					new Vector3f(object2.x, object2.y, object2.z));

		CollisionData collision = null;
		if (collider1 instanceof BoundingCircle && collider2 instanceof BoundingCircle) {
			collision = CollisionChecks2D.resolveBoundingCirclevsBoundingCircle(object1.x, object1.y,
					(BoundingCircle) collider1, object2.x, object2.y, (BoundingCircle) collider2);
			resolveCircle(object1, object2, collision);
		}

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof AxisAlignedBoundingQuad) {
			collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(object1.x, object1.y,
					(AxisAlignedBoundingQuad) collider1, object2.x, object2.y, (AxisAlignedBoundingQuad) collider2);
			resolveDefault(object1, object2, collision);
		}

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof BoundingCircle) {
			collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(object1.x, object1.y,
					(AxisAlignedBoundingQuad) collider1, object2.x, object2.y, (BoundingCircle) collider2);
			resolveDefault(object1, object2, collision);
		}

		else if (collider1 instanceof BoundingCircle && collider2 instanceof AxisAlignedBoundingQuad) {
			collision = CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(object2.x, object2.y,
					(AxisAlignedBoundingQuad) collider2, object1.x, object1.y, (BoundingCircle) collider1);
			resolveDefault(object2, object1, collision);
		}

		else if (collider1 instanceof LineCollider && collider2 instanceof BoundingCircle) {
			collision = CollisionChecks2D.resolveLinevsBoundingCircle(object1.x, object1.y, (LineCollider) collider1,
					object2.x, object2.y, (BoundingCircle) collider2);
			resolveDefault(object1, object2, collision);
		}

		else if (collider1 instanceof BoundingCircle && collider2 instanceof LineCollider) {
			collision = CollisionChecks2D.resolveLinevsBoundingCircle(object2.x, object2.y, (LineCollider) collider2,
					object1.x, object1.y, (BoundingCircle) collider1);
			resolveDefault(object2, object1, collision);
		}

		else if (collider1 instanceof LineCollider && collider2 instanceof AxisAlignedBoundingQuad) {
			collision = CollisionChecks2D.resolveLinevsAxisAlignedBoundingQuad(object1.x, object1.y,
					(LineCollider) collider1, object2.x, object2.y, (AxisAlignedBoundingQuad) collider2);
			resolveDefault(object1, object2, collision);
		}

		else if (collider1 instanceof AxisAlignedBoundingQuad && collider2 instanceof LineCollider) {
			collision = CollisionChecks2D.resolveLinevsAxisAlignedBoundingQuad(object2.x, object2.y,
					(LineCollider) collider2, object1.x, object1.y, (AxisAlignedBoundingQuad) collider1);
			resolveDefault(object2, object1, collision);
		}

		else if (collider1 instanceof LineCollider && collider2 instanceof LineCollider) {
			collision = CollisionChecks2D.resolveLinevsLine(object1.x, object1.y, (LineCollider) collider1, object2.x,
					object2.y, (LineCollider) collider2);
			resolveDefault(object1, object2, collision);
		}

		else if (collider1 instanceof PolygonCollider && collider2 instanceof PolygonCollider) {
			collision = CollisionChecks2D.resolvePolygonvsPolygon(object1.x, object1.y, (PolygonCollider) collider1,
					object2.x, object2.y, (PolygonCollider) collider2);
			resolveDefault(object1, object2, collision);
		}
		return collision;
	}

	public static void resolveDefault(PhysicObject object1, PhysicObject object2, CollisionData collision) {
		if (collision == null)
			return;
		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		if (collision.overlapX < collision.overlapY) {
			object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
			object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		} else {
			object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
			object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
		}
	}

	public static void resolveCircle(PhysicObject object1, PhysicObject object2, CollisionData collision) {
		if (collision == null)
			return;
		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
	}
}
