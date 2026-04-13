package jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import jgine.core.Entity;
import jgine.system.light.LightType;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.math.vector.Vector3f;

public class PointLight extends Light {

	private Transform transform;
	private Attenuation attenuation = Attenuation.DEFAULT;
	private float range = 1.0f;

	@Override
	public void setEntity(Entity entity) {
		transform = entity.getTransform();
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public Vector3f getPosition() {
		return transform.getPosition();
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		attenuation = ObjectUtils.toAttenuation(data.get("attenuation"), attenuation);
		range = ObjectUtils.toFloat(data.get("range"), range);
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		data.put("attenuation", Arrays.asList(attenuation.constant, attenuation.linear, attenuation.exponent));
		data.put("range", range);
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		attenuation = new Attenuation(in.readFloat(), in.readFloat(), in.readFloat());
		range = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeFloat(attenuation.constant);
		out.writeFloat(attenuation.linear);
		out.writeFloat(attenuation.exponent);
		out.writeFloat(range);
	}

	@Override
	public LightType<PointLight> getType() {
		return LightType.POINT;
	}

	@Override
	public PointLight clone() {
		PointLight object = (PointLight) super.clone();
		object.attenuation = attenuation;
		return object;
	}
}
