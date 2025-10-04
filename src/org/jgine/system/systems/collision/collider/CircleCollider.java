package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.render.mesh.BaseMesh;
import org.jgine.render.mesh.MeshGenerator;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.Collision;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;

/**
 * Represents a CircleCollider for 2D with float precision. A CircleCollider is
 * represented by center point(x, y) and radius(r).
 */
public class CircleCollider extends Collider {

	public static final BaseMesh COLLIDER_MESH = MeshGenerator.circleHollow(1.0f, 16);

	public float x;
	public float y;
	public float r;

	public CircleCollider() {
	}

	public CircleCollider(float r) {
		this.r = r;
	}

	public CircleCollider(float x, float y, float r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}

	@Override
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void move(float x, float y, float z) {
		this.x += x;
		this.y += y;
	}

	@Override
	public void scale(float x, float y, float z) {
		this.r *= (x + y) * 0.5f;
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.circlevsPoint(this.x, this.y, this.r, x, y);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof CircleCollider) {
			CircleCollider o = (CircleCollider) other;
			return CollisionChecks.circlevsCircle(x, y, r, o.x, o.y, o.r);
		}

		else if (other instanceof AxisAlignedBoundingQuad) {
			AxisAlignedBoundingQuad o = (AxisAlignedBoundingQuad) other;
			return CollisionChecks.circlevsQuad(x, y, r, o.x, o.y, o.w, o.h);
		}

		else if (other instanceof LineCollider) {
			LineCollider o = (LineCollider) other;
			return CollisionChecks.circlevsLine(x, y, r, o.x, o.y, o.xNorm, o.yNorm);
		}
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		if (other instanceof CircleCollider) {
			CircleCollider o = (CircleCollider) other;
			return CollisionChecks.resolveCirclevsCircle(x, y, r, o.x, o.y, o.r);
		}

		else if (other instanceof AxisAlignedBoundingQuad) {
			AxisAlignedBoundingQuad o = (AxisAlignedBoundingQuad) other;
			return CollisionChecks.resolveCirclevsQuad(x, y, r, o.x, o.y, o.w, o.h);
		}

		else if (other instanceof LineCollider) {
			LineCollider o = (LineCollider) other;
			return CollisionChecks.resolveCirclevsLine(x, y, r, o.x, o.y, o.xNorm, o.yNorm);
		}
		return null;
	}

	@Override
	public CircleCollider clone() {
		return new CircleCollider(x, y, r);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object xData = data.get("x");
		if (xData != null)
			x = YamlHelper.toFloat(xData);
		Object yData = data.get("y");
		if (yData != null)
			y = YamlHelper.toFloat(yData);
		Object radiusData = data.get("radius");
		if (radiusData != null)
			r = YamlHelper.toFloat(radiusData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		r = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(r);
	}

	@Override
	public ColliderType<CircleCollider> getType() {
		return ColliderTypes.CIRCLE;
	}

	@Override
	public void render() {
		Renderer2D.render(Transform.calculateMatrix2d(new Matrix(), x, y, r, r), COLLIDER_MESH, new Material());
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return 0.0f;
	}

	@Override
	public float getWidth() {
		return r;
	}

	@Override
	public float getHeight() {
		return r;
	}

	@Override
	public float getDepth() {
		return 0.0f;
	}
}
