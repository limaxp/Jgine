package org.jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.entity.Entity;
import org.jgine.system.UpdateManager;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.rotation.AxisAngle4f;
import org.jgine.utils.math.spacePartitioning.SpacePartitioning;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;

/**
 * A transform with float precision. Uses {@link Matrix} class internally. This
 * is your primary way to change entity position, rotation and scale. Also
 * stores a reference to its {@link Entity} and the scenes
 * {@link SpacePartitioning}.
 */
public class Transform implements Cloneable {

	private Entity entity;
	protected float posX;
	protected float posY;
	protected float posZ;
	protected float rotX;
	protected float rotY;
	protected float rotZ;
	protected float scaleX;
	protected float scaleY;
	protected float scaleZ;
	protected Matrix matrix;
	protected SpacePartitioning spacePartitioning;

	public Transform(Entity entity, float posX, float posY, float posZ, float rotX, float rotY, float rotZ,
			float scaleX, float scaleY, float scaleZ) {
		this.entity = entity;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.matrix = new Matrix();
		calculateMatrix();
		spacePartitioning = SpacePartitioning.NULL;
	}

	public final void calculateMatrix() {
		calculateMatrix(matrix, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
		if (entity.hasParent())
			matrix.mult(entity.getParent().transform.getMatrix());
		for (Entity child : entity.getChilds())
			child.transform.calculateMatrix();
	}

	public final Matrix getMatrix() {
		return matrix;
	}

	public final void setPosition(Vector2f position) {
		setPosition(position.x, position.y, 0.0f);
	}

	public final void setPosition(float x, float y) {
		setPosition(x, y, 0.0f);
	}

	public final void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}

	public final void setPosition(float x, float y, float z) {
		float oldX = getX();
		float oldY = getY();
		float oldZ = getZ();
		setPositionIntern(x, y, z);
		UpdateManager.getTransformPosition().accept(entity, getX() - oldX, getY() - oldY, getZ() - oldZ);
	}

	public final void setPositionIntern(float x, float y, float z) {
		float oldX = getX();
		float oldY = getY();
		float oldZ = getZ();
		posX = x;
		posY = y;
		posZ = z;
		calculateMatrix();
		spacePartitioning.move(entity, oldX, oldY, oldZ, getX(), getY(), getZ());
	}

	public final Vector3f getPosition() {
		return new Vector3f(matrix.m03, matrix.m13, matrix.m23);
	}

	public final float getX() {
		return matrix.m03;
	}

	public final float getY() {
		return matrix.m13;
	}

	public final float getZ() {
		return matrix.m23;
	}

	public final Vector3f getLocalPosition() {
		return new Vector3f(posX, posY, posZ);
	}

	public final float getLocalX() {
		return posX;
	}

	public final float getLocalY() {
		return posY;
	}

	public final float getLocalZ() {
		return posZ;
	}

	public final void setScale(float scale) {
		setScale(scale, scale, scale);
	}

	public final void setScale(Vector2f scale) {
		setScale(scale.x, scale.y, 0.0f);
	}

	public final void setScale(float x, float y) {
		setScale(x, y, 0.0f);
	}

	public final void setScale(Vector3f scale) {
		setScale(scale.x, scale.y, scale.z);
	}

	public final void setScale(float x, float y, float z) {
		float oldX = getScaleX();
		float oldY = getScaleY();
		float oldZ = getScaleZ();
		setScaleIntern(x, y, z);
		UpdateManager.getTransformScale().accept(entity, 1.0f + getScaleX() - oldX, 1.0f + getScaleY() - oldY,
				1.0f + getScaleZ() - oldZ);
	}

	public final void setScaleIntern(float x, float y, float z) {
		scaleX = x;
		scaleY = y;
		scaleZ = z;
		calculateMatrix();
	}

	public final Vector3f getScale() {
		return new Vector3f(matrix.m00, matrix.m11, matrix.m22);
	}

	public final float getScaleX() {
		return matrix.m00;
	}

	public final float getScaleY() {
		return matrix.m11;
	}

	public final float getScaleZ() {
		return matrix.m22;
	}

	public final Vector3f getLocalScale() {
		return new Vector3f(scaleX, scaleY, scaleZ);
	}

	public final float getLocalScaleX() {
		return scaleX;
	}

	public final float getLocalScaleY() {
		return scaleY;
	}

	public final float getLocalScaleZ() {
		return scaleZ;
	}

	public final void setRotation(Vector2f rotation) {
		setRotation(rotation.x, rotation.y);
	}

	public final void setRotation(float x, float y) {
		rotX = x;
		rotY = y;
		calculateMatrix();
	}

	public final void setRotation(Vector3f rotation) {
		setRotation(rotation.x, rotation.y, rotation.z);
	}

	public final void setRotation(float x, float y, float z) {
		rotX = x;
		rotY = y;
		rotZ = z;
		calculateMatrix();
	}

	public final AxisAngle4f getRotation() {
		return matrix.getRotation(new AxisAngle4f());
	}

	public final Vector3f getLocalRotation() {
		return new Vector3f(rotX, rotY, rotZ);
	}

	public final float getLocalRotationX() {
		return rotX;
	}

	public final float getLocalRotationY() {
		return rotY;
	}

	public final float getLocalRotationZ() {
		return rotZ;
	}

	public final void rotateX(float angle) {
		Vector3f rotation = getLocalRotation();
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, rotation));
		Vector3f result = Vector3f.normalize(Vector3f.rotate(rotation, angle, hAxis));
		setRotation(result.x, result.y, result.z);
	}

	public final void rotateY(float angle) {
		Vector3f rotation = getLocalRotation();
		Vector3f result = Vector3f.normalize(Vector3f.rotate(rotation, angle, Vector3f.Y_AXIS));
		setRotation(result.x, result.y, result.z);
	}

	final void cleanupEntity() {
		this.entity = null;
	}

	public final Entity getEntity() {
		return entity;
	}

	public final SpacePartitioning getSpacePartitioning() {
		return spacePartitioning;
	}

	@Override
	public Transform clone() {
		try {
			Transform obj = (Transform) super.clone();
			obj.matrix = new Matrix(matrix);
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final void load(DataInput in) throws IOException {
		posX = in.readFloat();
		posY = in.readFloat();
		posZ = in.readFloat();
		rotX = in.readFloat();
		rotY = in.readFloat();
		rotZ = in.readFloat();
		scaleX = in.readFloat();
		scaleY = in.readFloat();
		scaleZ = in.readFloat();
		calculateMatrix();
	}

	public final void save(DataOutput out) throws IOException {
		out.writeFloat(posX);
		out.writeFloat(posY);
		out.writeFloat(posZ);
		out.writeFloat(rotX);
		out.writeFloat(rotY);
		out.writeFloat(rotZ);
		out.writeFloat(scaleX);
		out.writeFloat(scaleY);
		out.writeFloat(scaleZ);
	}

	@Override
	public final String toString() {
		return super.toString() + "[pos: " + posX + "," + posY + "," + posZ + " | rot: " + rotX + "," + rotY + ","
				+ rotZ + " | scale: " + scaleX + "," + scaleY + "," + scaleZ + "]";
	}

	public static Matrix calculateMatrix2d(Matrix matrix, Vector2f position, Vector2f rotation, Vector2f scale) {
		return calculateMatrix2d(matrix, position.x, position.y, rotation.x, rotation.y, scale.x, scale.y);
	}

	public static Matrix calculateMatrix2d(Matrix matrix, float posX, float posY, float rotX, float rotY, float scaleX,
			float scaleY) {
		matrix.clear();
		matrix.setPosition(posX, posY, 0.0f);
		matrix.rotationXY(rotX, rotY);
		matrix.scaleLocal(scaleX, scaleY, 0.0f); // return new Matrix().scaling(scale).mult(matrix);
		return matrix;
	}

	public static Matrix calculateMatrix(Matrix matrix, Vector3f position, Vector3f rotation, Vector3f scale) {
		return calculateMatrix(matrix, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x,
				scale.y, scale.z);
	}

	public static Matrix calculateMatrix(Matrix matrix, float posX, float posY, float posZ, float rotX, float rotY,
			float rotZ, float scaleX, float scaleY, float scaleZ) {
		matrix.clear();
		matrix.setPosition(posX, posY, posZ);
		matrix.rotationXYZ(rotX, rotY, rotZ);
		matrix.scaleLocal(scaleX, scaleY, scaleZ); // return new Matrix().scaling(scale).mult(matrix);
		return matrix;
	}

	public static Matrix calculateMatrix2d(Matrix matrix, Vector2f position, Vector2f scale) {
		return calculateMatrix2d(matrix, position.x, position.y, scale.x, scale.y);
	}

	public static Matrix calculateMatrix2d(Matrix matrix, float posX, float posY, float scaleX, float scaleY) {
		matrix.clear();
		matrix.setPosition(posX, posY, 0.0f);
		matrix.scaleLocal(scaleX, scaleY, 0.0f);
		return matrix;
	}

	public static Matrix calculateMatrix(Matrix matrix, Vector3f position, Vector3f scale) {
		return calculateMatrix(matrix, position.x, position.y, position.z, scale.x, scale.y, scale.z);
	}

	public static Matrix calculateMatrix(Matrix matrix, float posX, float posY, float posZ, float scaleX, float scaleY,
			float scaleZ) {
		matrix.clear();
		matrix.setPosition(posX, posY, posZ);
		matrix.scaleLocal(scaleX, scaleY, scaleZ);
		return matrix;
	}
}
