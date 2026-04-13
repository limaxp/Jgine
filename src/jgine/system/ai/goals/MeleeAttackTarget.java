package jgine.system.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.core.Entity;
import jgine.system.ai.AiGoal;
import jgine.system.ai.AiGoalType;
import jgine.system.ai.AiObject;
import jgine.system.ai.navigation.Navigation;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.math.vector.Vector3f;

public class MeleeAttackTarget extends AiGoal {

	public static final float COOLDOWN_TIME = 2.0f;

	protected AiObject ai;
	protected Navigation navigation;
	protected Transform transform;
	protected float range;
	protected float time;

	public MeleeAttackTarget() {
	}

	public MeleeAttackTarget(float range) {
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.ai = ai;
		this.navigation = ai.getNavigation();
		Entity entity = ai.getEntity();
		this.transform = entity.getTransform();
	}

	@Override
	public boolean canStart() {
		if (ai.getTarget() == null)
			return false;
		Transform target = ai.getTarget().getTransform();
		if (Vector3f.distanceSquared(transform.getX(), transform.getY(), transform.getZ(), target.getX(), target.getY(),
				target.getZ()) > range * range)
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
	public AiGoalType<MeleeAttackTarget> getType() {
		return AiGoalType.MELEE_ATTACK_TARGET;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
