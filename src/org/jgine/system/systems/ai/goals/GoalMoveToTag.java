package org.jgine.system.systems.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Engine;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.entity.EntityTag;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiGoal;
import org.jgine.system.systems.ai.AiGoalType;
import org.jgine.system.systems.ai.AiGoalTypes;
import org.jgine.system.systems.physic.PhysicObject;

public class GoalMoveToTag extends AiGoal {

	public static final float DEFAULT_RANGE = 200.0f;

	protected Entity entity;
	protected Transform transform;
	protected PhysicObject physic;
	protected Transform target;
	protected int targetTag;
	protected float range;
	protected float time;

	public GoalMoveToTag() {
	}

	public GoalMoveToTag(int targetTag, float range) {
		this.targetTag = targetTag;
		this.range = range;
	}

	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
		this.transform = entity.transform;
		this.physic = entity.getSystem(Engine.PHYSIC_SYSTEM);
	}

	@Override
	public boolean start() {
		for (Entity entity : entity.scene.getEntitiesNear(transform.getPosition(), range)) {
			if (entity.getTag(targetTag)) {
				this.target = entity.transform;
				time = 0.0f;
				return true;
			}
		}
		return false;
	}

	@Override
	public void finish() {
	}

	@Override
	public boolean update(float dt) {
		time += dt;
		if (time > 10.0f)
			if (Vector2f.distance(transform.getPosition(), target.getPosition()) > range)
				return false;
			else
				time = 0.0f;
		Vector2f dirToTarget = Vector2f.normalize(Vector2f.sub(target.getPosition(), transform.getPosition()));
		physic.accelerate(Vector2f.mult(dirToTarget, 1000.0f));
		return true;
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
	public AiGoalType<GoalMoveToTag> getType() {
		return AiGoalTypes.MOVE;
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
