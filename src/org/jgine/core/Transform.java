package org.jgine.core;

import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector3f;

/**
 * Represents a Transform for 3D with float precision.
 * 
 * @author Maximilian Paar
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

	public Transform(Entity entity, Vector3f position, Vector3f rotation, Vector3f scale) {
		this(entity, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x, scale.y, scale.z);
	}

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
		matrix = new Matrix();
		calculateMatrix();
	}

	public final void calculateMatrix() {
		calculateMatrix(matrix, posX, posY, posZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
		if (entity.hasParent())
			matrix.mult(entity.getParent().transform.getMatrix());
		for (Entity child : entity.getChilds())
			child.transform.calculateMatrix();
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

	public final Matrix getMatrix() {
		return matrix;
	}

	public final void setPosition(Vector2f position) {
		UpdateManager.update(entity, "transformPosition", setPositionIntern(position.x, position.y));
	}

	public final void setPosition(float x, float y) {
		UpdateManager.update(entity, "transformPosition", setPositionIntern(x, y));
	}

	public final void setPosition(Vector3f position) {
		UpdateManager.update(entity, "transformPosition", setPositionIntern(position.x, position.y, position.z));
	}

	public final void setPosition(float x, float y, float z) {
		UpdateManager.update(entity, "transformPosition", setPositionIntern(x, y, z));
	}

	public final Vector2f setPositionIntern(float x, float y) {
		posX = x;
		posY = y;
		calculateMatrix();
		return matrix.getPosition();
	}

	public final Vector3f setPositionIntern(float x, float y, float z) {
		posX = x;
		posY = y;
		posZ = z;
		calculateMatrix();
		return matrix.getPosition();
	}

	public final Vector3f getPosition() {
		return matrix.getPosition();
	}

	public final Vector3f getLocalPosition() {
		return new Vector3f(posX, posY, posZ);
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

	public final Vector3f getRotation() {
		// TODO implement this
		return matrix.getRotation();
	}

	public final Vector3f getLocalRotation() {
		return new Vector3f(rotX, rotY, rotZ);
	}

	public final void setScale(Vector3f scale) {
		setScale(scale.x, scale.y, scale.z);
	}

	public final void setScale(float scale) {
		setScale(scale, scale, scale);
	}

	public final void setScale(float x, float y, float z) {
		scaleX = x;
		scaleY = y;
		scaleZ = z;
		calculateMatrix();
	}

	public final Vector3f getScale() {
		return matrix.getScale();
	}

	public final Vector3f getLocalScale() {
		return new Vector3f(scaleX, scaleY, scaleZ);
	}

	public final void rotateX(float angle) {
		Vector3f rotation = getLocalRotation();
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, rotation));
		setRotation(Vector3f.normalize(Vector3f.rotate(rotation, angle, hAxis)));
	}

	public final void rotateY(float angle) {
		Vector3f rotation = getLocalRotation();
		setRotation(Vector3f.normalize(Vector3f.rotate(rotation, angle, Vector3f.Y_AXIS)));
	}

	final void cleanupEntity() {
		this.entity = null;
	}

	public final Entity getEntity() {
		return entity;
	}

	@Override
	public final String toString() {
		return super.toString() + "[pos: " + posX + "," + posY + "," + posZ + " rot: " + rotX + "," + rotY + "," + rotZ
				+ " scale: " + scaleX + "," + scaleY + "," + scaleZ + "]";
	}

	@Override
	public Transform clone() {
		try {
			Transform obj = (Transform) super.clone();
			obj.matrix = new Matrix();
			obj.calculateMatrix();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
