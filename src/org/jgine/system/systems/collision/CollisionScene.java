package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.script.IScript;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.scheduler.TaskHelper;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	public static final int SUB_STEPS = 4;

	static {
		UpdateManager.addTransformPosition((entity, dx, dy, dz) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.move(dx, dy, dz);
		});
		UpdateManager.addTransformScale((entity, x, y, z) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.scale(x, y, z);
		});
		UpdateManager.addPhysicPosition((entity, dx, dy, dz) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.move(dx, dy, dz);
		});
	}

	public CollisionScene(CollisionSystem system, Scene scene) {
		super(system, scene, Collider.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Collider object) {
		Transform transform = entity.transform;
		object.move(transform.getX(), transform.getY(), transform.getZ());
		object.scale(transform.getScaleX(), transform.getScaleY(), transform.getScaleZ());
	}

	@Override
	public void update(float dt) {
		for (int i = 0; i < SUB_STEPS; i++)
			TaskHelper.execute(size, this::solveCollisions);
	}

	@Override
	public void render(float dt) {
		if (!system.showHitBox())
			return;
		for (int i = 0; i < size; i++)
			objects[i].render();
	}

	@Override
	public Collider load(DataInput in) throws IOException {
		Collider object = ColliderTypes.get(in.readInt()).get();
		object.load(in);
		return object;
	}

	@Override
	public void save(Collider object, DataOutput out) throws IOException {
		out.writeInt(object.getType().getId());
		object.save(out);
	}

	private void solveCollisions(int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			Collider object = objects[index];
//			for (int i = index + 1; i < this.size; ++i)
//				resolveCollision(entities[index], object, entities[i], objects[i]);

			Entity entity = entities[index];
			scene.getSpacePartitioning().forNear(object.getX() - object.getWidth(), object.getY() - object.getHeight(),
					object.getZ() - object.getDepth(), object.getX() + object.getWidth(),
					object.getY() + object.getHeight(), object.getZ() + object.getDepth(), (targetEntity) -> {
						if (targetEntity == entity)
							return;
						Collider targetObject = targetEntity.getSystem(Engine.COLLISION_SYSTEM);
						if (targetObject != null)
							resolveCollision(entity, object, targetEntity, targetObject);
					});
		}
	}

	private void resolveCollision(Entity entity1, Collider collider1, Entity entity2, Collider collider2) {
		CollisionData collision = collider1.resolveCollision(collider2);
		if (collision == null)
			return;
		if (!collider1.noResolve && !collider2.noResolve) {
			PhysicObject physic1 = entity1.getSystem(Engine.PHYSIC_SYSTEM);
			PhysicObject physic2 = entity2.getSystem(Engine.PHYSIC_SYSTEM);
			if (collision.strictResolve)
				resolveStrict(physic1, collider1, physic2, collider2, collision);
			else
				resolveDefault(physic1, collider1, physic2, collider2, collision);
		}
		callCollisionEvent(entity1, entity2, collider1, collider2, collision);
	}

	private static void resolveDefault(PhysicObject physic1, Collider collider1, PhysicObject physic2,
			Collider collider2, CollisionData collision) {
		Vector2f axisNormal = Vector2f.normalize(collision.getAxisX(), collision.getAxisY());
		if (physic1 != null) {
			float dx = physic1.stiffness * collision.overlapX * axisNormal.x;
			float dy = physic1.stiffness * collision.overlapY * axisNormal.y;
			physic1.x += dx;
			physic1.y += dy;
			collider1.move(dx, dy, 0.0f);
		}
		if (physic2 != null) {
			float dx = -physic2.stiffness * collision.overlapX * axisNormal.x;
			float dy = -physic2.stiffness * collision.overlapY * axisNormal.y;
			physic2.x += dx;
			physic2.y += dy;
			collider2.move(dx, dy, 0.0f);
		}
	}

	private static void resolveStrict(PhysicObject physic1, Collider collider1, PhysicObject physic2,
			Collider collider2, CollisionData collision) {
		Vector2f axisNormal = Vector2f.normalize(collision.getAxisX(), collision.getAxisY());
		if (collision.overlapX < collision.overlapY) {
			if (physic1 != null) {
				float dx = physic1.stiffness * collision.overlapX * axisNormal.x;
				physic1.x += dx;
				collider1.move(dx, 0.0f, 0.0f);
			}
			if (physic2 != null) {
				float dx = -physic2.stiffness * collision.overlapX * axisNormal.x;
				physic2.x += dx;
				collider2.move(dx, 0.0f, 0.0f);
			}
		} else {
			if (physic1 != null) {
				float dy = physic1.stiffness * collision.overlapY * axisNormal.y;
				physic1.y += dy;
				collider1.move(0.0f, dy, 0.0f);
			}
			if (physic2 != null) {
				float dy = -physic2.stiffness * collision.overlapY * axisNormal.y;
				physic2.y += dy;
				collider2.move(0.0f, dy, 0.0f);
			}
		}
	}

	private static void callCollisionEvent(Entity object, Entity target, Collider objectCollider,
			Collider targetCollider, CollisionData collision) {
		ScriptSystem.callEvent(object, new Collision(collision, target, objectCollider, targetCollider),
				IScript::onCollision);
		ScriptSystem.callEvent(target, new Collision(collision, object, targetCollider, objectCollider),
				IScript::onCollision);
	}
}
