package jgine.system.camera;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import jgine.core.Engine;
import jgine.core.input.Input;
import jgine.render.RenderTarget;
import jgine.system.SystemObject;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.Options;
import jgine.utils.math.Matrix;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector2i;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.scheduler.Service;

/**
 * Represents a 3D Camera
 */
public class Camera implements SystemObject {

	public static final byte MODE_PERSPECTIVE = 1;
	public static final byte MODE_ORTHOGRAPHIC = 2;

	protected Transform transform;
	private int width;
	private int height;
	private float zNear;
	private float zFar;
	private float fov;
	private Vector3f forward;
	private Vector3f up;
	private Matrix perspective;
	private Matrix orthographic;
	private Matrix projection;
	private Matrix viewProjection;
	private Matrix viewProjectionInverted;
	private RenderTarget renderTarget;
	private boolean dirty;

	public Camera() {
		this(MODE_PERSPECTIVE, Options.RESOLUTION_X.asInt(), Options.RESOLUTION_Y.asInt(), 0.1f, 1000.0f, 45f,
				Vector3f.Z_AXIS, Vector3f.UP);
	}

	public Camera(int width, int height, float zNear, float zFar) {
		this(MODE_ORTHOGRAPHIC, width, height, zNear, zFar, 45f, Vector3f.Z_AXIS, Vector3f.UP);
	}

	public Camera(int width, int height, float zNear, float zFar, float fov) {
		this(MODE_PERSPECTIVE, width, height, zNear, zFar, fov, Vector3f.Z_AXIS, Vector3f.UP);
	}

	public Camera(int width, int height, float zNear, float zFar, float fov, Vector3f forward) {
		this(MODE_PERSPECTIVE, width, height, zNear, zFar, fov, forward, Vector3f.UP);
	}

	public Camera(int width, int height, float zNear, float zFar, float fov, Vector3f forward, Vector3f up) {
		this(MODE_PERSPECTIVE, width, height, zNear, zFar, fov, forward, up);
	}

	protected Camera(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward,
			Vector3f up) {
		viewProjection = new Matrix();
		viewProjectionInverted = new Matrix();
		set(mode, width, height, zNear, zFar, fov, forward, up);
	}

	// loading constructor
	protected Camera(boolean ignored) {
		viewProjection = new Matrix();
		viewProjectionInverted = new Matrix();
		width = Options.RESOLUTION_X.asInt();
		height = Options.RESOLUTION_Y.asInt();
		zNear = 0.1f;
		zFar = 1000.0f;
		fov = 45f;
		forward = Vector3f.Z_AXIS;
		up = Vector3f.UP;
	}

	public final void set(byte mode, int width, int height, float zNear, float zFar, float fov, Vector3f forward,
			Vector3f up) {
		this.forward = forward;
		this.up = up;
		setPerspective(width, height, zNear, zFar, fov);
		setMode(mode);
	}

	public final void setPerspective(int width, int height, float zNear, float zFar, float fov) {
		this.width = width;
		this.height = height;
		this.zNear = zNear;
		this.zFar = zFar;
		this.fov = fov;
		perspective = Matrix.asPerspective(fov, width, height, zNear, zFar);
		int widthHalf = (int) (width * 0.5);
		int heightHalf = (int) (width * 0.5);
		orthographic = Matrix.asOrthographic(-widthHalf, widthHalf, -heightHalf, heightHalf, zNear, zFar);
		markDirty();
	}

	public final void setOrthographic(int width, int height, float zNear, float zFar) {
		setPerspective(width, height, zNear, zFar, fov);
	}

	public final void setMode(byte mode) {
		if (mode == MODE_PERSPECTIVE) {
			if (projection != perspective) {
				projection = perspective;
				markDirty();
			}
		} else if (mode == MODE_ORTHOGRAPHIC)
			if (projection != orthographic) {
				projection = orthographic;
				markDirty();
			}
	}

	public final byte getMode() {
		if (isPerspective())
			return MODE_PERSPECTIVE;
		else
			return MODE_ORTHOGRAPHIC;
	}

	public final String getModeName() {
		if (isPerspective())
			return "perspective";
		else
			return "orthographic";
	}

	public final boolean isPerspective() {
		return projection == perspective;
	}

	public final boolean isOrthographic() {
		return projection == orthographic;
	}

	public final void rotateX(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, forward));
		forward = Vector3f.normalize(Vector3f.rotate(forward, angle, hAxis));
		up = Vector3f.normalize(Vector3f.cross(forward, hAxis));
		markDirty();
	}

	public final void rotateY(float angle) {
		Vector3f hAxis = Vector3f.normalize(Vector3f.cross(Vector3f.Y_AXIS, forward));
		forward = Vector3f.normalize(Vector3f.rotate(forward, angle, Vector3f.Y_AXIS));
		up = Vector3f.normalize(Vector3f.cross(forward, hAxis));
		markDirty();
	}

	public final Matrix getViewProjection() {
		if (isDirty()) {
			calculateViewProjection(viewProjection);
			viewProjectionInverted.set(viewProjection).invert();
		}
		return viewProjection;
	}

	public final Matrix getViewProjectionInverted() {
		getViewProjection();
		return viewProjectionInverted;
	}

	public final void calculateViewProjection(Matrix viewProjection) {
		viewProjection.clear();
		Vector3f pos = transform.getPosition();
		viewProjection.setPosition(-pos.x, -pos.y, -pos.z);
		viewProjection.mult(Matrix.asCameraRotation(forward, up));
		viewProjection.mult(projection);
	}

	public final Vector2f viewToWorld(Vector2f pos) {
		Vector2i windowSize = Input.getWindowSize();
		float xNormal = 2 * pos.x / windowSize.x - 1;
		float yNormal = 2 * pos.y / windowSize.y - 1;
		Vector2f worldPos = Vector2f.mult(xNormal, yNormal, viewProjectionInverted);
		return new Vector2f(worldPos.x + transform.getX(), worldPos.y + transform.getY());
	}

	public final Vector2f worldtoView(Vector2f pos) {
		Vector2i windowSize = Input.getWindowSize();
		Vector2f viewdPos = Vector2f.mult(pos.x - transform.getX(), pos.y - transform.getY(), viewProjection);
		float x = (viewdPos.x + 1) * windowSize.x / 2;
		float y = (viewdPos.y + 1) * windowSize.y / 2;
		return new Vector2f(x, y);
	}

	public final Matrix getProjection() {
		return projection;
	}

	public final Matrix getPerspective() {
		return perspective;
	}

	public final Matrix getOrthographic() {
		return orthographic;
	}

	private final void markDirty() {
		this.dirty = true;
	}

	public final boolean isDirty() {
		return dirty;
	}

	public final Transform getTransform() {
		return transform;
	}

	public final void setForward(Vector3f vec) {
		forward = vec;
		markDirty();
	}

	public final Vector3f getForward() {
		return forward;
	}

	public final void setUp(Vector3f vec) {
		up = vec;
		markDirty();
	}

	public final Vector3f getUp() {
		return up;
	}

	public final Vector3f getLeft() {
		return Vector3f.cross(forward, up);
	}

	public final Vector3f getRight() {
		return Vector3f.cross(up, forward);
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public final float getZNear() {
		return zNear;
	}

	public final float getZFar() {
		return zFar;
	}

	public final float getFov() {
		return fov;
	}

	public final float getAspectRatio() {
		return (float) width / height;
	}

	public final void setRenderTarget(RenderTarget renderTarget) {
		this.renderTarget = renderTarget;
	}

	public final RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public final void setMainCamera() {
		Service.setSynchron("camera", this);
	}

	@Override
	public final void load(Map<String, Object> data) {
		byte modeValue = MODE_PERSPECTIVE;
		Object mode = data.get("mode");
		if (mode instanceof Number)
			modeValue = ((Number) mode).byteValue();
		else if (mode instanceof String) {
			if (((String) mode).equalsIgnoreCase("perspective"))
				modeValue = MODE_PERSPECTIVE;
			else if (((String) mode).equalsIgnoreCase("orthographic"))
				modeValue = MODE_ORTHOGRAPHIC;
		}
		int width = ObjectUtils.toInt(data.get("width"), this.width);
		int height = ObjectUtils.toInt(data.get("height"), this.height);
		float zNear = ObjectUtils.toFloat(data.get("zNear"), this.zNear);
		float zFar = ObjectUtils.toFloat(data.get("zFar"), this.zFar);
		float fov = ObjectUtils.toFloat(data.get("fov"), this.fov);
		Vector3f forward = ObjectUtils.toVector3f(data.get("forward"), this.forward);
		Vector3f up = ObjectUtils.toVector3f(data.get("up"), this.up);
		set(modeValue, width, height, zNear, zFar, fov, forward, up);
	}

	@Override
	public final void save(Map<String, Object> data) {
		data.put("mode", getModeName());
		data.put("width", width);
		data.put("height", height);
		data.put("zNear", zNear);
		data.put("zFar", zFar);
		data.put("fov", fov);
		data.put("forward", Arrays.asList(forward.x, forward.y, forward.z));
		data.put("up", Arrays.asList(up.x, up.y, up.z));
	}

	@Override
	public final void load(DataInput in) throws IOException {
		byte modeValue = in.readByte();
		int width = in.readInt();
		int height = in.readInt();
		float zNear = in.readFloat();
		float zFar = in.readFloat();
		float fov = in.readFloat();
		Vector3f forward = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		Vector3f up = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		set(modeValue, width, height, zNear, zFar, fov, forward, up);
	}

	@Override
	public final void save(DataOutput out) throws IOException {
		out.write(getMode());
		out.writeInt(width);
		out.writeInt(height);
		out.writeFloat(zNear);
		out.writeFloat(zFar);
		out.writeFloat(fov);
		out.writeFloat(forward.x);
		out.writeFloat(forward.y);
		out.writeFloat(forward.z);
		out.writeFloat(up.x);
		out.writeFloat(up.y);
		out.writeFloat(up.z);
	}

	@Override
	public int system() {
		return Engine.CAMERA;
	}

	@Override
	public final Camera clone() {
		try {
			return (Camera) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public final String toString() {
		return super.toString() + " [width: " + width + " | height: " + height + " | zNear: " + zNear + " | zFar: "
				+ zFar + " | fov: " + fov + " | forward: " + forward.x + "," + forward.y + "," + forward.z + " | up: "
				+ up.x + "," + up.y + "," + up.z + " | renderTarget: " + renderTarget + "]";
	}
}