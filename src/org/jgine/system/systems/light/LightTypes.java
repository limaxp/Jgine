package org.jgine.system.systems.light;

import java.util.function.Supplier;

import org.jgine.misc.utils.registry.Registry;
import org.jgine.render.light.DirectionalLight;
import org.jgine.render.light.Light;
import org.jgine.render.light.PointLight;

public class LightTypes {

	public static final LightType<DirectionalLight> DIRECTIONAL = a("directional", DirectionalLight::new);

	public static final LightType<PointLight> POINT = a("point", PointLight::new);

	public static <T extends Light> LightType<T> a(String name, Supplier<T> supplier) {
		LightType<T> type = new LightType<T>(name, supplier);
		Registry.LIGHT_TYPES.register(name, type);
		return type;
	}

	public static LightType<?> get(String name) {
		return Registry.LIGHT_TYPES.get(name);
	}

	public static LightType<?> get(int id) {
		return Registry.LIGHT_TYPES.get(id);
	}
}
