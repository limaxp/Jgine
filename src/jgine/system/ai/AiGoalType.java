package jgine.system.ai;

import java.util.function.Supplier;

import jgine.system.ai.goals.Idle;
import jgine.system.ai.goals.MeleeAttackTarget;
import jgine.system.ai.goals.MoveToTarget;
import jgine.system.ai.goals.RandomWalk;
import jgine.system.ai.goals.TargetNearFlag;
import jgine.system.ai.goals.TargetNearTag;
import jgine.utils.registry.Registry;

public class AiGoalType<T extends AiGoal> implements Supplier<T> {

	public static final AiGoalType<Idle> IDLE = as("idle", Idle::new);
	public static final AiGoalType<RandomWalk> RANDOM_WALK = as("random_walk", RandomWalk::new);
	public static final AiGoalType<TargetNearTag> TARGET_NEAR_TAG = as("target_near_tag", TargetNearTag::new);
	public static final AiGoalType<TargetNearFlag> TARGET_NEAR_FLAG = as("target_near_flag", TargetNearFlag::new);
	public static final AiGoalType<MoveToTarget> MOVE_TO_TARGET = as("move_to_target", MoveToTarget::new);
	public static final AiGoalType<MeleeAttackTarget> MELEE_ATTACK_TARGET = as("melee_attack_target",
			MeleeAttackTarget::new);

	public static <T extends AiGoal> AiGoalType<T> as(String name, Supplier<T> supplier) {
		AiGoalType<T> type = new AiGoalType<T>(name, supplier);
		type.id = Registry.AI_GOAL.register(name, type);
		return type;
	}

	public final String name;
	private int id;
	private final Supplier<T> supplier;

	public AiGoalType(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	public int id() {
		return id;
	}

	@Override
	public T get() {
		return supplier.get();
	}
}
