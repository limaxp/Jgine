package org.jgine.system.systems.collision.collider;

import java.util.Map;

import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks2D;
import org.jgine.system.systems.transform.Transform;

/**
 * Represents an AxisAlignedBoundingBox for 3D with float precision. An
 * AxisAlignedBoundingBox in 3D is represented by a center point, a width(w), a
 * height(h) and a depth(d).
 * 
 * @author Maximilian Paar
 */
public class AxisAlignedBoundingQuad extends Collider {

	public float w;
	public float h;

	public AxisAlignedBoundingQuad() {}

	public AxisAlignedBoundingQuad(float w, float h) {
		this.w = w;
		this.h = h;
	}

	public AxisAlignedBoundingQuad(Transform transform, float w, float h) {
		super(transform);
		this.w = w;
		this.h = h;
	}

	@Override
	public boolean containsPoint(Vector3f point) {
		Vector3f pos = transform.getPosition();
		return (point.x >= (pos.x - this.w / 2) && point.x <= (pos.x + this.w / 2))
				&& (point.y >= (pos.y - this.h) && point.y <= (pos.y + this.h));
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.AxisAlignedBoundingBoxvsAxisAlignedBoundingBox(this,
					(AxisAlignedBoundingQuad) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.AxisAlignedBoundingBoxvsBoundingCircle(this, (BoundingCircle) other);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.PlanevsAxisAlignedBoundingBox((LineCollider) other, this);
		return false;
	}

	@Override
	public AxisAlignedBoundingQuad clone() {
		return new AxisAlignedBoundingQuad(w, h);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object width = data.get("width");
		if (width != null && width instanceof Number)
			this.w = ((Number) width).floatValue();
		Object height = data.get("height");
		if (height != null && height instanceof Number)
			this.h = ((Number) height).floatValue();
	}

	@Override
	public ColliderType<AxisAlignedBoundingQuad> getType() {
		return ColliderTypes.QUAD;
	}

	@Override
	public void render() {
		Renderer2D.renderQuad(Transform.calculateMatrix(new Matrix(), transform.getPosition(), Vector3f.NULL, new Vector3f(w, h, 0)),
				new Material());
	}
}
