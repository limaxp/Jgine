package jgine.system.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.render.Renderer2D;
import jgine.render.material.Material;
import jgine.system.collision.Collider;
import jgine.system.collision.ColliderType;
import jgine.system.collision.Collision;
import jgine.system.collision.CollisionChecks;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.math.Matrix;
import jgine.utils.math.vector.Vector2f;

/**
 * Represents a LineCollider for 2D with float precision. A LineCollider is
 * represented by center point(x, y) and normal vector(xNorm, yNorm).
 */
public class LineCollider extends Collider {

	public float x;
	public float y;
	public float xNorm;
	public float yNorm;

	public LineCollider() {
	}

	public LineCollider(float xNorm, float yNorm) {
		this.xNorm = xNorm;
		this.yNorm = yNorm;
	}

	public LineCollider(float x, float y, float xNorm, float yNorm) {
		this.x = x;
		this.y = y;
		this.xNorm = xNorm;
		this.yNorm = yNorm;
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
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.linevsPoint(this.x, this.y, this.xNorm, this.yNorm, x, y);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof LineCollider o)
			return CollisionChecks.linevsLine(xNorm, yNorm, o.xNorm, o.yNorm);
		else if (other instanceof CircleCollider o)
			return CollisionChecks.linevsCircle(x, y, xNorm, yNorm, o.x, o.y, o.r);
		else if (other instanceof AxisAlignedBoundingQuad o)
			return CollisionChecks.linevsQuad(x, y, xNorm, yNorm, o.x, o.y, o.w, o.h);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		if (other instanceof LineCollider o)
			return CollisionChecks.resolveLinevsLine(x, y, xNorm, yNorm, o.x, o.y, o.xNorm, o.yNorm);
		else if (other instanceof CircleCollider o)
			return CollisionChecks.resolveLinevsCircle(x, y, xNorm, yNorm, o.x, o.y, o.r);
		else if (other instanceof AxisAlignedBoundingQuad o)
			return CollisionChecks.resolveLinevsQuad(x, y, xNorm, yNorm, o.x, o.y, o.w, o.h);
		return null;
	}

	@Override
	public LineCollider clone() {
		return new LineCollider(x, y, xNorm, yNorm);
	}

	@Override
	public void load(Map<String, Object> data) {
		x = ObjectUtils.toFloat(data.get("x"), x);
		y = ObjectUtils.toFloat(data.get("y"), y);
		Vector2f normal = ObjectUtils.toVector2f(data.get("normal"), () -> new Vector2f(xNorm, yNorm));
		xNorm = normal.x;
		yNorm = normal.y;
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("x", x);
		data.put("y", y);
		data.put("normal", Arrays.asList(xNorm, yNorm));
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		xNorm = in.readFloat();
		yNorm = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(xNorm);
		out.writeFloat(yNorm);
	}

	@Override
	public ColliderType<LineCollider> getType() {
		return ColliderType.LINE;
	}

	@Override
	public void render() {
		Renderer2D.renderLine(Transform.calculateMatrix2d(new Matrix(), x, y, Integer.MAX_VALUE, Integer.MAX_VALUE),
				new Material(), yNorm, -xNorm, -yNorm, xNorm);
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
		return Float.MAX_VALUE;
	}

	@Override
	public float getHeight() {
		return Float.MAX_VALUE;
	}

	@Override
	public float getDepth() {
		return 0.0f;
	}
}
