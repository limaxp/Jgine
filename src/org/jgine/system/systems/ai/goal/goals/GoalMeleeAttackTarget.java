package org.jgine.system.systems.ai.goal.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.ai.goal.AiGoal;
import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.ai.goal.AiGoalTypes;
import org.jgine.system.systems.ai.navigation.Navigation;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.vector.Vector3f;

public class GoalMeleeAttackTarget extends AiGoal {

	public static final float COOLDOWN_TIME = 2.0f;
	public static final float DEFAULT_RANGE = 100.0f;

	protected AiObject ai;
	protected Navigation navigation;
	protected Transform transform;
	protected float range;
	protected float time;

	public GoalMeleeAttackTarget() {
	}

	public GoalMeleeAttackTarget(float range) {
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.ai = ai;
		this.navigation = ai.getNavigation();
		Entity entity = ai.getEntity();
		this.transform = entity.transform;
	}

	@Override
	public boolean canStart() {
		if (ai.getTarget() == null)
			return false;
		if (Vector3f.distance(transform.getPosition(), ai.getTarget().transform.getPosition()) > range)
			return false;
		return true;
	}

	@Override
	public void start() {
		navigation.attack(ai.getTarget());
		time = 0.0f;
	}

	@Override
	public boolean update(float dt) {
		if ((time += dt) > COOLDOWN_TIME)
			return false;
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
	public AiGoalType<GoalMeleeAttackTarget> getType() {
		return AiGoalTypes.MELEE_ATTACK_TARGET;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
