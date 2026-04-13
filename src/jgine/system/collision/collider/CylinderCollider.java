package jgine.system.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.render.Renderer;
import jgine.system.collision.Collider;
import jgine.system.collision.ColliderType;
import jgine.system.collision.Collision;
import jgine.system.collision.CollisionChecks;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.loader.ResourceManager;
import jgine.utils.math.Matrix;

/**
 * Represents an BoundingCylinder for 3D with float precision. An
 * BoundingCylinder is represented by center point, radius(r) and height(h).
 */
public class CylinderCollider extends Collider {

	public float x;
	public float y;
	public float z;
	public float r;
	public float h;

	public CylinderCollider() {
	}

	public CylinderCollider(float r, float h) {
		this.r = r;
		this.h = h;
	}

	public CylinderCollider(float x, float y, float z, float r, float h) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.h = h;
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
		this.r *= (x + z) * 0.5f;
		this.h *= y;
	}

	@Override
	public boolean containsPoint(float x, float y, float z) {
		return CollisionChecks.cylindervsPoint(this.x, this.y, this.z, r, h, x, y, z);
	}

	@Override
	public boolean checkCollision(Collider other) {
		if (other instanceof CylinderCollider o)
			return CollisionChecks.cylindervsCylinder(x, y, z, r, h, o.x, o.y, o.z, o.r, o.h);
		else if (other instanceof SphereCollider o)
			return CollisionChecks.cylindervsSphere(x, y, z, r, h, o.x, o.y, o.z, o.r);
		else if (other instanceof AxisAlignedBoundingBox o)
			return CollisionChecks.cylindervsCube(x, y, z, r, h, o.x, o.y, o.z, o.w, o.h, o.d);
		else if (other instanceof PlaneCollider o)
			return CollisionChecks.cylindervsPlane(x, y, z, r, h, o.x, o.y, o.z, o.xNorm, o.yNorm, o.zNorm);
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Collider other) {
		return null;
	}

	@Override
	public CylinderCollider clone() {
		return new CylinderCollider(x, y, z, r, h);
	}

	@Override
	public void load(Map<String, Object> data) {
		x = ObjectUtils.toFloat(data.get("x"), x);
		y = ObjectUtils.toFloat(data.get("y"), y);
		z = ObjectUtils.toFloat(data.get("z"), z);
		r = ObjectUtils.toFloat(data.get("radius"), r);
		h = ObjectUtils.toFloat(data.get("height"), h);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("x", x);
		data.put("y", y);
		data.put("z", z);
		data.put("radius", r);
		data.put("height", h);
	}

	@Override
	public void load(DataInput in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		r = in.readFloat();
		h = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(r);
		out.writeFloat(h);
	}

	@Override
	public ColliderType<CylinderCollider> getType() {
		return ColliderType.CYLINDER;
	}

	@Override
	public void render() {
		Renderer.render(Transform.calculateMatrix(new Matrix(), x, y, z, r, h, r), ResourceManager.getModel("ball"));
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
		return r;
	}

	@Override
	public float getHeight() {
		return h;
	}

	@Override
	public float getDepth() {
		return r;
	}
}
