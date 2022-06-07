package org.jgine.misc.utils.id;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class IdGenerator {

	public static final int MAX_ID = 16777215;
	private static final short MINIMUM_FREE_INDICES = 1024;

	public final static byte INDEX_BITS = 24;
	public final static int INDEX_MASK = (1 << INDEX_BITS) - 1;
	public final static byte GENERATION_BITS = 8;
	public final static int GENERATION_MASK = (1 << GENERATION_BITS) - 1;

	private final byte[] generation;
	private int size;
	private final Queue<Integer> freeIndices;

	public IdGenerator() {
		this(MAX_ID);
	}

	public IdGenerator(int maxId) {
		generation = new byte[maxId];
		freeIndices = new ArrayBlockingQueue<Integer>(MINIMUM_FREE_INDICES + 1000);
	}

	public int generate() {
		int index;
		if (freeIndices.size() > MINIMUM_FREE_INDICES) {
			index = freeIndices.poll();
			int gen = generation[index] = (byte) (generation[index] + 1);
			return id(index, gen);
		} else {
			generation[index = size++] = (byte) 0;
			return index;
		}
	}

	public int free(int id) {
		int index = index(id);
		freeIndices.add(index);
		return index;
	}

	public boolean isAlive(int id) {
		return generation[index(id)] == generation(id);
	}

	public int getMaxId() {
		return generation.length;
	}

	public static int id(int index, int generation) {
		return 0x00000000 | generation << INDEX_BITS | index;
	}

	public static int index(int id) {
		return id & INDEX_MASK;
	}

	public static int generation(int id) {
		return (id >> INDEX_BITS) & GENERATION_MASK;
	}
}
