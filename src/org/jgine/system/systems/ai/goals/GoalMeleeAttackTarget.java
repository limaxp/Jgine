package org.jgine.system.systems.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiGoal;
import org.jgine.system.systems.ai.AiGoalType;
import org.jgine.system.systems.ai.AiGoalTypes;
import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.physic.PhysicObject;

public class GoalMeleeAttackTarget extends AiGoal {

	public static final float DEFAULT_RANGE = 100.0f;

	protected AiObject ai;
	protected Transform transform;
	protected PhysicObject physic;
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
		Entity entity = ai.getEntity();
		this.transform = entity.transform;
		this.physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
	}

	@Override
	public boolean canStart() {
		if (ai.getTarget() == null)
			return false;
		if (Vector2f.distance(transform.getPosition(), ai.getTarget().transform.getPosition()) > range)
			return false;
		return true;
	}

	@Override
	public void start() {
		Transform target = ai.getTarget().transform;
		Vector2f dirToTarget = Vector2f.normalize(Vector2f.sub(target.getPosition(), transform.getPosition()));
		physic.accelerate(Vector2f.mult(dirToTarget, 200000.0f));
		time = 0.0f;
	}

	@Override
	public boolean update(float dt) {
		if ((time += dt) > 2.0f)
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
