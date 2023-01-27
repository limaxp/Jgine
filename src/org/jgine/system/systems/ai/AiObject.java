package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.misc.collection.list.IntList;
import org.jgine.misc.collection.list.arrayList.IntArrayList;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.FastMath;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.SystemObject;

public class AiObject implements SystemObject, Cloneable {

	public static final int DEFAULT_PRIORITY = 5;

	protected Entity entity;
	protected AiGoal currentGoal;
	protected List<AiGoal> goals;
	protected IntList priorities;

	public AiObject() {
		goals = new UnorderedIdentityArrayList<AiGoal>();
		priorities = new IntArrayList();
	}

	public void update(float dt) {
		if (currentGoal == null) {
			int index = randomBiased(goals.size(), 0.4f);
			AiGoal nextGoal;
			do {
				nextGoal = goals.get(index--);
			} while (!nextGoal.start() && index >= 0);
			currentGoal = nextGoal;
		}
		if (!currentGoal.update(dt)) {
			currentGoal.finish();
			currentGoal = null;
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

			int priority = YamlHelper.toInt(childData.get("priority"), DEFAULT_PRIORITY);
			AiGoal goal = aiGoalType.get();
			goal.load(childData);
			addGoalIntern(priority, goal);
		}
	}

	public void load(DataInput in) throws IOException {
		int goalSize = in.readInt();
		for (int i = 0; i < goalSize; i++) {
			priorities.add(in.readInt());
			AiGoal goal = AiGoalTypes.get(in.readInt()).get();
			goal.load(in);
			goals.add(goal);
		}
	}

	public void save(DataOutput out) throws IOException {
		int goalSize = goals.size();
		out.writeInt(goalSize);
		for (int i = 0; i < goalSize; i++) {
			out.writeInt(priorities.getInt(i));
			AiGoal goal = goals.get(i);
			out.writeInt(goal.getType().getId());
			goal.save(out);
		}
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
			object.priorities = new IntArrayList(priorities);
			return object;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
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

	public void addGoal(int priority) {
		addGoal(DEFAULT_PRIORITY, currentGoal);
	}

	public void addGoal(int priority, AiGoal goal) {
		addGoalIntern(priority, goal);
		goal.setEntity(entity);
	}

	protected void addGoalIntern(int priority, AiGoal goal) {
		int index = getIndex(priority);
		goals.add(index, goal);
		priorities.add(index, priority);
	}

	public void removeGoal(AiGoal goal) {
		int index = goals.indexOf(goal);
		if (index == -1)
			return;
		goals.remove(index);
		priorities.removeInt(index);
	}

	private int getIndex(int priority) {
		int size = priorities.size();
		if (size == 0)
			return 0;
		if (priority >= priorities.getInt(size - 1))
			return size;
		if (priority <= priorities.getInt(0))
			return 0;
		return searchIndex(priority);
	}

	private int searchIndex(int priority) {
		int size = priorities.size();
		for (int i = 0; i < size; i++) {
			if (priorities.getInt(i) > priority)
				return i;
		}
		return -1;
	}

	/**
	 * This is useful because you can adjust bias to tweak the "rareness" of rarer
	 * items. A bias > 1 will favor lower numbers, < 1 will favor higher numbers, 1
	 * will be uniform.
	 * 
	 * @param max
	 * @param bias
	 * @return
	 */
	private static int randomBiased(int max, float bias) {
		return (int) (max * Math.pow(FastMath.random(), bias));
	}
}
