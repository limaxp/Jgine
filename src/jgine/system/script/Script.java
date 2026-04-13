package jgine.system.script;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.core.Engine;
import jgine.system.SystemObject;

public abstract class Script implements SystemObject, IScript {

	@Override
	public void load(Map<String, Object> data) {
	}

	@Override
	public void save(Map<String, Object> data) {
	}

	@Override
	public void load(DataInput in) throws IOException {
	}

	@Override
	public void save(DataOutput out) throws IOException {
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public int system() {
		return Engine.SCRIPT;
	}

	@Override
	public Script clone() {
		try {
			return (Script) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
