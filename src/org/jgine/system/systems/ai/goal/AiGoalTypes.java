package org.jgine.system.systems.ai.goal;

import java.util.function.Supplier;

import org.jgine.system.systems.ai.goal.goals.GoalIdle;
import org.jgine.system.systems.ai.goal.goals.GoalMeleeAttackTarget;
import org.jgine.system.systems.ai.goal.goals.GoalMoveToTarget;
import org.jgine.system.systems.ai.goal.goals.GoalRandomWalk;
import org.jgine.system.systems.ai.goal.goals.GoalTargetNearTag;
import org.jgine.utils.Registry;

public class AiGoalTypes {

	public static final AiGoalType<GoalIdle> IDLE = a("idle", GoalIdle::new);

	public static final AiGoalType<GoalRandomWalk> RANDOM_WALK = a("random_walk", GoalRandomWalk::new);

	public static final AiGoalType<GoalTargetNearTag> TARGET_NEAR_TAG = a("target_near_tag", GoalTargetNearTag::new);

	public static final AiGoalType<GoalMoveToTarget> MOVE_TO_TARGET = a("move_to_target", GoalMoveToTarget::new);

	public static final AiGoalType<GoalMeleeAttackTarget> MELEE_ATTACK_TARGET = a("melee_attack_target", GoalMeleeAttackTarget::new);

	public static <T extends AiGoal> AiGoalType<T> a(String name, Supplier<T> supplier) {
		AiGoalType<T> type = new AiGoalType<T>(name, supplier);
		type.setId(Registry.AI_GOAL_TYPES.register(name, type));
		return type;
	}

	public static AiGoalType<?> get(String name) {
		return Registry.AI_GOAL_TYPES.get(name);
	}

	public static AiGoalType<?> get(int id) {
		return Registry.AI_GOAL_TYPES.get(id);
	}
}
