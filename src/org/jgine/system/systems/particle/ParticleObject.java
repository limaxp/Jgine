package org.jgine.system.systems.particle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;
import org.jgine.render.mesh.particle.Particle;
import org.jgine.system.SystemObject;
import org.jgine.utils.Color;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.vector.Vector3f;

public class ParticleObject extends Particle implements SystemObject {

	Transform transform;
	public Material material = new Material();
	public int amount;
	public Vector3f positionRange = new Vector3f(0.2f, 0.2f, 0.2f);
	public Vector3f velocityMin = new Vector3f(-1.0f, -1.0f, -1.0f);
	public Vector3f velocityRange = new Vector3f(2.0f, 2.0f, 2.0f);
	public Vector3f gravity = new Vector3f(0.0f, -1.0f, 0.0f);
	public float liveMin = 1.0f;
	public float liveRange = 1.0f;
	public float sizeMin = 0.1f;
	public float sizeRange = 0.1f;
	public int colorMin = Color.WHITE;
	public int colorRange = Color.BLACK;
	public float spawnTime = 0.2f;
	private float elapsedTime;

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public ParticleObject clone() {
		try {
			ParticleObject obj = (ParticleObject) super.clone();
			obj.material = material.clone();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean checkSpawnTime(float dt) {
		elapsedTime += dt;
		if (elapsedTime > spawnTime) {
			elapsedTime = 0.0f;
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void load(Map<String, Object> data) {
		Object materialData = data.get("material");
		if (materialData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) materialData);
			if (texture != null)
				material.setTexture(texture);
		} else if (materialData instanceof Map)
			material.load((Map<String, Object>) materialData);

		Object amountData = data.get("amount");
		if (amountData != null)
			amount = YamlHelper.toInt(amountData);

		Object positionRangeData = data.get("positionRange");
		if (positionRangeData != null)
			positionRange = YamlHelper.toVector3f(positionRangeData);

		Object velocityMinData = data.get("velocityMin");
		if (velocityMinData != null)
			velocityMin = YamlHelper.toVector3f(velocityMinData);

		Object velocityRangeData = data.get("velocityRange");
		if (velocityRangeData != null)
			velocityRange = YamlHelper.toVector3f(velocityRangeData);

		Object gravityData = data.get("gravity");
		if (gravityData != null)
			gravity = YamlHelper.toVector3f(gravityData);

		Object liveMinData = data.get("liveMin");
		if (liveMinData != null)
			liveMin = YamlHelper.toFloat(liveMinData);

		Object liveRangeData = data.get("liveRange");
		if (liveRangeData != null)
			liveRange = YamlHelper.toFloat(liveRangeData);

		Object sizeMinData = data.get("sizeMin");
		if (sizeMinData != null)
			sizeMin = YamlHelper.toFloat(sizeMinData);

		Object sizeRangeData = data.get("sizeRange");
		if (sizeRangeData != null)
			sizeRange = YamlHelper.toFloat(sizeRangeData);

		Object colorMinData = data.get("colorMin");
		if (colorMinData != null)
			colorMin = YamlHelper.toInt(colorMinData);

		Object colorRangeData = data.get("colorRange");
		if (colorRangeData != null)
			colorRange = YamlHelper.toInt(colorRangeData);

		Object spawnTimeData = data.get("spawnTime");
		if (spawnTimeData != null)
			spawnTime = YamlHelper.toFloat(spawnTimeData);
	}

	public void load(DataInput in) throws IOException {
		material.load(in);
		amount = in.readInt();
		positionRange = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		velocityMin = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		velocityRange = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		gravity = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		liveMin = in.readFloat();
		liveRange = in.readFloat();
		sizeMin = in.readFloat();
		sizeRange = in.readFloat();
		colorMin = in.readInt();
		colorRange = in.readInt();
		spawnTime = in.readFloat();
	}

	public void save(DataOutput out) throws IOException {
		material.save(out);
		out.writeInt(amount);
		out.writeFloat(positionRange.x);
		out.writeFloat(positionRange.y);
		out.writeFloat(positionRange.z);
		out.writeFloat(velocityMin.x);
		out.writeFloat(velocityMin.y);
		out.writeFloat(velocityMin.z);
		out.writeFloat(velocityRange.x);
		out.writeFloat(velocityRange.y);
		out.writeFloat(velocityRange.z);
		out.writeFloat(gravity.x);
		out.writeFloat(gravity.y);
		out.writeFloat(gravity.z);
		out.writeFloat(liveMin);
		out.writeFloat(liveRange);
		out.writeFloat(sizeMin);
		out.writeFloat(sizeRange);
		out.writeInt(colorMin);
		out.writeInt(colorRange);
		out.writeFloat(spawnTime);
	}

	public Transform getTransform() {
		return transform;
	}
}