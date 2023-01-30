package org.jgine.system.systems.ai.goal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jgine.misc.collection.list.IntList;
import org.jgine.misc.collection.list.arrayList.IntArrayList;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.system.systems.ai.AiObject;

public class GoalSelector implements Cloneable {

	public final AiObject ai;
	protected List<AiGoal> goals;
	protected IntList priorities;

	public GoalSelector(AiObject ai) {
		this.ai = ai;
		goals = new UnorderedIdentityArrayList<AiGoal>();
		priorities = new IntArrayList();
	}

	public void load(Object data) {
		if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> goalList = (List<Object>) data;
			int goalSize = goalList.size();
			for (int i = 0; i < goalSize; i++)
				loadGoal(goalList.get(i), i + 1);
		} else if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> goalMap = (Map<String, Object>) data;
			int i = 1;
			for (Object goal : goalMap.values())
				loadGoal(goal, i++);
		}
	}

	private void loadGoal(Object data, int priorityIndex) {
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

			int priority = YamlHelper.toInt(childData.get("priority"), priorityIndex);
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

	@Override
	public GoalSelector clone() {
		try {
			GoalSelector object = (GoalSelector) super.clone();
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

	public void addGoal(int priority, AiGoal goal) {
		addGoalIntern(priority, goal);
		goal.init(ai);
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

	protected int getIndex(int priority) {
		int size = priorities.size();
		if (size == 0)
			return 0;
		if (priority >= priorities.getInt(size - 1))
			return size;
		if (priority < priorities.getInt(0))
			return 0;
		return searchIndex(priority);
	}

	protected int searchIndex(int priority) {
		int size = priorities.size();
		for (int i = 0; i < size; i++) {
			if (priorities.getInt(i) > priority)
				return i;
		}
		return -1;
	}

	public AiGoal getGoal(int index) {
		return goals.get(index);
	}

	public int getPriority(int index) {
		return priorities.getInt(index);
	}

	public int size() {
		return goals.size();
	}
}
