package org.jgine.misc.utils.script;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jgine.core.entity.Entity;
import org.jgine.misc.utils.function.TriConsumer;
import org.jgine.system.SystemObject;
import org.jgine.system.systems.script.IScript;
import org.jgine.system.systems.script.ScriptObject;

public class EventManager {

	private static Function<Entity, SystemObject[]> scriptSupplier = (entity) -> null;

	public static void setScriptSupplier(Function<Entity, SystemObject[]> supplier) {
		scriptSupplier = supplier;
	}

	public static SystemObject[] getScripts(Entity entity) {
		return scriptSupplier.apply(entity);
	}

	public static <T> void callEvent(Entity entity, T t, BiConsumer<IScript, T> func) {
		SystemObject[] scripts = getScripts(entity);
		if (scripts != null)
			for (int i = 0; i < scripts.length; i++)
				func.accept(((ScriptObject) scripts[i]).getInterface(), t);
	}

	public static <T1, T2> void callEvent(Entity entity, T1 t1, T2 t2, TriConsumer<IScript, T1, T2> func) {
		SystemObject[] scripts = getScripts(entity);
		if (scripts != null)
			for (int i = 0; i < scripts.length; i++)
				func.accept(((ScriptObject) scripts[i]).getInterface(), t1, t2);
	}
}
