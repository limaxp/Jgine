package org.jgine.system.systems.collision.collider;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.Renderer;
import org.jgine.render.material.Material;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.CollisionChecks;
import org.jgine.system.systems.collision.CollisionData;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector3f;

/**
 * Represents an AxisAlignedBoundingBox for 3D with float precision. An
 * AxisAlignedBoundingBox is represented by center point, width(w), height(h)
 * and depth(d).
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
	public void scale(Vector3f scale) {
		w *= scale.x;
		h *= scale.y;
		d *= scale.z;
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		return CollisionChecks.cubevsPoint(pos.x, pos.y, pos.z, w, h, d, point.x, point.y, point.z);
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.cubevsCube(pos.x, pos.y, pos.z, w, h, d, otherPos.x, otherPos.y, otherPos.z,
					((AxisAlignedBoundingBox) other).w, ((AxisAlignedBoundingBox) other).h,
					((AxisAlignedBoundingBox) other).d);
		else if (other instanceof SphereCollider)
			return CollisionChecks.cubevsSphere(pos.x, pos.y, pos.z, w, h, d, otherPos.x, otherPos.y, otherPos.z,
					((SphereCollider) other).r);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.cubevsPlane(pos.x, pos.y, pos.z, w, h, d, otherPos.x, otherPos.y, otherPos.z,
					((PlaneCollider) other).xNorm, ((PlaneCollider) other).yNorm, ((PlaneCollider) other).zNorm);
		else if (other instanceof CylinderCollider)
			return CollisionChecks.cubevsCylinder(pos.x, pos.y, pos.z, w, h, d, otherPos.x, otherPos.y, otherPos.z,
					((CylinderCollider) other).r, ((CylinderCollider) other).h);
		return false;
	}

	@Nullable
	@Override
	public CollisionData resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (other instanceof AxisAlignedBoundingBox)
			return CollisionChecks.resolveCubevsCube(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(AxisAlignedBoundingBox) other);
		else if (other instanceof SphereCollider)
			return CollisionChecks.resolveCubevsSphere(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(SphereCollider) other);
		else if (other instanceof PlaneCollider)
			return CollisionChecks.resolveCubevsPlane(pos.x, pos.y, pos.z, this, otherPos.x, otherPos.y, otherPos.z,
					(PlaneCollider) other);
		return null;
	}

	@Override
	public AxisAlignedBoundingBox clone() {
		return new AxisAlignedBoundingBox(w, h, d);
	}

	@Override
	public void load(Map<String, Object> data) {
		Object widthData = data.get("width");
		if (widthData != null)
			w = YamlHelper.toFloat(widthData);
		Object heightData = data.get("height");
		if (heightData != null)
			h = YamlHelper.toFloat(heightData);
		Object depthData = data.get("depth");
		if (depthData != null)
			d = YamlHelper.toFloat(depthData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		w = in.readFloat();
		h = in.readFloat();
		d = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(w);
		out.writeFloat(h);
		out.writeFloat(d);
	}

	@Override
	public ColliderType<AxisAlignedBoundingBox> getType() {
		return ColliderTypes.BOX;
	}

	@Override
	public void render(Vector3f pos) {
		Renderer.enableWireframeMode();
		Renderer.renderCube(Transform.calculateMatrix(new Matrix(), pos, new Vector3f(w, h, d)), new Material());
		Renderer.disableWireframeMode();
	}
}
