package org.jgine.core.entity;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.collection.bitSet.LongBitSet;

public class EntityTag {

	private static final String[] TAGS = new String[LongBitSet.MAX_SIZE];
	private static final Map<String, Integer> NAME_MAP = new HashMap<String, Integer>();
	private static int size = 1;

	public static int register(String name) {
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
