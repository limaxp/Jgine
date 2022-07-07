package org.jgine.system.systems.camera;

import org.jgine.core.entity.Transform;
import org.jgine.core.manager.ServiceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

/**
 * Represents a 3D Camera
 * 
 * @author Maximilian Paar
 */
public class Camera implements SystemObject {

	protected Transform transform;
	private Vector3f forward;
	private Vector3f up;
	private Perspective perspective;
	private Orthographic orthographic;
	private Matrix perspectiveMatrix;
	private Matrix orthographicMatrix;
	private Matrix usedMatrix;

	public Camera() {
		this(Vector3f.Z_AXIS, Perspective.DEFAULT);
	}

	public Camera(Perspective perspective) {
		this(Vector3f.Z_AXIS, perspective);
	}

	public Camera(Orthographic orthographic) {
		this(Vector3f.Z_AXIS, orthographic);
	}

	public Camera(Vector3f forward, Perspective perspective) {
		this.forward = forward;
		this.up = Vector3f.UP;
		setOrthographic(Orthographic.DEFAULT);
		setPerspective(perspective);
	}

	public Camera(Vector3f forward, Orthographic orthographic) {
		this.forward = forward;
		this.up = Vector3f.UP;
		setPerspective(Perspective.DEFAULT);
		setOrthographic(orthographic);
	}

	public final void setPerspective(Perspective perspective) {
		this.perspective = perspective;
		perspectiveMatrix = Matrix.asPerspective(perspective.fov, perspective.width, perspective.height,
				perspective.zNear, perspective.zFar);
//		perspectiveMatrix = new Matrix();
//		perspectiveMatrix.setPerspective(perspective.fov, perspective.width / perspective.height, perspective.zNear, perspective.zFar, false);
		setPerspective();
	}

	public final void setPerspective() {
		usedMatrix = perspectiveMatrix;
	}

	public Perspective getPerspective() {
		return perspective;
	}

	public final void setOrthographic(Orthographic orthographic) {
		this.orthographic = orthographic;
		orthographicMatrix = Matrix.asOrthographic(orthographic.left, orthographic.right, orthographic.bottom,
				orthographic.top, orthographic.zNear, orthographic.zFar);
//		orthographicMatrix = new Matrix();
//		orthographicMatrix.setOrthographic(orthographic.left, orthographic.right, orthographic.bottom,
//			orthographic.top, orthographic.zNear, orthographic.zFar, true);
		setOrthographic();
	}

	public final void setOrthographic() {
		usedMatrix = orthographicMatrix;
	}

	public Orthographic getOrthographic() {
		return orthographic;
	}

	public final void rotateX(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, forward));
		forward = Vector3f.normalize(Vector3f.rotate(forward, angle, hAxis));
		up = Vector3f.normalize(Vector3f.cross(forward, hAxis));
	}

	public final void rotateY(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, forward));
		forward = Vector3f.normalize(Vector3f.rotate(forward, angle, Vector3f.Y_AXIS));
		up = Vector3f.normalize(Vector3f.cross(forward, hAxis));
	}

	public final Matrix getMatrix() {
		Vector3f pos = transform.getPosition();
		Matrix matrix = new Matrix();
		matrix.setPosition(-pos.x, -pos.y, -pos.z);
		matrix.mult(Matrix.asCameraRotation(forward, up));
		matrix.mult(usedMatrix);
		return matrix;

		// return new Matrix()
		// .perspective((float) Math.toRadians(perspective.fov),
		// perspective.getAspectRatio(), perspective.zNear,
		// perspective.zFar)
		// .lookAt(pos, forward, up);
	}

	public Matrix getPerspectiveMatrix() {
		return perspectiveMatrix;
	}

	public Matrix getOrthographicMatrix() {
		return orthographicMatrix;
	}

	public Transform getTransform() {
		return transform;
	}

	public Vector3f getForward() {
		return forward;
	}

	public Vector3f getUp() {
		return up;
	}

	public final Vector3f getLeft() {
		return Vector3f.cross(forward, up);
	}

	public final Vector3f getRight() {
		return Vector3f.cross(up, forward);
	}

	public void setMainCamera() {
		ServiceManager.set("camera", this);
	}
}
