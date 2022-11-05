package org.jgine.render.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.light.LightType;
import org.jgine.system.systems.light.LightTypes;

public class PointLight extends Light {

	private Attenuation attenuation = Attenuation.DEFAULT;
	private Vector3f pos = Vector3f.NULL;
	private float range = 1.0f;

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
		hasChanged = true;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setPosition(Vector3f pos) {
		this.pos = pos;
		hasChanged = true;
	}

	public Vector3f getPosition() {
		return pos;
	}

	public void setRange(float range) {
		this.range = range;
		hasChanged = true;
	}

	public float getRange() {
		return range;
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object attenuation = data.get("attenuation");
		if (attenuation != null && attenuation instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> attenuationMap = (Map<String, Object>) attenuation;
			this.attenuation = new Attenuation(YamlHelper.toFloat(attenuationMap.get("constant"), 0),
					YamlHelper.toFloat(attenuationMap.get("linear"), 0),
					YamlHelper.toFloat(attenuationMap.get("exponent"), 1));
		}
		pos = YamlHelper.toVector3f(data.get("pos"));
		range = YamlHelper.toFloat(data.get("range"), 1.0f);
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		attenuation = new Attenuation(in.readFloat(), in.readFloat(), in.readFloat());
		pos = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		range = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		out.writeFloat(attenuation.constant);
		out.writeFloat(attenuation.linear);
		out.writeFloat(attenuation.exponent);
		out.writeFloat(pos.x);
		out.writeFloat(pos.y);
		out.writeFloat(pos.z);
		out.writeFloat(range);
	}

	@Override
	public LightType<PointLight> getType() {
		return LightTypes.POINT;
	}
}
