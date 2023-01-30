package org.jgine.system.systems.ai.goal.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.ai.goal.AiGoal;
import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.ai.goal.AiGoalTypes;

public class GoalMoveToTarget extends AiGoal {

	public static final float DISTANCE_CHECK_TIME = 5.0f;
	public static final float DEFAULT_RANGE = 200.0f;

	protected AiObject ai;
	protected Transform transform;
	protected Transform target;
	protected float range;
	protected float time;

	public GoalMoveToTarget() {
	}

	public GoalMoveToTarget(float range) {
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.ai = ai;
		Entity entity = ai.getEntity();
		this.transform = entity.transform;
	}

	@Override
	public boolean canStart() {
		if (ai.getTarget() == null)
			return false;
		return true;
	}

	@Override
	public void start() {
		target = ai.getTarget().transform;
	}

	@Override
	public boolean update(float dt) {
		time += dt;
		if (time > DISTANCE_CHECK_TIME) {
			time = 0.0f;
			if (Vector3f.distance(transform.getPosition(), target.getPosition()) > range) {
				ai.setTarget(null);
				return false;
			}
		}
		Vector3f pos = target.getPosition();
		ai.getNavigation().move(pos.x, pos.y, pos.z);
		return true;
	}

	@Override
	public void load(Map<String, Object> data) {
		range = YamlHelper.toFloat(data.get("range"), DEFAULT_RANGE);
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
	public AiGoalType<GoalMoveToTarget> getType() {
		return AiGoalTypes.MOVE_TO_TARGET;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
