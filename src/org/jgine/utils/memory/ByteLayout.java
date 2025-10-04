package org.jgine.utils.memory;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.Platform;

public class ByteLayout extends LayoutEntry {

	protected static final int DEFAULT_PACK_ALIGNMENT = Platform.get() == Platform.WINDOWS ? 8 : 0x4000_0000;
	protected static final int DEFAULT_ALIGN_AS = 0;

	private final LayoutEntry[] entries;

	ByteLayout(int size, int alignment, boolean forceAlignment, LayoutEntry[] entries) {
		super(size, alignment, forceAlignment);
		this.entries = entries;
	}

	public int offsetof(int member) {
		return entries[member].offset;
	}

	public static LayoutEntry padding(int size, boolean condition) {
		return member(condition ? size : 0, 1);
	}

	public static LayoutEntry member(int size) {
		return member(size, size);
	}

	public static LayoutEntry member(int size, int alignment) {
		return member(size, alignment, false);
	}

	public static LayoutEntry member(int size, int alignment, boolean forceAlignment) {
		return new LayoutEntry(size, alignment, forceAlignment);
	}

	public static LayoutEntry array(int size, int length) {
		return array(size, size, length);
	}

	public static LayoutEntry array(int size, int alignment, int length) {
		return new LayoutEntry(size * length, alignment, false);
	}

	public static LayoutEntry array(int size, int alignment, boolean forceAlignment, int length) {
		return new LayoutEntry(size * length, alignment, forceAlignment);
	}

	public static ByteLayout struct(LayoutEntry... members) {
		return struct(DEFAULT_PACK_ALIGNMENT, DEFAULT_ALIGN_AS, members);
	}

	public static ByteLayout struct(int packAlignment, int alignas, LayoutEntry... members) {
		List<LayoutEntry> struct = new ArrayList<>(members.length);
		int size = 0;
		int alignment = alignas;
		for (LayoutEntry m : members) {
			int memberAlignment = m.getAlignment(packAlignment);
			m.offset = align(size, memberAlignment);
			size = m.offset + m.size;
			alignment = max(alignment, memberAlignment);
			struct.add(m);
			if (m instanceof ByteLayout)
				addNestedMembers((ByteLayout) m, struct, m.offset);
		}
		// tail padding
		size = align(size, alignment);
		return new ByteLayout(size, alignment, alignas != 0, struct.toArray(new LayoutEntry[0]));
	}

	public static ByteLayout union(LayoutEntry... members) {
		return union(DEFAULT_PACK_ALIGNMENT, DEFAULT_ALIGN_AS, members);
	}

	public static ByteLayout union(int packAlignment, int alignas, LayoutEntry... members) {
		List<LayoutEntry> union = new ArrayList<>(members.length);
		int size = 0;
		int alignment = alignas;
		for (LayoutEntry m : members) {
			size = max(size, m.size);
			alignment = max(alignment, m.getAlignment(packAlignment));
			m.offset = 0;
			union.add(m);
			if (m instanceof ByteLayout)
				addNestedMembers((ByteLayout) m, union, 0);
		}
		return new ByteLayout(size, alignment, alignas != 0, union.toArray(new LayoutEntry[0]));
	}

	private static void addNestedMembers(ByteLayout nested, List<LayoutEntry> members, int offset) {
		for (LayoutEntry entry : nested.entries) {
			entry.offset += offset;
			members.add(entry);
		}
	}

	private static int align(int offset, int alignment) {
		return ((offset - 1) | (alignment - 1)) + 1;
	}
}
