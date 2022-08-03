package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.Collision;
import org.jgine.system.systems.collision.CollisionChecks;

/**
 * Represents an AxisAlignedBoundingBox for 3D with float precision. An
 * AxisAlignedBoundingBox in 3D is represented by a center point, a width(w), a
 * height(h) and a depth(d).
 * 
 * @author Maximilian Paar
 */
public class AxisAlignedBoundingBox extends Collider {

	public float w;
	public float h;
	public float d;

	public AxisAlignedBoundingBox() {
	}

	public AxisAlignedBoundingBox(float w, float h, float d) {
		this.w = w;
		this.h = h;
		this.d = d;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return (point.x >= (pos.x - this.w / 2) && point.x <= (pos.x + this.w / 2)) && (point.y >= (pos.y - this.h)
				&& point.y <= (pos.y + this.h) && (point.z >= (pos.z - this.d) && point.z <= (pos.z + this.d)));
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.checkAxisAlignedBoundingBoxvsAxisAlignedBoundingBox(pos, this, otherPos,
					(AxisAlignedBoundingBox) other);
		else if (other instanceof BoundingSphere)
			return CollisionChecks.checkAxisAlignedBoundingBoxvsBoundingSphere(pos, this, otherPos,
					(BoundingSphere) other);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.checkPlanevsAxisAlignedBoundingBox(otherPos, (PlaneCollider) other, pos, this);
		else if (other instanceof BoundingCylinder)
			return CollisionChecks.checkBoundingCylindervsAxisAlignedBoundingBox(otherPos, (BoundingCylinder) other,
					pos, this);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		return null;
	}

	@Override
	public AxisAlignedBoundingBox clone() {
		return new AxisAlignedBoundingBox(w, h, d);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object width = data.get("width");
		if (width != null && width instanceof Number)
			this.w = ((Number) width).floatValue();
		Object height = data.get("height");
		if (height != null && height instanceof Number)
			this.h = ((Number) height).floatValue();
		Object depth = data.get("depth");
		if (depth != null && depth instanceof Number)
			this.d = ((Number) depth).floatValue();
	}

	@Override
	public ColliderType<AxisAlignedBoundingBox> getType() {
		return ColliderTypes.BOX;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.enableWireframeMode();
		Renderer.renderCube(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(w, h, d)),
				new Material());
		Renderer.disableWireframeMode();
	}
}
