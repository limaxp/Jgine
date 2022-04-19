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

public class BoundingCylinder extends Collider {

	public float r;
	public float h;

	public BoundingCylinder() {}

	public BoundingCylinder(float r, float h) {
		this.r = r;
		this.h = h;
	}

	public BoundingCylinder(Transform transform, float r, float h) {
		super(transform);
		this.r = r;
		this.h = h;
	}

	@Override
	public boolean containsPoint(Vector3f point) {
		// TODO make this use height!
		return Vector3f.distance(transform.getPosition(), point) < r * r;
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof BoundingCylinder)
			return CollisionChecks.BoundingCylindervsBoundingCylinder(this, (BoundingSphere) other);
		else if (other instanceof BoundingSphere)
			return CollisionChecks.BoundingCylindervsBoundingSphere(this, (BoundingSphere) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.BoundingCylindervsAxisAlignedBoundingBox(this, (AxisAlignedBoundingBox) other);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.BoundingCylindervsPlane(this, (PlaneCollider) other);
		return false;
	}

	@Override
	public BoundingCylinder clone() {
		return new BoundingCylinder(r, h);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object radius = data.get("radius");
		if (radius != null && radius instanceof Number)
			this.r = ((Number) radius).floatValue();

		Object height = data.get("height");
		if (height != null && height instanceof Number)
			this.h = ((Number) height).floatValue();
	}

	@Override
	public ColliderType<BoundingCylinder> getType() {
		return ColliderTypes.CYLINDER;
	}

	@Override
	public void render() {
		Renderer.render(Transform.calculateMatrix(new Matrix(), transform.getPosition(), Vector3f.NULL, new Vector3f(r, h, r)),
				ResourceManager.getModel("ball"));
	}
}
