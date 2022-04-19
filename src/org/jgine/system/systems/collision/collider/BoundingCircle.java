package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer2D;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks2D;
import org.jgine.system.systems.transform.Transform;

/**
 * Represents an BoundingSphere for 3D with float precision. An BoundingSphere
 * in 3D is represented by a center point and a radius(r).
 * 
 * @author Maximilian Paar
 */
public class BoundingCircle extends Collider {

	public float r;

	public BoundingCircle() {}

	public BoundingCircle(float r) {
		this.r = r;
	}

	public BoundingCircle(Transform transform, float r) {
		super(transform);
		this.r = r;
	}

	@Override
	public boolean containsPoint(Vector3f point) {
		return Vector3f.distance(transform.getPosition(), point) < r * r;
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof BoundingCircle)
			return CollisionChecks2D.BoundingCirclevsBoundingCircle(this, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.AxisAlignedBoundingBoxvsBoundingCircle((AxisAlignedBoundingQuad) other, this);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.PlanevsBoundingCircle((LineCollider) other, this);
		return false;
	}

	@Override
	public BoundingCircle clone() {
		return new BoundingCircle(r);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object radius = data.get("radius");
		if (radius != null && radius instanceof Number)
			this.r = ((Number) radius).floatValue();
	}

	@Override
	public ColliderType<BoundingCircle> getType() {
		return ColliderTypes.CIRCLE;
	}

	@Override
	public void render() {
		Renderer2D.render(Transform.calculateMatrix(new Matrix(), transform.getPosition(), Vector3f.NULL, new Vector3f(r)),
				ResourceManager.getModel("ball"));
	}
}
