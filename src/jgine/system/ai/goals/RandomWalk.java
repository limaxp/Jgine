package jgine.system.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.system.ai.AiGoal;
import jgine.system.ai.AiGoalType;
import jgine.system.ai.AiObject;
import jgine.system.ai.navigation.Navigation;
import jgine.utils.ObjectUtils;
import jgine.utils.math.FastMath;
import jgine.utils.math.vector.Vector3f;

public class RandomWalk extends AiGoal {

	public static final float START_CHANCE = 0.3f;

	protected Navigation navigation;
	protected Vector3f targetPos;
	protected float range;
	protected float time;

	public RandomWalk() {
	}

	public RandomWalk(float range) {
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.navigation = ai.getNavigation();
	}

	@Override
	public boolean canStart() {
		if (FastMath.random() > START_CHANCE)
			return false;
		return true;
	}

	@Override
	public void start() {
		targetPos = navigation.getPosition(range, range, 0); // TODO make this 3d able!
		time = 0.0f;
	}

	@Override
	public boolean update(float dt) {
		time += dt;
		if (time > 5.0f)
			return false;
		navigation.move(targetPos.x, targetPos.y, targetPos.z);
		return true;
	}

	@Override
	public void load(Map<String, Object> data) {
		range = ObjectUtils.toFloat(data.get("range"), range);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("range", range);
	}

	@Override
	public void load(DataInput in) throws IOException {
		range = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(range);
	}

	@Override
	public AiGoalType<RandomWalk> getType() {
		return AiGoalType.RANDOM_WALK;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
