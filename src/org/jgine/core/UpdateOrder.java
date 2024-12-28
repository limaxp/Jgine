package org.jgine.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jgine.collection.list.arrayList.IdentityArrayList;
import org.jgine.core.manager.SystemManager;
import org.jgine.system.EngineSystem;

/**
 * Defines an update or render order for a {@link Scene}. Use add() methods to
 * add a system and its required {@link EngineSystem}<code>s</code>.
 */
public class UpdateOrder {

	private List<EngineSystem<?, ?>> start;
	private List<EngineSystem<?, ?>>[] before;
	private List<EngineSystem<?, ?>>[] after;
	private int size;

	@SuppressWarnings("unchecked")
	public UpdateOrder() {
		start = new IdentityArrayList<EngineSystem<?, ?>>();
		int size = SystemManager.getSize();
		before = new List[size];
		after = new List[size];
		for (int i = 0; i < size; i++) {
			before[i] = new IdentityArrayList<EngineSystem<?, ?>>();
			after[i] = new IdentityArrayList<EngineSystem<?, ?>>();
		}
	}

	public void add(EngineSystem<?, ?> system, Collection<EngineSystem<?, ?>> before) {
		int systemId = system.id;
		for (EngineSystem<?, ?> b : before) {
			int beforeId = b.id;
			this.before[systemId].add(b);
			this.after[beforeId].add(system);
		}
		size++;
	}

	public void add(EngineSystem<?, ?> system, EngineSystem<?, ?>... before) {
		int systemId = system.id;
		for (int i = 0; i < before.length; i++) {
			EngineSystem<?, ?> b = before[i];
			int beforeId = b.id;
			this.before[systemId].add(b);
			this.after[beforeId].add(system);
		}
		size++;
	}

	public void add(EngineSystem<?, ?> system, EngineSystem<?, ?> before) {
		this.before[system.id].add(before);
		this.after[before.id].add(system);
		size++;
	}

	public void add(EngineSystem<?, ?> system) {
		start.add(system);
		size++;
	}

	public List<EngineSystem<?, ?>> getStart() {
		return start;
	}

	public List<EngineSystem<?, ?>> getBefore(EngineSystem<?, ?> system) {
		return before[system.id];
	}

	public List<EngineSystem<?, ?>> getAfter(EngineSystem<?, ?> system) {
		return after[system.id];
	}

	public int size() {
		return size;
	}

	public void load(DataInput in) throws IOException {
		int startSize = in.readInt();
		for (int i = 0; i < startSize; i++)
			start.add(SystemManager.get(in.readInt()));

		int dataSize = in.readInt();
		for (int i = 0; i < dataSize; i++) {
			List<EngineSystem<?, ?>> beforeList = before[i];
			int beforeSize = in.readInt();
			for (int j = 0; j < beforeSize; j++)
				beforeList.add(SystemManager.get(in.readInt()));

			List<EngineSystem<?, ?>> afterList = after[i];
			int afterSize = in.readInt();
			for (int j = 0; j < afterSize; j++)
				afterList.add(SystemManager.get(in.readInt()));
		}
		size = in.readInt();
	}

	public void save(DataOutput out) throws IOException {
		int startSize = start.size();
		out.writeInt(startSize);
		for (int i = 0; i < startSize; i++)
			out.writeInt(start.get(i).id);

		int dataSize = before.length;
		out.writeInt(dataSize);
		for (int i = 0; i < dataSize; i++) {
			List<EngineSystem<?, ?>> beforeList = before[i];
			int beforeSize = beforeList.size();
			out.writeInt(beforeSize);
			for (int j = 0; j < beforeSize; j++)
				out.writeInt(beforeList.get(j).id);

			List<EngineSystem<?, ?>> afterList = after[i];
			int afterSize = afterList.size();
			out.writeInt(afterSize);
			for (int j = 0; j < afterSize; j++)
				out.writeInt(afterList.get(j).id);
		}
		out.writeInt(size);
	}
}
