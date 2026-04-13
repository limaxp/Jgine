package jgine.system.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.render.Renderer2D;
import jgine.render.material.Material;
import jgine.render.mesh.BaseMesh;
import jgine.render.mesh.MeshGenerator;
import jgine.system.collision.Collider;
import jgine.system.collision.ColliderType;
import jgine.system.collision.Collision;
import jgine.system.collision.CollisionChecks;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.math.Matrix;

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
		if (other instanceof CircleCollider o)
			return CollisionChecks.circlevsCircle(x, y, r, o.x, o.y, o.r);
		else if (other instanceof AxisAlignedBoundingQuad o)
			return CollisionChecks.circlevsQuad(x, y, r, o.x, o.y, o.w, o.h);
		else if (other instanceof LineCollider o)
			return CollisionChecks.circlevsLine(x, y, r, o.x, o.y, o.xNorm, o.yNorm);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		if (other instanceof CircleCollider o)
			return CollisionChecks.resolveCirclevsCircle(x, y, r, o.x, o.y, o.r);
		else if (other instanceof AxisAlignedBoundingQuad o)
			return CollisionChecks.resolveCirclevsQuad(x, y, r, o.x, o.y, o.w, o.h);
		else if (other instanceof LineCollider o)
			return CollisionChecks.resolveCirclevsLine(x, y, r, o.x, o.y, o.xNorm, o.yNorm);
		return null;
	}

	@Override
	public CircleCollider clone() {
		return new CircleCollider(x, y, r);
	}

	@Override
	public void load(Map<String, Object> data) {
		x = ObjectUtils.toFloat(data.get("x"), x);
		y = ObjectUtils.toFloat(data.get("y"), y);
		r = ObjectUtils.toFloat(data.get("radius"), r);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("x", x);
		data.put("y", y);
		data.put("radius", r);
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
		return ColliderType.CIRCLE;
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
