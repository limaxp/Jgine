package jgine.utils.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jgine.utils.collection.list.UnorderedIdentityArrayList;
import jgine.utils.math.FastMath;

/**
 * Basic spatial hashing implementation for 2d. Spatial hashing divides space
 * into a grid of cells. Cell size might severely impact performance since cell
 * object count should be generally low.
 * <p>
 * numCells must be a power of 2!
 */
public class SpatialHashing2d<T> implements SpacePartitioning<T> {

	private int numCells;
	private int sizeCells;
	private double invSizeCells;
	private List<T>[] tiles;

	public SpatialHashing2d() {
	}

	public SpatialHashing2d(int numCells, int sizeCells) {
		this.numCells = numCells;
		this.sizeCells = sizeCells;
		if (numCells % 2 != 0)
			throw new IllegalArgumentException("numCells must be a power of 2!");
		init();
	}

	@SuppressWarnings("unchecked")
	protected void init() {
		this.invSizeCells = 1 / (double) sizeCells;
		tiles = new List[numCells];
		for (int i = 0; i < numCells; i++)
			tiles[i] = new UnorderedIdentityArrayList<>();
	}

	@Override
	public void add(T object, double x, double y, double z) {
		tiles[hash(cell(x), cell(y))].add(object);
	}

	@Override
	public void add(T object, double x, double y, double z, double r) {
		int firstX = cell(x - r);
		int firstY = cell(y - r);
		int lastX = cell(x + r);
		int lastY = cell(y + r);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[hash(xTile, yTile)].add(object);
	}

	@Override
	public void remove(T object, double x, double y, double z) {
		tiles[hash(cell(x), cell(y))].remove(object);
	}

	@Override
	public void remove(T object, double x, double y, double z, double r) {
		int firstX = cell(x - r);
		int firstY = cell(y - r);
		int lastX = cell(x + r);
		int lastY = cell(y + r);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[hash(xTile, yTile)].remove(object);
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
		int oldPos = hash(cell(xOld), cell(yOld));
		int newPos = hash(cell(xNew), cell(yNew));
		if (oldPos != newPos) {
			tiles[oldPos].remove(object);
			tiles[newPos].add(object);
		}
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double rOld, double xNew, double yNew,
			double zNew, double rNew) {
		int firstX = cell(xOld - rOld);
		int firstY = cell(yOld - rOld);
		int lastX = cell(xOld + rOld);
		int lastY = cell(yOld + rOld);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[hash(xTile, yTile)].remove(object);

		firstX = cell(xNew - rNew);
		firstY = cell(yNew - rNew);
		lastX = cell(xNew + rNew);
		lastY = cell(yNew + rNew);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[hash(xTile, yTile)].add(object);
	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<T> func) {
		get(xMin, yMin, zMin, xMax, yMax, zMax, new HashSet<>()).forEach(func);
	}

	@Override
	public Set<T> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, Set<T> target) {
		int firstX = cell(xMin);
		int firstY = cell(yMin);
		int lastX = cell(xMax);
		int lastY = cell(yMax);
		for (int x = firstX; x <= lastX; x++)
			for (int y = firstY; y <= lastY; y++)
				for (T object : tiles[hash(x, y)])
					target.add(object);
		return target;
	}

	@Override
	public void clear() {
		for (int i = 0; i < tiles.length; i++)
			tiles[i].clear();
	}

	@Override
	public void load(DataInput in) throws IOException {
		this.numCells = in.readInt();
		this.sizeCells = in.readInt();
		init();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(numCells);
		out.writeInt(sizeCells);
	}

	public int cell(double v) {
		return (int) FastMath.floor(v * invSizeCells);
	}

	public int hash(int x, int y) {
		int h = (x * 73856093) ^ (y * 19349663);
		return Math.abs(h) & (this.numCells - 1);
	}

	public int getNumCells() {
		return numCells;
	}

	public int getSizeCells() {
		return sizeCells;
	}

	public double getInvSizeCells() {
		return invSizeCells;
	}
}
