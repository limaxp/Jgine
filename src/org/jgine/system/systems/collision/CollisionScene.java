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
import org.jgine.utils.math.FastMath;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.Job;

public class CollisionScene extends EntitySystemScene<CollisionSystem, Collider> {

	public static final int SUB_STEPS = 4;

	static {
		UpdateManager.addTransformPosition((entity, x, y, z) -> {
			entity.forSystems(Engine.COLLISION_SYSTEM, (Collider collider) -> collider.set(x, y, z));
		});
		UpdateManager.addTransformScale((entity, x, y, z) -> {
			entity.forSystems(Engine.COLLISION_SYSTEM, (Collider collider) -> collider.scale(x, y, z));
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
		object.set(transform.getX(), transform.getY(), transform.getZ());
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
		Collision collision = collider1.resolveCollision(collider2);
		if (collision == null)
			return;
		if (!collider1.noResolve && !collider2.noResolve) {
			if (collision.strictResolve)
				resolveStrict(entity1, collider1, entity2, collider2, collision);
			else
				resolveDefault(entity1, collider1, entity2, collider2, collision);
		}
		callCollisionEvent(entity1, entity2, collider1, collider2, collision);
	}

	private static void callCollisionEvent(Entity object, Entity target, Collider objectCollider,
			Collider targetCollider, Collision collision) {
		ScriptSystem.callEvent(object, collision, target, objectCollider, targetCollider, IScript::onCollision);
		ScriptSystem.callEvent(target, collision, object, targetCollider, objectCollider, IScript::onCollision);
	}

	private static void resolveDefault(Entity entity1, Collider collider1, Entity entity2, Collider collider2,
			Collision collision) {
		PhysicObject physic1 = entity1.getSystem(Engine.PHYSIC_SYSTEM);
		PhysicObject physic2 = entity2.getSystem(Engine.PHYSIC_SYSTEM);
		Vector3f axisNormal = Vector3f.normalize(collision.getAxisX(), collision.getAxisY(), collision.getAxisZ());
		float dx = collision.overlapX * axisNormal.x + 0.00001f;
		float dy = collision.overlapY * axisNormal.y + 0.00001f;
		float dz = collision.overlapZ * axisNormal.z + 0.00001f;

		float dx1 = physic1.getStiffness() * dx;
		float dy1 = physic1.getStiffness() * dy;
		float dz1 = physic1.getStiffness() * dz;
		physic1.velX += dx1;
		physic1.velY += dy1;
		physic1.velZ += dz1;
		collider1.move(dx1, dy1, dz1);

		float dx2 = -physic2.getStiffness() * dx;
		float dy2 = -physic2.getStiffness() * dy;
		float dz2 = -physic2.getStiffness() * dz;
		physic2.velX += dx2;
		physic2.velY += dy2;
		physic2.velZ += dz2;
		collider2.move(dx2, dy2, dz2);
	}

	public static float calculateVelocity(float v1, float m1, float v2, float m2) {
		return (v1 * (m1 - m2) + (2 * m2 * v2)) / (m1 + m2);
	}

	private static void resolveStrict(Entity entity1, Collider collider1, Entity entity2, Collider collider2,
			Collision collision) {
		PhysicObject physic1 = entity1.getSystem(Engine.PHYSIC_SYSTEM);
		PhysicObject physic2 = entity2.getSystem(Engine.PHYSIC_SYSTEM);
		Vector3f axisNormal = Vector3f.normalize(collision.getAxisX(), collision.getAxisY(), collision.getAxisZ());

		float ox = collision.overlapX == 0 ? Float.MAX_VALUE : collision.overlapX;
		float oy = collision.overlapY == 0 ? Float.MAX_VALUE : collision.overlapY;
		float oz = collision.overlapZ == 0 ? Float.MAX_VALUE : collision.overlapZ;
		float smallest = FastMath.min(ox, FastMath.min(oy, oz));
		if (collision.overlapX == smallest) {
			float dx = collision.overlapX * axisNormal.x + 0.00001f;
			float dx1 = physic1.getStiffness() * dx;
			physic1.velX += dx1;
			collider1.move(dx1, 0.0f, 0.0f);

			float dx2 = -physic2.getStiffness() * dx;
			physic2.velX += dx2;
			collider2.move(dx2, 0.0f, 0.0f);

		} else if (collision.overlapY == smallest) {
			float dy = collision.overlapY * axisNormal.y + 0.00001f;
			float dy1 = physic1.getStiffness() * dy;
			physic1.velY += dy1;
			collider1.move(0.0f, dy1, 0.0f);

			float dy2 = -physic2.getStiffness() * dy;
			physic2.velY += dy2;
			collider2.move(0.0f, dy2, 0.0f);

		} else {
			float dz = collision.overlapZ * axisNormal.z + 0.00001f;
			float dz1 = physic1.getStiffness() * dz;
			physic1.velZ += dz1;
			collider1.move(0.0f, 0.0f, dz1);

			float dz2 = -physic2.getStiffness() * dz;
			physic2.velZ += dz2;
			collider2.move(0.0f, 0.0f, dz2);
		}
	}
}
