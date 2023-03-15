package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.physic.PhysicObject;
import org.jgine.system.systems.script.IScript;
import org.jgine.system.systems.script.ScriptSystem;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.TaskHelper;

public class CollisionScene extends EntityListSystemScene<CollisionSystem, Collider> {

	public static final int SUB_STEPS = 4;

	static {
		UpdateManager.addTransformScale((entity, scale) -> {
			Collider collider = entity.getSystem(Engine.COLLISION_SYSTEM);
			if (collider != null)
				collider.scale(scale);
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
		object.scale(entity.transform.getScale());
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
			objects[i].render(entities[i].transform.getPosition());
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
			Entity entity = entities[index];
			PhysicObject physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
			for (int i = index + 1; i < this.size; ++i) {
				Entity targetEntity = entities[i];
				resolveCollision(entity, physic, object, targetEntity, targetEntity.getSystem(Engine.PHYSIC_SYSTEM),
						objects[i]);
			}
		}
	}

	public static void resolveCollision(Entity entity1, PhysicObject physic1, Collider collider1, Entity entity2,
			PhysicObject physic2, Collider collider2) {
		CollisionData collision = collider1.resolveCollision(new Vector3f(physic1.x, physic1.y, physic1.z), collider2,
				new Vector3f(physic2.x, physic2.y, physic2.z));
		if (collider1.noResolve || collider2.noResolve || collision == null)
			return;
		if (collider1 != collision.collider1) {
			PhysicObject tmp = physic1;
			physic1 = physic2;
			physic2 = tmp;
		}
		if (collision.strictResolve)
			resolveStrict(physic1, physic2, collision);
		else
			resolveDefault(physic1, physic2, collision);
		callCollisionEvent(entity1, entity2, collider1, collider2, collision);
	}

	public static void resolveDefault(PhysicObject object1, PhysicObject object2, CollisionData collision) {
		if (collision == null)
			return;
		Vector2f axisNormal = Vector2f.normalize(collision.axisX, collision.axisY);
		object1.x += object1.stiffness * collision.overlapX * axisNormal.x;
		object2.x -= object2.stiffness * collision.overlapX * axisNormal.x;
		object1.y += object1.stiffness * collision.overlapY * axisNormal.y;
		object2.y -= object2.stiffness * collision.overlapY * axisNormal.y;
	}

	public static void resolveStrict(PhysicObject object1, PhysicObject object2, CollisionData collision) {
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

	private static void callCollisionEvent(Entity object, Entity target, Collider objectCollider,
			Collider targetCollider, CollisionData collision) {
		ScriptSystem.callEvent(object, new Collision(collision, target, objectCollider, targetCollider),
				IScript::onCollision);
		ScriptSystem.callEvent(target, new Collision(collision, object, targetCollider, objectCollider),
				IScript::onCollision);
	}
}
