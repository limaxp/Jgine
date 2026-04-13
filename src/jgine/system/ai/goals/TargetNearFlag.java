package jgine.system.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import jgine.core.Entity;
import jgine.core.Scene;
import jgine.system.ai.AiGoal;
import jgine.system.ai.AiGoalType;
import jgine.system.ai.AiObject;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;

public class TargetNearFlag extends AiGoal {

	protected AiObject ai;
	protected Transform transform;
	protected Scene scene;
	protected int flag;
	protected float range;

	public TargetNearFlag() {
	}

	public TargetNearFlag(int flag, float range) {
		this.flag = flag;
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.ai = ai;
		Entity entity = ai.getEntity();
		this.transform = entity.getTransform();
		this.scene = entity.scene;
	}

	@Override
	public boolean canStart() {
		for (Entity entity : scene.getSpacePartitioning().getNear(transform.getX(), transform.getY(), transform.getZ(),
				range, new HashSet<>())) {
			if (entity.getFlag(flag)) {
				ai.setTarget(entity);
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
	}

	@Override
	public boolean update(float dt) {
		return false;
	}

	@Override
	public void load(Map<String, Object> data) {
		Object flagData = data.get("flag");
		if (flagData instanceof Number)
			flag = ((Number) flagData).intValue();
		range = ObjectUtils.toFloat(data.get("range"), range);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("flag", flag);
		data.put("range", range);
	}

	@Override
	public void load(DataInput in) throws IOException {
		flag = in.readInt();
		range = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(flag);
		out.writeFloat(range);
	}

	@Override
	public AiGoalType<TargetNearFlag> getType() {
		return AiGoalType.TARGET_NEAR_FLAG;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
