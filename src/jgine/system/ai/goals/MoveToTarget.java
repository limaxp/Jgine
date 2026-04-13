package jgine.system.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.core.Entity;
import jgine.system.ai.AiGoal;
import jgine.system.ai.AiGoalType;
import jgine.system.ai.AiObject;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;
import jgine.utils.math.vector.Vector3f;

public class MoveToTarget extends AiGoal {

	public static final float DISTANCE_CHECK_TIME = 5.0f;

	protected AiObject ai;
	protected Transform transform;
	protected Transform target;
	protected float range;
	protected float time;

	public MoveToTarget() {
	}

	public MoveToTarget(float range) {
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.ai = ai;
		Entity entity = ai.getEntity();
		this.transform = entity.getTransform();
	}

	@Override
	public boolean canStart() {
		if (ai.getTarget() == null)
			return false;
		return true;
	}

	@Override
	public void start() {
		target = ai.getTarget().getTransform();
	}

	@Override
	public boolean update(float dt) {
		time += dt;
		if (time > DISTANCE_CHECK_TIME) {
			time = 0.0f;
			if (Vector3f.distanceSquared(transform.getX(), transform.getY(), transform.getZ(), target.getX(),
					target.getY(), target.getZ()) > range * range) {
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
	public AiGoalType<MoveToTarget> getType() {
		return AiGoalType.MOVE_TO_TARGET;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
