package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.transform.Transform;

public class PlaneCollider extends Collider {

	public Vector3f normal;

	public PlaneCollider() {
		this.normal = Vector3f.NULL;
	}

	public PlaneCollider(Vector3f normal) {
		this.normal = normal;
	}

	public PlaneCollider(Transform transform, Vector3f normal) {
		super(transform);
		this.normal = normal;
	}

	@Override
	public boolean containsPoint(Vector3f point) {
		return distance(point) > 0 ? true : false;
	}

	private double distance(Vector3f point) {
		// Distance = (A*x0+B*y0+C*z0+D)
		return normal.x * point.x + normal.y * point.y + normal.z * point.z;
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof PlaneCollider)
			return CollisionChecks.PlanevsPlane(this, (PlaneCollider) other);
		else if (other instanceof BoundingSphere)
			return CollisionChecks.PlanevsBoundingSphere(this, (BoundingSphere) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.PlanevsAxisAlignedBoundingBox(this, (AxisAlignedBoundingBox) other);
		else if (other instanceof BoundingCylinder)
			return CollisionChecks.BoundingCylindervsPlane((BoundingCylinder) other, this);
		return false;
	}

	@Override
	public PlaneCollider clone() {
		return new PlaneCollider(normal);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object normal = data.get("normal");
		if (normal != null && normal instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> normalMap = (Map<String, Object>) normal;
			this.normal = new Vector3f((float) normalMap.getOrDefault("x", 0), (float) normalMap.getOrDefault(
					"y", 0), (float) normalMap.getOrDefault("z", 0));
		}
	}

	@Override
	public ColliderType<PlaneCollider> getType() {
		return ColliderTypes.PLANE;
	}

	@Override
	public void render() {
		Renderer.renderQuad(Transform.calculateMatrix(new Matrix(), transform.getPosition(), normal, new Vector3f(Float.MAX_VALUE)),
				new Material());
	}
}
