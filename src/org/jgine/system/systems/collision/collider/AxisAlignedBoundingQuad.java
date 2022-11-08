package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.system.systems.collision.CollisionChecks2D;

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

	public AxisAlignedBoundingQuad() {
	}

	public AxisAlignedBoundingQuad(float w, float h) {
		this.w = w;
		this.h = h;
	}

	@Override
	public void scale(Vector3f scale) {
		w *= scale.x;
		h *= scale.y;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return (point.x >= (pos.x - this.w / 2) && point.x <= (pos.x + this.w / 2))
				&& (point.y >= (pos.y - this.h) && point.y <= (pos.y + this.h));
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.checkAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(pos, this, otherPos,
					(AxisAlignedBoundingQuad) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.checkAxisAlignedBoundingQuadvsBoundingCircle(pos, this, otherPos,
					(BoundingCircle) other);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.checkLinevsAxisAlignedBoundingQuad(otherPos, (LineCollider) other, pos, this);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks2D.resolveAxisAlignedBoundingQuadvsAxisAlignedBoundingQuad(pos, this, otherPos,
					(AxisAlignedBoundingQuad) other);
		else if (other instanceof BoundingCircle)
			return CollisionChecks2D.resolveAxisAlignedBoundingQuadvsBoundingCircle(pos, this, otherPos,
					(BoundingCircle) other);
		else if (other instanceof LineCollider)
			return CollisionChecks2D.resolveLinevsAxisAlignedBoundingQuad(otherPos, (LineCollider) other, pos, this);
		return null;
	}

	@Override
	public AxisAlignedBoundingQuad clone() {
		return new AxisAlignedBoundingQuad(w, h);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object widthData = data.get("width");
		if (widthData != null)
			w = YamlHelper.toFloat(widthData);
		Object heightData = data.get("height");
		if (heightData != null)
			h = YamlHelper.toFloat(heightData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		w = in.readFloat();
		h = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(w);
		out.writeFloat(h);
	}

	@Override
	public ColliderType<AxisAlignedBoundingQuad> getType() {
		return ColliderTypes.QUAD;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer2D.renderLine2d(Transform.calculateMatrix(new Matrix(), pos, Vector3f.NULL, new Vector3f(w, h, 0)),
				new float[] { -1, -1, 1, -1, 1, 1, -1, 1 }, new Material(), true);
	}
}
