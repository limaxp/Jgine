package org.jgine.system.systems.ai.goal.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.core.entity.EntityTag;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiObject;
import org.jgine.system.systems.ai.goal.AiGoal;
import org.jgine.system.systems.ai.goal.AiGoalType;
import org.jgine.system.systems.ai.goal.AiGoalTypes;

public class GoalTargetNearTag extends AiGoal {

	public static final float DEFAULT_RANGE = 200.0f;

	protected AiObject ai;
	protected Entity entity;
	protected int targetTag;
	protected float range;

	public GoalTargetNearTag() {
	}

	public GoalTargetNearTag(int targetTag, float range) {
		this.targetTag = targetTag;
		this.range = range;
	}

	@Override
	public void init(AiObject ai) {
		this.ai = ai;
		this.entity = ai.getEntity();
	}

	@Override
	public boolean canStart() {
		for (Entity entity : entity.scene.getEntitiesNear(entity.transform.getPosition(), range)) {
			if (entity.getTag(targetTag)) {
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
		Object tagData = data.get("tag");
		if (tagData instanceof Number)
			targetTag = ((Number) tagData).intValue();
		if (tagData instanceof String)
			targetTag = EntityTag.get((String) tagData);
		range = YamlHelper.toFloat(data.get("range"), DEFAULT_RANGE);
	}

	@Override
	public void load(DataInput in) throws IOException {
		targetTag = in.readInt();
		range = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(targetTag);
		out.writeFloat(range);
	}

	@Override
	public AiGoalType<GoalTargetNearTag> getType() {
		return AiGoalTypes.TARGET_NEAR_TAG;
	}

	public void setTargetTag(int targetTag) {
		this.targetTag = targetTag;
	}

	public int getTargetTag() {
		return targetTag;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
