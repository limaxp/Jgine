package jgine.system.light;

import java.util.function.Supplier;

import jgine.core.Registry;
import jgine.render.light.DirectionalLight;
import jgine.render.light.Light;
import jgine.render.light.PointLight;

public class LightType<T extends Light> implements Supplier<T> {

	public static final LightType<DirectionalLight> DIRECTIONAL = as("directional", DirectionalLight::new);
	public static final LightType<PointLight> POINT = as("point", PointLight::new);

	public static <T extends Light> LightType<T> as(String name, Supplier<T> supplier) {
		LightType<T> type = new LightType<T>(name, supplier);
		type.id = Registry.LIGHT.register(name, type);
		return type;
	}

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public LightType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	public int id() {
		return id;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
