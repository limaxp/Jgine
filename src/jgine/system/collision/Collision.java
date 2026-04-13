package jgine.system.collision;

public class Collision {

	private float normalX;
	private float normalY;
	private float normalZ;
	public final float posX;
	public final float posY;
	public final float posZ;
	public final float deltaX;
	public final float deltaY;
	public final float deltaZ;

	public Collision(float normalX, float normalY, float posX, float posY, float deltaX, float deltaY) {
		this(normalX, normalY, 0.0f, posX, posY, 0.0f, deltaX, deltaY, 0.0f);
	}

	public Collision(float normalX, float normalY, float normalZ, float posX, float posY, float posZ, float deltaX,
			float deltaY, float deltaZ) {
		this.normalX = normalX;
		this.normalY = normalY;
		this.normalZ = normalZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.deltaZ = deltaZ;
	}

	Collision reverse() {
		normalX = -normalX;
		normalY = -normalY;
		normalZ = -normalZ;
		return this;
	}

	public float getNormalX() {
		return normalX;
	}

	public float getNormalY() {
		return normalY;
	}

	public float getNormalZ() {
		return normalZ;
	}
}
