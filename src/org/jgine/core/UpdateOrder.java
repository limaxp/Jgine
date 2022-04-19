package org.jgine.core;

import java.util.List;

import org.jgine.misc.collection.list.arrayList.FastArrayList;
import org.jgine.misc.math.FastMath;
import org.jgine.system.EngineSystem;

@SuppressWarnings("unchecked")
public class UpdateOrder {

	private FastArrayList<EngineSystem>[] data;

	public UpdateOrder(int size) {
		data = new FastArrayList[size];
		for (int i = 0; i < size; i++)
			data[i] = new FastArrayList<EngineSystem>();
	}

	public void setSize(int size) {
		FastArrayList<EngineSystem>[] newArray = new FastArrayList[size];
		int copySize = FastMath.min(size, data.length);
		System.arraycopy(data, 0, newArray, 0, copySize);
		data = newArray;
		for (int i = copySize - 1; i < size; i++)
			data[i] = new FastArrayList<EngineSystem>();
	}

	public int size() {
		return data.length;
	}

	public void set(int index, EngineSystem... systems) {
		data[index].setArray(systems);
	}

	public List<EngineSystem> get(int index) {
		return data[index];
	}

	public void add(int index, EngineSystem system) {
		data[index].add(system);
	}

	public void remove(int index, EngineSystem system) {
		data[index].remove(system);
	}

	public EngineSystem get(int index, int subIndex) {
		return data[index].get(subIndex);
	}
}
