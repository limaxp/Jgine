package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
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

	public LineCollider(Transform transform, Vector2f normal) {
		super(transform);
		this.normal = normal;
	}

	@Override
	public boolean containsPoint(Vector3f point) {
		return distance(point) > 0 ? true : false;
	}

	private double distance(Vector2f point) {
		// Distance = (A*x0+B*y0+D)
		return normal.x * point.x + normal.y * point.y;
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof LineCollider)
			return CollisionChecks2D.PlanevsPlane(this, (LineCollider) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.PlanevsBoundingCircle(this, (BoundingCircle) other);
		else if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.PlanevsAxisAlignedBoundingBox(this, (AxisAlignedBoundingQuad) other);
		return false;
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
			this.normal = new Vector2f((float) normalMap.getOrDefault("x", 0), (float) normalMap.getOrDefault(
					"y", 0));
		}
	}

	@Override
	public ColliderType<LineCollider> getType() {
		return ColliderTypes.LINE;
	}

	@Override
	public void render() {
		// TODO render line here!
		Renderer2D.renderQuad(Transform.calculateMatrix(new Matrix(), transform.getPosition(), new Vector3f(normal), new Vector3f(
				Float.MAX_VALUE)), new Material());
	}
}
