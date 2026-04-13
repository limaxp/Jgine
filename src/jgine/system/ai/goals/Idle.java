package jgine.system.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.system.ai.AiGoal;
import jgine.system.ai.AiGoalType;
import jgine.system.ai.AiObject;
import jgine.utils.ObjectUtils;
import jgine.utils.math.FastMath;

public class Idle extends AiGoal {

	protected float minTime;
	protected float maxTime;
	protected float time;
	protected float counter;

	public Idle() {
	}

	public Idle(float minTime, float maxTime) {
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
		minTime = ObjectUtils.toFloat(data.get("minTime"), minTime);
		maxTime = ObjectUtils.toFloat(data.get("maxTime"), maxTime);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("minTime", minTime);
		data.put("maxTime", maxTime);
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
	public AiGoalType<Idle> getType() {
		return AiGoalType.IDLE;
	}
}
