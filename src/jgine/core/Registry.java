package jgine.core;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import jgine.system.EngineSystem;
import jgine.system.ai.AiGoalType;
import jgine.system.collision.ColliderType;
import jgine.system.input.InputHandler;
import jgine.system.light.LightType;
import jgine.system.script.ScriptBase;
import jgine.system.ui.UIObjectType;
import jgine.utils.Reflection;
import jgine.utils.registry.AbstractRegistry;
import jgine.utils.registry.KeyRegistry;

/**
 * Engine Registry
 * <p>
 * <strong> All registers should be done on startup otherwise not thread
 * safe!</strong> Supports up to 1000 different registries.
 * 
 */
public class Registry {

	public static final KeyRegistry<AbstractRegistry<?>> REGISTRY = AbstractRegistry.REGISTRY;
	public static final KeyRegistry<EngineSystem<?, ?>> SYSTEM = new KeyRegistry<EngineSystem<?, ?>>("system", 64);
	public static final KeyRegistry<ColliderType<?>> COLLIDER = new KeyRegistry<ColliderType<?>>("collider", 100);
	public static final KeyRegistry<UIObjectType<?>> UI_OBJECT = new KeyRegistry<UIObjectType<?>>("ui_objects", 100);
	public static final KeyRegistry<LightType<?>> LIGHT = new KeyRegistry<LightType<?>>("light", 10);
	public static final KeyRegistry<AiGoalType<?>> AI_GOAL = new KeyRegistry<AiGoalType<?>>("ai_goals", 1000);
	public static final KeyRegistry<Supplier<ScriptBase>> SCRIPT = new KeyRegistry<Supplier<ScriptBase>>("script",
			10000);
	public static final KeyRegistry<Supplier<InputHandler>> INPUT = new KeyRegistry<Supplier<InputHandler>>("input",
			1000);

	public static void init() {
		var _ = ColliderType.CIRCLE;
		var _ = UIObjectType.COMPOUND;
		var _ = LightType.DIRECTIONAL;
		var _ = AiGoalType.IDLE;

		Collection<URL> allPackagePrefixes = Arrays.stream(Package.getPackages()).map(p -> p.getName())
				.map(s -> s.split("\\.")[0]).distinct().map(s -> ClasspathHelper.forPackage(s)).reduce((c1, c2) -> {
					Collection<URL> c3 = new HashSet<>();
					c3.addAll(c1);
					c3.addAll(c2);
					return c3;
				}).get();
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().addUrls(allPackagePrefixes).addScanners(Scanners.SubTypes));

		for (Class<?> c : reflections.getSubTypesOf(ScriptBase.class))
			SCRIPT.register(c.getSimpleName(), () -> (ScriptBase) Reflection.newInstance_(c));

		for (Class<?> c : reflections.getSubTypesOf(InputHandler.class))
			INPUT.register(c.getSimpleName(), () -> (InputHandler) Reflection.newInstance_(c));
	}
}
