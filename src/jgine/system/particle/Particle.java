package jgine.system.particle;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jgine.core.Engine;
import jgine.render.material.Material;
import jgine.render.mesh.ParticleMesh;
import jgine.system.SystemObject;
import jgine.system.transform.Transform;
import jgine.utils.Color;
import jgine.utils.ObjectUtils;
import jgine.utils.math.vector.Vector3f;

public class Particle implements SystemObject {

	Transform transform;
	private Material material = new Material();
	public int amount;
	public Vector3f positionRange = new Vector3f(0.2f, 0.2f, 0.2f);
	public Vector3f velocityMin = new Vector3f(-1.0f, -1.0f, -1.0f);
	public Vector3f velocityRange = new Vector3f(2.0f, 2.0f, 2.0f);
	public Vector3f gravity = new Vector3f(0.0f, -1.0f, 0.0f);
	public float liveMin = 1.0f;
	public float liveRange = 1.0f;
	public float sizeMin = 0.1f;
	public float sizeRange = 0.2f;
	public int colorMin = Color.WHITE;
	public int colorRange = Color.BLACK;
	public float spawnTime = 0.2f;
	private float elapsedTime;
	private ParticleMesh mesh;

	public void close() {
		mesh.close();
	}

	ParticleMesh getMesh() {
		if (mesh == null)
			mesh = new ParticleMesh();
		return mesh;
	}

	public boolean checkSpawnTime(float dt) {
		elapsedTime += dt;
		if (elapsedTime > spawnTime) {
			elapsedTime = 0.0f;
			return true;
		}
		return false;
	}

	@Override
	public void load(Map<String, Object> data) {
		ObjectUtils.toMaterial(material, data.get("material"));
		amount = ObjectUtils.toInt(data.get("amount"), amount);
		positionRange = ObjectUtils.toVector3f(data.get("positionRange"), positionRange);
		velocityMin = ObjectUtils.toVector3f(data.get("velocityMin"), velocityMin);
		velocityRange = ObjectUtils.toVector3f(data.get("velocityRange"), velocityRange);
		gravity = ObjectUtils.toVector3f(data.get("gravity"), gravity);
		liveMin = ObjectUtils.toFloat(data.get("liveMin"), liveMin);
		liveRange = ObjectUtils.toFloat(data.get("liveRange"), liveRange);
		sizeMin = ObjectUtils.toFloat(data.get("sizeMin"), sizeMin);
		sizeRange = ObjectUtils.toFloat(data.get("sizeRange"), sizeRange);
		colorMin = ObjectUtils.toInt(data.get("colorMin"), colorMin);
		colorRange = ObjectUtils.toInt(data.get("colorRange"), colorRange);
		spawnTime = ObjectUtils.toFloat(data.get("spawnTime"), spawnTime);
	}

	@Override
	public void save(Map<String, Object> data) {
		Map<String, Object> materialMap = new HashMap<String, Object>();
		material.save(materialMap);
		data.put("material", materialMap);
		data.put("amount", amount);
		data.put("positionRange", Arrays.asList(positionRange.x, positionRange.y, positionRange.z));
		data.put("velocityMin", Arrays.asList(velocityMin.x, velocityMin.y, velocityMin.z));
		data.put("velocityRange", Arrays.asList(velocityRange.x, velocityRange.y, velocityRange.z));
		data.put("gravity", Arrays.asList(gravity.x, gravity.y, gravity.z));
		data.put("liveMin", liveMin);
		data.put("liveRange", liveRange);
		data.put("sizeMin", sizeMin);
		data.put("sizeRange", sizeRange);
		data.put("colorMin", colorMin);
		data.put("colorRange", colorRange);
		data.put("spawnTime", spawnTime);
	}

	@Override
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

	@Override
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

	public Material getMaterial() {
		return material;
	}

	@Override
	public int system() {
		return Engine.PARTICLE;
	}

	@Override
	public Particle clone() {
		try {
			Particle obj = (Particle) super.clone();
			obj.material = material.clone();
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}