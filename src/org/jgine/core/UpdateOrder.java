package org.jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import org.jgine.collection.list.IntList;
import org.jgine.collection.list.arrayList.IntArrayList;
import org.jgine.core.manager.SystemManager;
import org.jgine.system.EngineSystem;

/**
 * Defines an update or render order for a {@link Scene}. Use add() methods to
 * add a system and its required {@link EngineSystem}<code>s</code>.
 */
public class UpdateOrder {

	private IntList start;
	private IntList[] before;
	private IntList[] after;
	private int size;

	public UpdateOrder() {
		start = new IntArrayList();
		int size = SystemManager.getSize();
		before = new IntList[size];
		after = new IntList[size];
		for (int i = 0; i < size; i++) {
			before[i] = new IntArrayList();
			after[i] = new IntArrayList();
		}
	}

	public void add(EngineSystem system, Collection<EngineSystem> before) {
		ensureCapacity();
		int systemId = system.getId();
		for (EngineSystem b : before) {
			int beforeId = b.getId();
			this.before[systemId].add(beforeId);
			this.after[beforeId].add(systemId);
		}
		size++;
	}

	public void add(int system, Collection<Integer> before) {
		ensureCapacity();
		for (int beforeId : before) {
			this.before[system].add(beforeId);
			this.after[beforeId].add(system);
		}
		size++;
	}

	public void add(EngineSystem system, EngineSystem... before) {
		ensureCapacity();
		int systemId = system.getId();
		for (int i = 0; i < before.length; i++) {
			int beforeId = before[i].getId();
			this.before[systemId].add(beforeId);
			this.after[beforeId].add(systemId);
		}
		size++;
	}

	public void add(int system, int... before) {
		ensureCapacity();
		for (int i = 0; i < before.length; i++) {
			int beforeId = before[i];
			this.before[system].add(beforeId);
			this.after[beforeId].add(system);
		}
		size++;
	}

	public void add(EngineSystem system, EngineSystem before) {
		add(system.getId(), before.getId());
	}

	public void add(int system, int before) {
		ensureCapacity();
		this.before[system].add(before);
		this.after[before].add(system);
		size++;
	}

	public void add(EngineSystem system) {
		start.add(system.getId());
		size++;
	}

	public void add(int system) {
		start.add(system);
		size++;
	}

	public IntList getStart() {
		return start;
	}

	public IntList getBefore(EngineSystem system) {
		return before[system.getId()];
	}

	public IntList getBefore(int system) {
		return before[system];
	}

	public IntList getAfter(EngineSystem system) {
		return after[system.getId()];
	}

	public IntList getAfter(int system) {
		return after[system];
	}

	public int size() {
		return size;
	}

	public void load(DataInput in) throws IOException {
		int startSize = in.readInt();
		for (int i = 0; i < startSize; i++)
			start.add(in.readInt());

		int dataSize = in.readInt();
		for (int i = 0; i < dataSize; i++) {
			IntList beforeList = before[i];
			int beforeSize = in.readInt();
			for (int j = 0; j < beforeSize; j++)
				beforeList.add(in.readInt());

			IntList afterList = after[i];
			int afterSize = in.readInt();
			for (int j = 0; j < afterSize; j++)
				afterList.add(in.readInt());
		}

		size = in.readInt();
	}

	public void save(DataOutput out) throws IOException {
		int startSize = start.size();
		out.writeInt(startSize);
		for (int i = 0; i < startSize; i++)
			out.writeInt(start.getInt(i));

		int dataSize = before.length;
		out.writeInt(dataSize);
		for (int i = 0; i < dataSize; i++) {
			IntList beforeList = before[i];
			int beforeSize = beforeList.size();
			out.writeInt(beforeSize);
			for (int j = 0; j < beforeSize; j++)
				out.writeInt(beforeList.getInt(j));

			IntList afterList = after[i];
			int afterSize = afterList.size();
			out.writeInt(afterSize);
			for (int j = 0; j < afterSize; j++)
				out.writeInt(afterList.getInt(j));
		}
		out.writeInt(size);
	}

	protected void ensureCapacity() {
		if (before.length < SystemManager.getSize()) {
			IntList[] newBefore = new IntList[SystemManager.getSize()];
			System.arraycopy(before, 0, newBefore, 0, before.length);
			for (int i = before.length - 1; i < newBefore.length; i++)
				newBefore[i] = new IntArrayList();
			before = newBefore;

			IntList[] newAfter = new IntList[SystemManager.getSize()];
			System.arraycopy(after, 0, newAfter, 0, after.length);
			for (int i = after.length - 1; i < newAfter.length; i++)
				newAfter[i] = new IntArrayList();
			after = newAfter;
		}
	}
}
