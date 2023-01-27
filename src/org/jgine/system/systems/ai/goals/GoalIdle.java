package org.jgine.system.systems.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiGoal;
import org.jgine.system.systems.ai.AiGoalType;
import org.jgine.system.systems.ai.AiGoalTypes;

public class GoalIdle extends AiGoal {

	public static final float DEFAULT_MIN_TIME = 1.0f;
	public static final float DEFAULT_MAX_TIME = 5.0f;

	protected float minTime;
	protected float maxTime;
	protected float time;
	protected float counter;

	public GoalIdle() {
	}

	public GoalIdle(float minTime, float maxTime) {
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	@Override
	public void setEntity(Entity entity) {
	}

	@Override
	public boolean start() {
		time = FastMath.random(minTime, maxTime);
		counter = 0;
		return true;
	}

	@Override
	public void finish() {
	}

	@Override
	public boolean update(float dt) {
		counter += dt;
		if (counter > time)
			return false;
		return true;
	}

	@Override
	public void load(Map<String, Object> data) {
		minTime = YamlHelper.toFloat(data.get("minTime"), DEFAULT_MIN_TIME);
		maxTime = YamlHelper.toFloat(data.get("maxTime"), DEFAULT_MAX_TIME);
	}

	@Override
	public void load(DataInput in) throws IOException {
		minTime = in.readFloat();
		maxTime = in.readFloat();
		time = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(minTime);
		out.writeFloat(maxTime);
		out.writeFloat(time);
	}

	public void setMinTime(float minTime) {
		this.minTime = minTime;
	}

	public float getMinTime() {
		return minTime;
	}

	public void setMaxTime(float maxTime) {
		this.maxTime = maxTime;
	}

	public float getMaxTime() {
		return maxTime;
	}

	@Override
	public AiGoalType<GoalIdle> getType() {
		return AiGoalTypes.IDLE;
	}
}
