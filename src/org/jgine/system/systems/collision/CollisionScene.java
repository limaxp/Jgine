package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.script.IScript;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.scheduler.TaskHelper;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	public static final int SUB_STEPS = 4;
	private static final PhysicObject EMPTY_DUMMY = new PhysicObject();

	static {
		UpdateManager.addTransformPosition((entity, x, y, z) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.move(x, y, z);
		});
		UpdateManager.addTransformScale((entity, x, y, z) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.scale(x, y, z);
		});
		UpdateManager.addPhysicPosition((entity, x, y, z) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.move(x, y, z);
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
	public void render() {
		if (!system.showHitBox())
			return;
		Renderer.setShader(Renderer.BASIC_SHADER);
		Renderer.enableDepthTest();
		for (int i = 0; i < size; i++)
			objects[i].render();
		Renderer.disableDepthTest();
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
//				resolveCollision(index, object, i, objects[i]);

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

			if (physic1 == null) {
				if (physic2 != null) {
					physic1 = EMPTY_DUMMY;
					resolve(physic1, physic2, collision);
					collider2.move(physic2.x, physic2.y, 0);
				}
			} else if (physic2 == null) {
				physic2 = EMPTY_DUMMY;
				resolve(physic1, physic2, collision);
				collider1.move(physic1.x, physic1.y, 0);
			} else {
				resolve(physic1, physic2, collision);
				collider1.move(physic1.x, physic1.y, 0);
				collider2.move(physic2.x, physic2.y, 0);
			}
		}
		callCollisionEvent(entity1, entity2, collider1, collider2, collision);
	}

	private static void resolve(PhysicObject physic1, PhysicObject physic2, CollisionData collision) {
		if (collision.strictResolve)
			resolveStrict(physic1, physic2, collision);
		else
			resolveDefault(physic1, physic2, collision);
	}

	private static void resolveDefault(PhysicObject physic1, PhysicObject physic2, CollisionData collision) {
		Vector2f axisNormal = Vector2f.normalize(collision.getAxisX(), collision.getAxisY());
		physic1.x += physic1.stiffness * collision.overlapX * axisNormal.x;
		physic2.x -= physic2.stiffness * collision.overlapX * axisNormal.x;
		physic1.y += physic1.stiffness * collision.overlapY * axisNormal.y;
		physic2.y -= physic2.stiffness * collision.overlapY * axisNormal.y;
	}

	private static void resolveStrict(PhysicObject physic1, PhysicObject physic2, CollisionData collision) {
		Vector2f axisNormal = Vector2f.normalize(collision.getAxisX(), collision.getAxisY());
		if (collision.overlapX < collision.overlapY) {
			physic1.x += physic1.stiffness * collision.overlapX * axisNormal.x;
			physic2.x -= physic2.stiffness * collision.overlapX * axisNormal.x;
		} else {
			physic1.y += physic1.stiffness * collision.overlapY * axisNormal.y;
			physic2.y -= physic2.stiffness * collision.overlapY * axisNormal.y;
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
