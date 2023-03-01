package org.jgine.system.systems.ai.goal.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.ai.goal.AiGoal;
import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.ai.goal.AiGoalTypes;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.FastMath;

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
	public void init(AiObject ai) {
	}
	
	@Override
	public boolean canStart() {
		return true;
	}

	@Override
	public void start() {
		time = FastMath.random(minTime, maxTime);
		counter = 0;
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
