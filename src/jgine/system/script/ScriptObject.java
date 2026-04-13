package jgine.system.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import javax.script.ScriptEngine;

import jgine.core.Entity;
import jgine.system.collision.Collider;
import jgine.system.collision.Collision;

public class ScriptObject extends Script implements Updateable {

	public final String name;
	protected ScriptEngine engine;
	protected IScript scriptInterface;

	public ScriptObject(ScriptEngine engine) {
		this(null, engine);
	}

	public ScriptObject(String name, ScriptEngine engine) {
		this.name = name;
		this.engine = engine;
	}

	@Override
	public void onEnable(Entity entity) {
		Object object = ScriptManager.eval(engine, "new " + name + "()");
		if (object instanceof IScript s) {
			scriptInterface = s;
			return;
		}
		scriptInterface = IScript.as(engine, object);
		scriptInterface.onEnable(entity);
	}

	@Override
	public void onDisable() {
		scriptInterface.onDisable();
	}

	@Override
	public void update() {
		scriptInterface.update();
	}

	@Override
	public void onCollision(Collision data, Entity other, Collider collider, Collider otherColider) {
		scriptInterface.onCollision(data, other, collider, otherColider);
	}

	@Override
	public void load(Map<String, Object> data) {
		scriptInterface.load(data);
	}

	@Override
	public void save(Map<String, Object> data) {
		scriptInterface.save(data);
	}

	@Override
	public void load(DataInput in) throws IOException {
		scriptInterface.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		scriptInterface.save(out);
	}

	@Override
	public String getName() {
		return name;
	}
}
