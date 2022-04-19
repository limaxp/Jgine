package org.jgine.system.systems.light;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.Light;
import org.jgine.render.light.PointLight;
import org.jgine.system.data.ListSystemScene;

public class LightScene extends ListSystemScene<LightSystem, Light> {

	private final List<PointLight> pointLights;
	private final List<DirectionalLight> directionalLights;

	public LightScene(LightSystem system, Scene scene) {
		super(system, scene, Light.class);
		pointLights = new UnorderedIdentityArrayList<PointLight>();
		directionalLights = new UnorderedIdentityArrayList<DirectionalLight>();
	}

	@Override
	public Light addObject(Entity entity, Light object) {
		if (object instanceof PointLight)
			pointLights.add((PointLight) object);
		if (object instanceof DirectionalLight)
			directionalLights.add((DirectionalLight) object);
		return super.addObject(entity, object);
	}

	@Override
	@Nullable
	public Light removeObject(Light object) {
		if (object instanceof PointLight)
			pointLights.remove(object);
		if (object instanceof DirectionalLight)
			directionalLights.remove((DirectionalLight) object);
		return super.removeObject(object);
	}

	public List<PointLight> getPointLights() {
		return Collections.unmodifiableList(pointLights);
	}

	public List<DirectionalLight> getDirectionalLights() {
		return Collections.unmodifiableList(directionalLights);
	}
}
