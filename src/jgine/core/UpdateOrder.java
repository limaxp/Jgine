package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import jgine.system.EngineSystem;
import jgine.utils.ArrayUtils;

/**
 * Defines an update order for a {@link Scene}. Use add() methods to add a
 * {@link EngineSystem}<code>s</code> with its parents and child's.
 */
public final class UpdateOrder implements Cloneable {

	private static final int[] EMPTY_DATA = new int[0];

	private int[] start;
	private int[][] parents;
	private int[][] childs;
	private int size;

	public UpdateOrder() {
		this(Registry.SYSTEM.capacity());
	}

	UpdateOrder(int size) {
		this.start = EMPTY_DATA;
		this.parents = new int[size][];
		this.childs = new int[size][];
		for (int i = 0; i < size; i++) {
			this.parents[i] = EMPTY_DATA;
			this.childs[i] = EMPTY_DATA;
		}
	}

	public void add(int system) {
		start = ArrayUtils.append(start, system);
		size++;
	}

	public void add(int system, int... parents) {
		for (int i = 0; i < parents.length; i++) {
			int parent = parents[i];
			this.childs[parent] = ArrayUtils.append(this.childs[parent], system);
			this.parents[system] = ArrayUtils.append(this.parents[system], parent);
		}
		size++;
	}

	public void add(int system, int[] parents, int... childs) {
		add(system, parents);
		for (int i = 0; i < childs.length; i++) {
			int child = childs[i];
			this.childs[system] = ArrayUtils.append(this.childs[system], child);
			this.parents[child] = ArrayUtils.append(this.parents[child], system);
		}
	}

	public int size() {
		return size;
	}

	int[] getStart() {
		return start;
	}

	int[] getParents(int system) {
		return parents[system];
	}

	int[] getChilds(int system) {
		return childs[system];
	}

	public boolean isStart(int system) {
		return ArrayUtils.contains(start, system);
	}

	public boolean hasParent(int system, int parent) {
		return ArrayUtils.contains(parents[system], parent);
	}

	public boolean hasChild(int system, int child) {
		return ArrayUtils.contains(childs[system], child);
	}

	@Override
	public UpdateOrder clone() {
		UpdateOrder obj;
		try {
			obj = (UpdateOrder) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

		int systemSize = parents.length;
		obj.parents = Arrays.copyOf(parents, systemSize);
		obj.childs = Arrays.copyOf(childs, systemSize);
		return obj;
	}

	public void save(DataOutput out) throws IOException {
		int startSize = start.length;
		out.writeInt(startSize);
		for (int i : start)
			out.writeInt(i);

		int systemSize = parents.length;
		for (int i = 0; i < systemSize; i++) {
			int[] parentList = parents[i];
			if (parentList.length > 0) {
				out.writeInt(i);
				out.writeInt(parentList.length);
				for (int j : parentList)
					out.writeInt(j);
			}
		}
		out.writeInt(-1);

		for (int i = 0; i < systemSize; i++) {
			int[] childList = childs[i];
			if (childList.length > 0) {
				out.writeInt(i);
				out.writeInt(childList.length);
				for (int j : childList)
					out.writeInt(j);
			}
		}
		out.writeInt(-1);
		out.writeInt(size);
	}

	public void load(DataInput in) throws IOException {
		int startSize = in.readInt();
		start = new int[startSize];
		for (int i = 0; i < startSize; i++)
			start[i] = in.readInt();

		int i;
		while ((i = in.readInt()) != -1) {
			int parentSize = in.readInt();
			int[] parentList = parents[i] = new int[parentSize];
			for (int j = 0; j < parentSize; j++)
				parentList[j] = in.readInt();
		}

		while ((i = in.readInt()) != -1) {
			int childSize = in.readInt();
			int[] childList = childs[i] = new int[childSize];
			for (int j = 0; j < childSize; j++)
				childList[j] = in.readInt();
		}
		size = in.readInt();
	}
}
