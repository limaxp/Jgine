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
import org.jgine.system.data.ObjectSystemScene.EntitySystemScene;
import org.jgine.utils.Color;
import org.jgine.utils.collection.list.UnorderedArrayList;

public class LightScene extends EntitySystemScene<LightSystem, Light> {

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
	public void onInit(Entity entity, Light object) {
		if (object instanceof PointLight)
			pointLights.add((PointLight) object);
		if (object instanceof DirectionalLight)
			directionalLight = (DirectionalLight) object;
		object.setEntity(entity);
	}

	@Override
	public void onRemove(Entity entity, Light object) {
		if (object instanceof PointLight)
			pointLights.remove((PointLight) object);
		if (object instanceof DirectionalLight)
			directionalLight = new DirectionalLight();
	}

	@Override
	protected void saveData(Light object, DataOutput out) throws IOException {
		out.writeInt(object.getType().getId());
		object.save(out);
	}

	@Override
	protected Light loadData(DataInput in) throws IOException {
		Light object = LightTypes.get(in.readInt()).get();
		object.load(in);
		return object;
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
