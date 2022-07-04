package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.Collision;
import org.jgine.system.systems.collision.CollisionChecks2D;
import org.jgine.system.systems.transform.Transform;

public class LineCollider extends Collider {

	public Vector2f normal;

	public LineCollider() {
		this.normal = Vector2f.NULL;
	}

	public LineCollider(Vector2f normal) {
		this.normal = normal;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return distance(point) > 0 ? true : false;
	}

	private double distance(Vector2f point) {
		// Distance = (A*x0+B*y0+D)
		return normal.x * point.x + normal.y * point.y;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof LineCollider)
			return CollisionChecks2D.checkPlanevsPlane(pos, this, otherPos, (LineCollider) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.checkPlanevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.checkPlanevsAxisAlignedBoundingQuad(pos, this, otherPos,
					(AxisAlignedBoundingQuad) other);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof LineCollider)
			return CollisionChecks2D.resolvePlanevsPlane(pos, this, otherPos, (LineCollider) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.resolvePlanevsBoundingCircle(pos, this, otherPos, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.resolvePlanevsAxisAlignedBoundingQuad(pos, this, otherPos,
					(AxisAlignedBoundingQuad) other);
		return null;
	}

	@Override
	public LineCollider clone() {
		return new LineCollider(normal);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object normal = data.get("normal");
		if (normal != null && normal instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> normalMap = (Map<String, Object>) normal;
			this.normal = new Vector2f((float) normalMap.getOrDefault("x", 0), (float) normalMap.getOrDefault("y", 0));
		}
	}

	@Override
	public ColliderType<LineCollider> getType() {
		return ColliderTypes.LINE;
	}

	@Override
	public void render(Vector3f pos) {
		// TODO render line here!
		Renderer2D.renderQuad(
				Transform.calculateMatrix(new Matrix(), pos, new Vector3f(normal), new Vector3f(Float.MAX_VALUE)),
				new Material());
	}
}
