package org.jgine.utils.id;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A generator for 32-bit identifiers. Identifiers consist of index and
 * generation. Index is a 24-bit value while generation uses the other 8-bit. So
 * there are up to 16777215 identifiers possible but you can also specify a
 * lower maximum. Generation is used to check if id is still in use (alive).
 * Will recycle freed identifiers!
 * <p>
 * <strong>Identifiers must be freed after use!</strong>
 */
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
	private int minimumFreeIndices;

	public IdGenerator() {
		this(0, MAX_ID);
	}

	public IdGenerator(int maxId) {
		this(0, maxId);
	}

	public IdGenerator(int startId, int maxId) {
		generation = new byte[maxId];
		size = startId;
		minimumFreeIndices = Math.min(maxId - 2 - startId, MINIMUM_FREE_INDICES);
		freeIndices = new ArrayDeque<Integer>(minimumFreeIndices + 1000);
	}

	public int generate() {
		int index;
		if (freeIndices.size() > minimumFreeIndices) {
			index = freeIndices.poll();
			return id(index, generation[index]);
		} else {
			generation[index = size++] = (byte) 0;
			return index;
		}
	}

	public int free(int id) {
		int index = index(id);
		freeIndices.add(index);
		generation[index] = (byte) (generation[index] + 1);
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
