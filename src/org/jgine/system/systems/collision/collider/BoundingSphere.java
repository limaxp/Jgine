package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.transform.Transform;

/**
 * Represents an BoundingSphere for 3D with float precision. An BoundingSphere
 * in 3D is represented by a center point and a radius(r).
 * 
 * @author Maximilian Paar
 */
public class BoundingSphere extends Collider {

	public float r;

	public BoundingSphere() {}

	public BoundingSphere(float r) {
		this.r = r;
	}

	public BoundingSphere(Transform transform, float r) {
		super(transform);
		this.r = r;
	}

	@Override
	public boolean containsPoint(Vector3f point) {
		return Vector3f.distance(transform.getPosition(), point) < r * r;
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof BoundingSphere)
			return CollisionChecks.BoundingSpherevsBoundingSphere(this, (BoundingSphere) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.AxisAlignedBoundingBoxvsBoundingSphere((AxisAlignedBoundingBox) other, this);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.PlanevsBoundingSphere((PlaneCollider) other, this);
		else if (other instanceof BoundingCylinder)
			return CollisionChecks.BoundingCylindervsBoundingSphere((BoundingCylinder) other, this);
		return false;
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
	public void render() {
		Renderer.render(Transform.calculateMatrix(new Matrix(), transform.getPosition(), Vector3f.NULL, new Vector3f(r)),
				ResourceManager.getModel("ball"));
	}
}
