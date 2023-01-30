package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public abstract class AiGoal implements Cloneable {

	public abstract void init(AiObject object);

	public abstract boolean canStart();

	public abstract void start();

	public abstract boolean update(float dt);

	public abstract void load(Map<String, Object> data);

	public abstract void load(DataInput in) throws IOException;

	public abstract void save(DataOutput out) throws IOException;

	public abstract AiGoalType<?> getType();

	@Override
	public AiGoal clone() {
		try {
			return (AiGoal) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
