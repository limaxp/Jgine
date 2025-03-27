package org.jgine.system.systems.light;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.Light;
import org.jgine.render.light.PointLight;
import org.jgine.render.shader.PhongShader;
import org.jgine.system.data.EntityListSystemScene;
import org.jgine.utils.Color;
import org.jgine.utils.collection.list.UnorderedArrayList;

public class LightScene extends EntityListSystemScene<LightSystem, Light> {

	private int ambientLight;
	private List<PointLight> pointLights;
	private DirectionalLight directionalLight;

	public LightScene(LightSystem system, Scene scene) {
		super(system, scene, Light.class, 10000);
		ambientLight = Color.BLACK;
		pointLights = new UnorderedArrayList<PointLight>(PhongShader.MAX_POINT_LIGHTS);
		directionalLight = new DirectionalLight();
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Light object) {
		if (object instanceof PointLight)
			pointLights.add((PointLight) object);
		if (object instanceof DirectionalLight)
			directionalLight = (DirectionalLight) object;
		object.setEntity(entity);
	}

	@Override
	public Light removeObject(int index) {
		Light object = super.removeObject(index);
		if (object instanceof PointLight)
			pointLights.remove((PointLight) object);
		if (object instanceof DirectionalLight)
			directionalLight = new DirectionalLight();
		return object;
	}

	@Override
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		for (int i = 0; i < size; i++) {
			Light object = LightTypes.get(in.readInt()).get();
			object.load(in);
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			Light object = objects[i];
			out.writeInt(object.getType().getId());
			object.save(out);
		}
	}

	public void setAmbientLight(int ambientLight) {
		this.ambientLight = ambientLight;
	}

	public int getAmbientLight() {
		return ambientLight;
	}

	public List<PointLight> getPointLights() {
		return pointLights;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}
}
