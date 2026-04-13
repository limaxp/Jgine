package jgine.system.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import jgine.utils.ObjectUtils;
import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.registry.Registry;

public class GoalSelector implements Cloneable {

	protected AiObject ai;
	protected List<AiGoal> goals;
	protected IntList priorities;

	public GoalSelector() {
		goals = new UnorderedIdentityArrayList<AiGoal>();
		priorities = new IntArrayList();
	}

	public void init(AiObject ai) {
		this.ai = ai;
		int size = goals.size();
		for (int i = 0; i < size; i++)
			goals.get(i).init(ai);
	}

	@SuppressWarnings("unchecked")
	public void load(Object data) {
		if (data instanceof List) {
			List<Object> goalList = (List<Object>) data;
			int goalSize = goalList.size();
			for (int i = 0; i < goalSize; i++) {
				Object goal = goalList.get(i);
				if (goal instanceof Map g)
					loadGoal(g, i);
			}
		} else if (data instanceof Map) {
			Map<String, Object> goalMap = (Map<String, Object>) data;
			int i = 0;
			for (Object goal : goalMap.values())
				if (goal instanceof Map g)
					loadGoal(g, i++);
		}
	}

	public List<Map<String, Object>> save() {
		int size = goals.size();
		@SuppressWarnings("unchecked")
		Map<String, Object>[] result = new Map[size];
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> subResult = new HashMap<String, Object>();
			result[i] = subResult;
			saveGoal(subResult, i);
		}
		return Arrays.asList(result);
	}

	private void loadGoal(Map<String, Object> data, int index) {
		AiGoalType<?> aiGoalType;
		Object type = data.get("type");
		if (type instanceof String) {
			aiGoalType = Registry.AI_GOAL.getOrDefault((String) type, AiGoalType.IDLE);
		} else
			aiGoalType = AiGoalType.IDLE;

		int priority = ObjectUtils.toInt(data.get("priority"), index + 1);
		AiGoal goal = aiGoalType.get();
		goal.load(data);
		addGoalIntern(priority, goal);
	}

	private void saveGoal(Map<String, Object> data, int index) {
		AiGoal goal = goals.get(index);
		data.put("type", goal.getType().id());
		data.put("priority", priorities.getInt(index));
		goal.save(data);
	}

	public void load(DataInput in) throws IOException {
		int goalSize = in.readInt();
		for (int i = 0; i < goalSize; i++) {
			priorities.add(in.readInt());
			AiGoal goal = Registry.AI_GOAL.get(in.readInt()).get();
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
			out.writeInt(goal.getType().id());
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

	public void clear() {
		goals.clear();
		priorities.clear();
	}

	public int size() {
		return goals.size();
	}
}
