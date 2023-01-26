package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.FastMath;
import org.jgine.system.SystemObject;

public class AiObject implements SystemObject, Cloneable {

	protected Entity entity;
	protected AiGoal currentGoal;
	protected List<AiGoal> goals;

	public AiObject() {
		goals = new UnorderedIdentityArrayList<AiGoal>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public AiObject clone() {
		try {
			AiObject object = (AiObject) super.clone();
			object.currentGoal = null;
			object.goals = new UnorderedIdentityArrayList<AiGoal>();
			for (AiGoal goal : goals)
				object.goals.add(goal.clone());
			return object;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void load(Map<String, Object> data) {
		Object goals = data.get("goals");
		if (goals instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> goalList = (List<Object>) goals;
			for (Object subData : goalList)
				loadGoal(subData);
		} else if (goals instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> goalMap = (Map<String, Object>) goals;
			for (Object subData : goalMap.values())
				loadGoal(subData);
		}
	}

	private void loadGoal(Object data) {
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> childData = (Map<String, Object>) data;
			AiGoalType<?> aiGoalType;
			Object type = childData.get("type");
			if (type instanceof String) {
				aiGoalType = AiGoalTypes.get((String) type);
				if (aiGoalType == null)
					aiGoalType = AiGoalTypes.IDLE;
			} else
				aiGoalType = AiGoalTypes.IDLE;
			AiGoal goal = aiGoalType.get();
			goal.load(childData);
			goals.add(goal);
		}
	}

	public void load(DataInput in) throws IOException {
		int goalSize = in.readInt();
		for (int i = 0; i < goalSize; i++) {
			AiGoal goal = AiGoalTypes.get(in.readInt()).get();
			goal.load(in);
			goals.add(goal);
		}
	}

	public void save(DataOutput out) throws IOException {
		int goalSize = goals.size();
		out.writeInt(goalSize);
		for (int i = 0; i < goalSize; i++) {
			AiGoal goal = goals.get(i);
			out.writeInt(goal.getType().getId());
			goal.save(out);
		}
	}

	public void update(float dt) {
		if (currentGoal == null) {
			AiGoal nextGoal = rollGoal();
			if (nextGoal.start())
				currentGoal = nextGoal;
			else
				return;
		}
		if (!currentGoal.update(dt)) {
			currentGoal.finish();
			currentGoal = null;
		}
	}

	private AiGoal rollGoal() {
		return goals.get(FastMath.random(goals.size() - 1));
	}

	void setEntity(Entity entity) {
		this.entity = entity;
		for (AiGoal goal : goals)
			goal.setEntity(entity);
	}

	public Entity getEntity() {
		return entity;
	}

	public AiGoal getCurrentGoal() {
		return currentGoal;
	}

	public void addGoal(AiGoal goal) {
		goals.add(goal);
		goal.setEntity(entity);
	}

	public void removeGoal(AiGoal goal) {
		goals.remove(goal);
	}
}
