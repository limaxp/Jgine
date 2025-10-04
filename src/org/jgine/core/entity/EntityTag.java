package org.jgine.core.entity;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.collection.bitSet.LongBitSet;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Tags that can be attached to an {@link Prefab} to mark it with identifiers.
 * <p>
 * Only supports up to 64 tags since its internally stored as a long! Tags must
 * be registered here before using them!
 * <p>
 * Alternatively use setData(), getData(), etc. methods in {@link Prefab}.
 */
public class EntityTag {

	private static final String[] TAGS = new String[LongBitSet.MAX_SIZE];
	private static final Object2IntMap<String> NAME_MAP = new Object2IntOpenHashMap<String>(64);
	private static int size;

	public static int register(String name) {
		if (size >= LongBitSet.MAX_SIZE - 1)
			throw new ArrayIndexOutOfBoundsException(
					"Maximum tag size reached! Only up to " + LongBitSet.MAX_SIZE + " allowed!");
		int index = size++;
		TAGS[index] = name;
		NAME_MAP.put(name, index);
		return index;
	}

	@Nullable
	public static String get(int index) {
		return TAGS[index];
	}

	public static int get(String name) {
		return NAME_MAP.getOrDefault(name, -1);
	}

	public static int size() {
		return size;
	}
}
