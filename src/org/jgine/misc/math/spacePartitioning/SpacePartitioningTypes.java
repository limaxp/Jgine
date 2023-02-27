package org.jgine.misc.math.spacePartitioning;

import java.util.function.Supplier;

import org.jgine.misc.utils.registry.Registry;

public class SpacePartitioningTypes {

	public static final SpacePartitioningType<SceneSpacePartitioning> SCENE = a("scene", SceneSpacePartitioning::new);

	public static final SpacePartitioningType<BinarySpaceTree> BINARY_SPACE_TREE = a("binary_tree",
			BinarySpaceTree::new);

	public static final SpacePartitioningType<QuadTree> QUAD_TREE = a("quad_tree", QuadTree::new);

	public static <T extends SpacePartitioning> SpacePartitioningType<T> a(String name, Supplier<T> supplier) {
		SpacePartitioningType<T> type = new SpacePartitioningType<T>(name, supplier);
		type.setId(Registry.SPACE_PARTITIONING_TYPES.register(name, type));
		return type;
	}

	public static SpacePartitioningType<?> get(String name) {
		return Registry.SPACE_PARTITIONING_TYPES.get(name);
	}

	public static SpacePartitioningType<?> get(int id) {
		return Registry.SPACE_PARTITIONING_TYPES.get(id);
	}
}