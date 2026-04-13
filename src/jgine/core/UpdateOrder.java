package jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import jgine.system.EngineSystem;
import jgine.utils.registry.Registry;

/**
 * Defines an update order for a {@link Scene}. Use add() methods to add a
 * {@link EngineSystem}<code>s</code>. Not thread save!
 */
public class UpdateOrder implements Cloneable {

	private IntSet start;
	private IntSet[] parents;
	private IntSet[] childs;
	private int size;

	public UpdateOrder() {
		this(Registry.SYSTEM.capacity());
	}

	UpdateOrder(int size) {
		this.start = new IntOpenHashSet();
		this.parents = new IntSet[size];
		this.childs = new IntSet[size];
		for (int i = 0; i < size; i++) {
			this.parents[i] = new IntOpenHashSet();
			this.childs[i] = new IntOpenHashSet();
		}
	}

	public void add(int system) {
		start.add(system);
		size++;
	}

	public void add(int system, int... parents) {
		add(system, parents, new int[0]);
	}

	public void add(int system, int[] parents, int[] childs) {
		for (int i = 0; i < parents.length; i++) {
			int p = parents[i];
			this.childs[p].add(system);
			this.parents[system].add(p);
		}
		for (int i = 0; i < childs.length; i++) {
			int c = childs[i];
			this.childs[system].add(c);
			this.parents[c].add(system);
		}
		size++;
	}

	public IntSet getStart() {
		return start;
	}

	public IntSet getParents(int system) {
		return parents[system];
	}

	public IntSet getChilds(int system) {
		return childs[system];
	}

	public int size() {
		return size;
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

		obj.start = new IntOpenHashSet(start);
		for (int i = 0; i < size; i++) {
			obj.parents[i] = new IntOpenHashSet(parents[i]);
			obj.childs[i] = new IntOpenHashSet(childs[i]);
		}
		return obj;
	}

	public void load(DataInput in) throws IOException {
		int startSize = in.readInt();
		for (int i = 0; i < startSize; i++)
			start.add(in.readInt());

		int dataSize = in.readInt();
		for (int i = 0; i < dataSize; i++) {
			IntSet beforeList = parents[i];
			int beforeSize = in.readInt();
			for (int j = 0; j < beforeSize; j++)
				beforeList.add(in.readInt());

			IntSet afterList = childs[i];
			int afterSize = in.readInt();
			for (int j = 0; j < afterSize; j++)
				afterList.add(in.readInt());
		}
		size = in.readInt();
	}

	public void save(DataOutput out) throws IOException {
		int startSize = start.size();
		out.writeInt(startSize);
		for (int i : start)
			out.writeInt(i);

		int dataSize = parents.length;
		out.writeInt(dataSize);
		for (int i = 0; i < dataSize; i++) {
			IntSet beforeList = parents[i];
			int beforeSize = beforeList.size();
			out.writeInt(beforeSize);
			for (int j : beforeList)
				out.writeInt(j);

			IntSet afterList = childs[i];
			int afterSize = afterList.size();
			out.writeInt(afterSize);
			for (int j : afterList)
				out.writeInt(j);
		}
		out.writeInt(size);
	}
}
