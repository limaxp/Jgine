package org.jgine.system.systems.ai;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.system.SystemObject;

public class AiObject implements SystemObject, Cloneable {

	public static final float TARGET_UPDATE_TIME = 0.5f;

	protected Entity entity;
	protected GoalSelector goalSelector;
	protected GoalSelector targetSelector;
	protected AiGoal currentGoal;
	protected Entity target;
	protected float time;

	public AiObject() {
		goalSelector = new GoalSelector(this);
		targetSelector = new GoalSelector(this);
	}

	protected void init(Entity entity) {
		this.entity = entity;
		for (AiGoal goal : goalSelector.goals)
			goal.init(this);
		for (AiGoal goal : targetSelector.goals)
			goal.init(this);
		chooseTarget();
		chooseGoal();
	}

	protected void update(float dt) {
		if ((time += dt) > TARGET_UPDATE_TIME) {
			time = 0.0f;
			if (chooseTarget())
				chooseGoal();
		}
		if (!currentGoal.update(dt))
			chooseGoal();
	}

	protected boolean chooseTarget() {
		int targetSize = targetSelector.size();
		for (int i = 0; i < targetSize; i++) {
			AiGoal nextTargetGoal = targetSelector.getGoal(i);
			if (nextTargetGoal.canStart()) {
				nextTargetGoal.start();
				return true;
			}
		}
		return false;
	}

	protected boolean chooseGoal() {
		int goalSize = goalSelector.size();
		for (int i = 0; i < goalSize; i++) {
			AiGoal nextGoal = goalSelector.getGoal(i);
			if (nextGoal.canStart()) {
				if (currentGoal != nextGoal) {
					currentGoal = nextGoal;
					nextGoal.start();
				}
				return true;
			}
		}
		return false;
	}

	public void load(Map<String, Object> data) {
		goalSelector.load(data.get("goals"));
		targetSelector.load(data.get("targetGoals"));
	}

	public void load(DataInput in) throws IOException {
		goalSelector.load(in);
		targetSelector.load(in);
	}

	public void save(DataOutput out) throws IOException {
		goalSelector.save(out);
		targetSelector.save(out);
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
			object.goalSelector = goalSelector.clone();
			object.targetSelector = targetSelector.clone();
			object.currentGoal = null;
			object.target = null;
			return object;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Entity getEntity() {
		return entity;
	}

	public GoalSelector getGoalSelector() {
		return goalSelector;
	}

	public GoalSelector getTargetSelector() {
		return targetSelector;
	}

	public AiGoal getCurrentGoal() {
		return currentGoal;
	}

	public void setTarget(@Nullable Entity target) {
		this.target = target;
	}

	@Nullable
	public Entity getTarget() {
		return target;
	}
}
