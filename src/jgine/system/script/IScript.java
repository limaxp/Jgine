package jgine.system.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import javax.script.ScriptEngine;

import jgine.core.Entity;
import jgine.system.collision.Collider;
import jgine.system.collision.Collision;

public interface IScript extends ScriptBase {

	public default void onEnable(Entity entity) {
	}

	public default void onDisable() {
	}

	public default void update() {
	}

	public default void onCollision(Collision data, Entity other, Collider collider, Collider otherColider) {
	}

	public void load(Map<String, Object> data);

	public void save(Map<String, Object> data);

	public void load(DataInput in) throws IOException;

	public void save(DataOutput out) throws IOException;

	public static IScript as(ScriptEngine engine, Object object) {
		return new IScript() {

			@Override
			public void onEnable(Entity entity) {
				ScriptManager.invoke(engine, object, "onEnable", entity);
			}

			@Override
			public void onDisable() {
				ScriptManager.invoke(engine, object, "onDisable");
			}

			@Override
			public void update() {
				ScriptManager.invoke(engine, object, "update");
			}

			@Override
			public void onCollision(Collision data, Entity other, Collider collider, Collider otherColider) {
				ScriptManager.invoke(engine, object, "onCollision", data, other, collider, otherColider);
			}

			@Override
			public void load(Map<String, Object> data) {
				ScriptManager.invoke(engine, object, "load", data);
			}

			@Override
			public void save(Map<String, Object> data) {
				ScriptManager.invoke(engine, object, "save", data);
			}

			@Override
			public void load(DataInput in) throws IOException {
				ScriptManager.invoke(engine, object, "load", in);
			}

			@Override
			public void save(DataOutput out) throws IOException {
				ScriptManager.invoke(engine, object, "save", out);
			}
		};
	}
}
