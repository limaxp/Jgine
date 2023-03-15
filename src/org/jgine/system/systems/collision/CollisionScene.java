package org.jgine.system.systems.collision;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.render.Renderer;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.system.systems.collision.collider.AxisAlignedBoundingQuad;
import org.jgine.system.systems.collision.collider.BoundingCircle;
import org.jgine.system.systems.collision.collider.LineCollider;
import org.jgine.system.systems.collision.collider.PolygonCollider;
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
				Collider target = objects[i];
				Entity targetEntity = entities[i];
				PhysicObject targetPhysic = targetEntity.getSystem(Engine.PHYSIC_SYSTEM);
				CollisionData collision = resolveCollision(physic, object, targetPhysic, target);
				if (collision != null)
					callCollisionEvent(entity, targetEntity, object, target, collision);
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
