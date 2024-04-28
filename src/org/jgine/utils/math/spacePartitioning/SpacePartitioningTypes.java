package org.jgine.utils.math.spacePartitioning;

import java.util.function.Supplier;

import org.jgine.utils.Registry;

/**
 * Helper class for {@link SpacePartitioningType} registration.
 */
public class SpacePartitioningTypes {

	public static final SpacePartitioningType<SceneSpacePartitioning> SCENE = a("scene", SceneSpacePartitioning::new);

	public static final SpacePartitioningType<SpatialHashing2d> SPATIAL_HASHING_2D = a("spatial_hashing_2d",
			SpatialHashing2d::new);

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
