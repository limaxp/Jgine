package org.jgine.system.systems.ai;

import java.util.function.Supplier;

import org.jgine.misc.utils.registry.Registry;
import org.jgine.system.systems.ai.goals.GoalIdle;
import org.jgine.system.systems.ai.goals.GoalMoveToTag;
import org.jgine.system.systems.ai.goals.GoalRandomWalk;

public class AiGoalTypes {

	public static final AiGoalType<GoalIdle> IDLE = a("idle", GoalIdle::new);

	public static final AiGoalType<GoalMoveToTag> MOVE = a("move", GoalMoveToTag::new);

	public static final AiGoalType<GoalRandomWalk> WALK_AROUND = a("walk_around", GoalRandomWalk::new);

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
