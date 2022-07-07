package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.Collision;
import org.jgine.system.systems.collision.CollisionChecks;

/**
 * Represents an BoundingSphere for 3D with float precision. An BoundingSphere
 * in 3D is represented by a center point and a radius(r).
 * 
 * @author Maximilian Paar
 */
public class BoundingSphere extends Collider {

	public float r;

	public BoundingSphere() {
	}

	public BoundingSphere(float r) {
		this.r = r;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return Vector3f.distance(pos, point) < r * r;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof BoundingSphere)
			return CollisionChecks.checkBoundingSpherevsBoundingSphere(pos, this, otherPos, (BoundingSphere) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.checkAxisAlignedBoundingBoxvsBoundingSphere(otherPos, (AxisAlignedBoundingBox) other,
					pos, this);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.checkPlanevsBoundingSphere(otherPos, (PlaneCollider) other, pos, this);
		else if (other instanceof BoundingCylinder)
			return CollisionChecks.checkBoundingCylindervsBoundingSphere(otherPos, (BoundingCylinder) other, pos, this);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return null;
	}

	@Override
	public BoundingSphere clone() {
		return new BoundingSphere(r);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object radius = data.get("radius");
		if (radius != null && radius instanceof Number)
			this.r = ((Number) radius).floatValue();
	}

	@Override
	public ColliderType<BoundingSphere> getType() {
		return ColliderTypes.SPHERE;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.render(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(r)),
				ResourceManager.getModel("ball"));
	}
}
