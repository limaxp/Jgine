package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer2D;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.Collision;
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

	public BoundingCircle() {
	}

	public BoundingCircle(float r) {
		this.r = r;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return Vector3f.distance(pos, point) < r * r;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof BoundingCircle)
			return CollisionChecks2D.checkBoundingCirclevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.checkAxisAlignedBoundingQuadvsBoundingCircle(otherPos,
					(AxisAlignedBoundingQuad) other, pos, this);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.checkLinevsBoundingCircle(otherPos, (LineCollider) other, pos, this);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof BoundingCircle)
			return CollisionChecks2D.resolveBoundingCirclevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(otherPos,
					(AxisAlignedBoundingQuad) other, pos, this);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.resolveLinevsBoundingCircle(otherPos, (LineCollider) other, pos, this);
		return null;
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
	public void render(Vector3f pos) {
		Renderer2D.render(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(r)),
				ResourceManager.getModel("ball"));
	}
}
