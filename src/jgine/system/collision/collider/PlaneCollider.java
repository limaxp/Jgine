package jgine.system.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.render.Renderer;
import jgine.render.material.Material;
import jgine.system.collision.Collider;
import jgine.system.collision.ColliderType;
import jgine.system.collision.Collision;
import jgine.system.collision.CollisionChecks;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.math.Matrix;
import jgine.utils.math.vector.Vector3f;

/**
 * Represents a PlaneCollider for 3D with float precision. A PlaneCollider is
 * represented by center point and normal vector(xNorm, yNorm, zNorm).
 */
public class PlaneCollider extends Collider {

	public float x;
	public float y;
	public float z;
	public float xNorm;
	public float yNorm;
	public float zNorm;

	public PlaneCollider() {
	}

	public PlaneCollider(float xNorm, float yNorm, float zNorm) {
		this.xNorm = xNorm;
		this.yNorm = yNorm;
		this.zNorm = zNorm;
	}

	public PlaneCollider(float x, float y, float z, float xNorm, float yNorm, float zNorm) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xNorm = xNorm;
		this.yNorm = yNorm;
		this.zNorm = zNorm;
	}

	@Override
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void move(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	@Override
	public void scale(float x, float y, float z) {
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.planevsPoint(this.x, this.y, this.z, xNorm, yNorm, zNorm, x, y, z);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof PlaneCollider o)
			return CollisionChecks.planevsPlane(xNorm, yNorm, zNorm, o.xNorm, o.yNorm, o.zNorm);
		else if (other instanceof SphereCollider o)
			return CollisionChecks.planevsSphere(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.r);
		else if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.planevsCube(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.w, o.h, o.d);
		else if (other instanceof CylinderCollider o)
			return CollisionChecks.planevsCylinder(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.r, o.h);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		if (other instanceof PlaneCollider o)
			return CollisionChecks.resolvePlanevsPlane(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.xNorm, o.yNorm,
					o.zNorm);
		else if (other instanceof SphereCollider o)
			return CollisionChecks.resolvePlanevsSphere(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.r);
		else if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.resolvePlanevsCube(x, y, z, xNorm, yNorm, zNorm, o.x, o.y, o.z, o.w, o.h, o.d);
		return null;
	}

	@Override
	public PlaneCollider clone() {
		return new PlaneCollider(x, y, z, xNorm, yNorm, zNorm);
	}

	@Override
	public void load(Map<String, Object> data) {
		x = ObjectUtils.toFloat(data.get("x"), x);
		y = ObjectUtils.toFloat(data.get("y"), y);
		z = ObjectUtils.toFloat(data.get("z"), z);
		Vector3f normal = ObjectUtils.toVector3f(data.get("normal"), () -> new Vector3f(xNorm, yNorm, zNorm));
		xNorm = normal.x;
		yNorm = normal.y;
		zNorm = normal.z;
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("x", x);
		data.put("y", y);
		data.put("z", z);
		data.put("normal", Arrays.asList(xNorm, yNorm, zNorm));
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		xNorm = in.readFloat();
		yNorm = in.readFloat();
		zNorm = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(xNorm);
		out.writeFloat(yNorm);
		out.writeFloat(zNorm);
	}

	@Override
	public ColliderType<PlaneCollider> getType() {
		return ColliderType.PLANE;
	}

	@Override
	public void render() {
		Renderer.renderQuad(Transform.calculateMatrix(new Matrix(), x, y, z, xNorm, yNorm, zNorm, Float.MAX_VALUE,
				Float.MAX_VALUE, Float.MAX_VALUE), new Material());
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
		return z;
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
		return Float.MAX_VALUE;
	}
}
