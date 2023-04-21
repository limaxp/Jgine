package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer;
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
		if (other instanceof LineCollider) {
			LineCollider o = (LineCollider) other;
			return CollisionChecks.linevsLine(xNorm, yNorm, o.xNorm, o.yNorm);
		}

		else if (other instanceof CircleCollider) {
			CircleCollider o = (CircleCollider) other;
			return CollisionChecks.linevsCircle(x, y, xNorm, yNorm, o.x, o.y, o.r);
		}

		else if (other instanceof AxisAlignedBoundingQuad) {
			AxisAlignedBoundingQuad o = (AxisAlignedBoundingQuad) other;
			return CollisionChecks.linevsQuad(x, y, xNorm, yNorm, o.x, o.y, o.w, o.h);
		}
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Collider other) {
		if (other instanceof LineCollider) {
			LineCollider o = (LineCollider) other;
			return CollisionChecks.resolveLinevsLine(x, y, xNorm, yNorm, o.x, o.y, o.xNorm, o.yNorm);
		}

		else if (other instanceof CircleCollider) {
			CircleCollider o = (CircleCollider) other;
			return CollisionChecks.resolveLinevsCircle(x, y, xNorm, yNorm, o.x, o.y, o.r);
		}

		else if (other instanceof AxisAlignedBoundingQuad) {
			AxisAlignedBoundingQuad o = (AxisAlignedBoundingQuad) other;
			return CollisionChecks.resolveLinevsQuad(x, y, xNorm, yNorm, o.x, o.y, o.w, o.h);
		}
		return null;
	}

	@Override
	public LineCollider clone() {
		return new LineCollider(x, y, xNorm, yNorm);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object xData = data.get("x");
		if (xData != null)
			x = YamlHelper.toFloat(xData);
		Object yData = data.get("y");
		if (yData != null)
			y = YamlHelper.toFloat(yData);
		Object normalData = data.get("normal");
		if (normalData != null) {
			Vector2f normal = YamlHelper.toVector2f(normalData);
			xNorm = normal.x;
			yNorm = normal.y;
		}
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
		return ColliderTypes.LINE;
	}

	@Override
	public void render() {
		Renderer2D.renderLine(Transform.calculateMatrix2d(new Matrix(), x, y, Float.MAX_VALUE, Float.MAX_VALUE),
				Renderer.BASIC_SHADER, new Material(), yNorm, -xNorm, -yNorm, xNorm);
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
