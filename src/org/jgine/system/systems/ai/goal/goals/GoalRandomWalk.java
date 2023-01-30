package org.jgine.system.systems.ai.goal.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.ai.goal.AiGoal;
import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.ai.goal.AiGoalTypes;
import org.jgine.system.systems.physic.PhysicObject;

public class GoalRandomWalk extends AiGoal {

	public static final float START_CHANCE = 0.3f;
	public static final float DEFAULT_RANGE = 200.0f;

	protected Transform transform;
	protected PhysicObject physic;
	protected Vector2f targetPos;
	protected float range;
	protected float time;

	public GoalRandomWalk() {
	}

	public GoalRandomWalk(float range) {
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		Entity entity = ai.getEntity();
		this.transform = entity.transform;
		this.physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
	}

	@Override
	public boolean canStart() {
		if (FastMath.random() > START_CHANCE)
			return false;
		return true;
	}

	@Override
	public void start() {
		Vector2f pos = transform.getPosition();
		targetPos = new Vector2f(pos.x + FastMath.random(-range, range), pos.y + FastMath.random(-range, range));
		time = 0.0f;
	}

	@Override
	public boolean update(float dt) {
		time += dt;
		if (time > 5.0f)
			return false;
		Vector2f dirToTarget = Vector2f.normalize(Vector2f.sub(targetPos, transform.getPosition()));
		physic.accelerate(Vector2f.mult(dirToTarget, 1000.0f));
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
	public AiGoalType<GoalRandomWalk> getType() {
		return AiGoalTypes.RANDOM_WALK;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
