package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Represents an AxisAlignedBoundingQuad for 2D with float precision. An
 * AxisAlignedBoundingQuad is represented by center point, width(w), height(h)
 * and depth(d).
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
		return CollisionChecks.quadvsPoint(pos.x, pos.y, w, h, point.x, point.y);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks.quadvsQuad(pos.x, pos.y, w, h, otherPos.x, otherPos.y,
					((AxisAlignedBoundingQuad) other).w, ((AxisAlignedBoundingQuad) other).h);
		else if (other instanceof CircleCollider)
			return CollisionChecks.quadvsCircle(pos.x, pos.y, w, h, otherPos.x, otherPos.y, ((CircleCollider) other).r);
		else if (other instanceof LineCollider)
			return CollisionChecks.quadvsLine(pos.x, pos.y, w, h, otherPos.x, otherPos.y, ((LineCollider) other).xNorm,
					((LineCollider) other).yNorm);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingQuad)
			return CollisionChecks.resolveQuadvsQuad(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(AxisAlignedBoundingQuad) other);
		else if (other instanceof CircleCollider)
			return CollisionChecks.resolveQuadvsCircle(pos.x, pos.y, this, otherPos.x, otherPos.y,
					(CircleCollider) other);
		else if (other instanceof LineCollider)
			return CollisionChecks.resolveQuadvsLine(pos.x, pos.y, this, otherPos.x, otherPos.y, (LineCollider) other);
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
		Renderer2D.renderLine2d(Transform.calculateMatrix2d(new Matrix(), pos, new Vector2f(w, h)), new Material(),
				true, new float[] { -1, -1, 1, -1, 1, 1, -1, 1 });
	}
}
