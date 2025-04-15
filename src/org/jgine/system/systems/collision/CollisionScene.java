package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Engine.UpdateTask;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.Renderer;
import org.jgine.system.UpdateManager;
import org.jgine.system.data.ObjectSystemScene.EntitySystemScene;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.script.IScript;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.scheduler.Job;

public class CollisionScene extends EntitySystemScene<CollisionSystem, Collider> {

	public static final int SUB_STEPS = 4;

	static {
		UpdateManager.addTransformPosition((entity, dx, dy, dz) -> {
			entity.forSystems(Engine.COLLISION_SYSTEM, (Collider collider) -> collider.move(dx, dy, dz));
		});
		UpdateManager.addTransformScale((entity, x, y, z) -> {
			entity.forSystems(Engine.COLLISION_SYSTEM, (Collider collider) -> collider.scale(x, y, z));
		});
		UpdateManager.addPhysicPosition((entity, dx, dy, dz) -> {
			entity.forSystems(Engine.COLLISION_SYSTEM, (Collider collider) -> collider.move(dx, dy, dz));
		});
	}

	public CollisionScene(CollisionSystem system, Scene scene) {
		super(system, scene, Collider.class, 100000);
	}

	@Override
	public void free() {
	}

	@Override
	public void onInit(Entity entity, Collider object) {
		Transform transform = entity.transform;
		object.move(transform.getX(), transform.getY(), transform.getZ());
		object.scale(transform.getScaleX(), transform.getScaleY(), transform.getScaleZ());
	}

	@Override
	public void update(UpdateTask update) {
		updateStep(update, 0);
	}

	private void updateStep(UpdateTask update, int subStep) {
		Job.region(size(), this::solveCollision, () -> {
			if (subStep < SUB_STEPS)
				updateStep(update, subStep + 1);
			else
				update.finish(system);
		});
	}

	@Override
	public void render(float dt) {
		if (!system.showHitBox())
			return;

		Renderer.enableDepthTest();
		Renderer.setShader(Renderer.BASIC_SHADER);
		for (int i = 0; i < size(); i++)
			get(i).render();
		Renderer.disableDepthTest();
	}

	@Override
	protected void saveData(Collider object, DataOutput out) throws IOException {
		out.writeInt(object.getType().getId());
		object.save(out);
	}

	@Override
	protected Collider loadData(DataInput in) throws IOException {
		Collider object = ColliderTypes.get(in.readInt()).get();
		object.load(in);
		return object;
	}

	private void solveCollision(int index) {
		Collider object = get(index);
//		for (int i = index + 1; i < this.size; ++i)
//			resolveCollision(entities[index], object, entities[i], objects[i]);

		Entity entity = getEntity(index);
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
			float dx = physic1.getStiffness() * collision.overlapX * axisNormal.x;
			float dy = physic1.getStiffness() * collision.overlapY * axisNormal.y;
			physic1.x += dx;
			physic1.y += dy;
			collider1.move(dx, dy, 0.0f);
		}
		if (physic2 != null) {
			float dx = -physic2.getStiffness() * collision.overlapX * axisNormal.x;
			float dy = -physic2.getStiffness() * collision.overlapY * axisNormal.y;
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
				float dx = physic1.getStiffness() * collision.overlapX * axisNormal.x;
				physic1.x += dx;
				collider1.move(dx, 0.0f, 0.0f);
			}
			if (physic2 != null) {
				float dx = -physic2.getStiffness() * collision.overlapX * axisNormal.x;
				physic2.x += dx;
				collider2.move(dx, 0.0f, 0.0f);
			}
		} else {
			if (physic1 != null) {
				float dy = physic1.getStiffness() * collision.overlapY * axisNormal.y;
				physic1.y += dy;
				collider1.move(0.0f, dy, 0.0f);
			}
			if (physic2 != null) {
				float dy = -physic2.getStiffness() * collision.overlapY * axisNormal.y;
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
