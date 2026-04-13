package jgine.system.ai.goals;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import jgine.core.Entity;
import jgine.core.Prefab;
import jgine.core.Scene;
import jgine.system.ai.AiGoal;
import jgine.system.ai.AiGoalType;
import jgine.system.ai.AiObject;
import jgine.system.transform.Transform;
import jgine.utils.ObjectUtils;

public class TargetNearTag extends AiGoal {

	protected AiObject ai;
	protected Transform transform;
	protected Scene scene;
	protected int tag;
	protected float range;

	public TargetNearTag() {
	}

	public TargetNearTag(int tag, float range) {
		this.tag = tag;
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
			if (entity.getPrefab().getTag(tag)) {
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
			tag = ((Number) tagData).intValue();
		if (tagData instanceof String)
			tag = Prefab.Tag.get((String) tagData);
		range = ObjectUtils.toFloat(data.get("range"), range);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("tag", Prefab.Tag.get(tag));
		data.put("range", range);
	}

	@Override
	public void load(DataInput in) throws IOException {
		tag = in.readInt();
		range = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(tag);
		out.writeFloat(range);
	}

	@Override
	public AiGoalType<TargetNearTag> getType() {
		return AiGoalType.TARGET_NEAR_TAG;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTag() {
		return tag;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
}
