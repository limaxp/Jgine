package org.jgine.core.entity;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.collection.bitSet.LongBitSet;

/**
 * Tags that can be attached to an {@link Prefab} to mark it with identifiers.
 * <p>
 * Only supports up to 64 tags since its internally stored as a long! Tags must
 * be registered here before using them!
 * <p>
 * Alternatively use setData(), getData(), etc. methods in {@link Prefab}.
 */
public class EntityTag {

	public static final int MAX_SIZE = LongBitSet.MAX_SIZE;
	private static final String[] TAGS = new String[MAX_SIZE];
	private static final Map<String, Integer> NAME_MAP = new HashMap<String, Integer>();
	private static int size = 1;

	public static int register(String name) {
		if (size >= MAX_SIZE - 1)
			throw new IllegalArgumentException("Maximum tag size reached! Only up to " + MAX_SIZE + " allowed!");
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
		return NAME_MAP.getOrDefault(name, 0);
	}

	public static int size() {
		return size;
	}
}
