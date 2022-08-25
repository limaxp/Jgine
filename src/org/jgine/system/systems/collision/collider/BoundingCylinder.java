package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.system.systems.collision.CollisionChecks;

public class BoundingCylinder extends Collider {

	public float r;
	public float h;

	public BoundingCylinder() {
	}

	public BoundingCylinder(float r, float h) {
		this.r = r;
		this.h = h;
	}

	@Override
	public void scale(Vector3f scale) {
		if (scale.x == scale.z)
			r *= scale.x;
		else
			r *= (int) (scale.x + scale.z) / 2;
		h *= scale.y;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		// TODO make this use height!
		return Vector3f.distance(pos, point) < r * r;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof BoundingCylinder)
			return CollisionChecks.checkBoundingCylindervsBoundingCylinder(pos, this, otherPos, (BoundingSphere) other);
		else if (other instanceof BoundingSphere)
			return CollisionChecks.checkBoundingCylindervsBoundingSphere(pos, this, otherPos, (BoundingSphere) other);
		else if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.checkBoundingCylindervsAxisAlignedBoundingBox(pos, this, otherPos,
					(AxisAlignedBoundingBox) other);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.checkBoundingCylindervsPlane(pos, this, otherPos, (PlaneCollider) other);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return null;
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
	public void render(Vector3f pos) {
		Renderer.enableWireframeMode();
		Renderer.render(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(r, h, r)),
				ResourceManager.getModel("ball"));
		Renderer.disableWireframeMode();
	}
}
