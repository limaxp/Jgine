package org.jgine.system.systems.script;

import java.util.function.Supplier;

import org.jgine.utils.registry.Registry;

public class ScriptTypes {

	public static final ScriptType<ScriptObjectJava> NONE = a("none", () -> null);

	public static <T extends ScriptObjectJava> ScriptType<T> a(String name, Supplier<T> supplier) {
		ScriptType<T> type = new ScriptType<T>(name, supplier);
		type.setId(Registry.SCRIPT_TYPES.register(name, type));
		return type;
	}

	public static ScriptType<?> get(String name) {
		return Registry.SCRIPT_TYPES.get(name);
	}

	public static ScriptType<?> get(int id) {
		return Registry.SCRIPT_TYPES.get(id);
	}
}
