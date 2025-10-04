package org.jgine.utils.memory;

import static java.lang.Math.min;

public class LayoutEntry {

	public final int size;
	public final int alignment;
	public final boolean forceAlignment;
	int offset;

	LayoutEntry(int size, int alignment, boolean forceAlignment) {
		this.size = size;
		this.alignment = alignment;
		this.forceAlignment = forceAlignment;
	}

	public int getAlignment(int packAlignment) {
		return forceAlignment ? alignment : min(alignment, packAlignment);
	}
}
