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
 */
public class SpatialHashing2d<T> implements SpacePartitioning<T> {

	private int xMin;
	private int yMin;
	private int xMax;
	private int yMax;
	private int cols;
	private int rows;
	private List<T>[] tiles;

	public SpatialHashing2d() {
	}

	public SpatialHashing2d(int xMin, int yMin, int xMax, int yMax, int tileWidth, int tileHeight) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.cols = (xMax - xMin) / tileWidth;
		this.rows = (yMax - yMin) / tileHeight;
		init();
	}

	@SuppressWarnings("unchecked")
	protected void init() {
		int size = cols * rows;
		tiles = new List[size];
		for (int i = 0; i < size; i++)
			tiles[i] = new UnorderedIdentityArrayList<>();
	}

	@Override
	public void add(T object, double x, double y, double z) {
		tiles[getTilePos(x, y)].add(object);
	}

	@Override
	public void add(T object, double x, double y, double z, double r) {
		int firstX = getTileX(x - r);
		int firstY = getTileY(y - r);
		int lastX = getTileX(x + r);
		int lastY = getTileY(y + r);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[xTile + yTile * cols].add(object);
	}

	@Override
	public void remove(T object, double x, double y, double z) {
		tiles[getTilePos(x, y)].remove(object);
	}

	@Override
	public void remove(T object, double x, double y, double z, double r) {
		int firstX = getTileX(x - r);
		int firstY = getTileY(y - r);
		int lastX = getTileX(x + r);
		int lastY = getTileY(y + r);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[xTile + yTile * cols].remove(object);
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
		int oldPos = getTilePos(xOld, yOld);
		int newPos = getTilePos(xNew, yNew);
		if (oldPos != newPos) {
			tiles[oldPos].remove(object);
			tiles[newPos].add(object);
		}
	}

	@Override
	public void move(T object, double xOld, double yOld, double zOld, double rOld, double xNew, double yNew,
			double zNew, double rNew) {
		int firstX = getTileX(xOld - rOld);
		int firstY = getTileY(yOld - rOld);
		int lastX = getTileX(xOld + rOld);
		int lastY = getTileY(yOld + rOld);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[xTile + yTile * cols].remove(object);

		firstX = getTileX(xNew - rNew);
		firstY = getTileY(yNew - rNew);
		lastX = getTileX(xNew + rNew);
		lastY = getTileY(yNew + rNew);
		for (int xTile = firstX; xTile <= lastX; xTile++)
			for (int yTile = firstY; yTile <= lastY; yTile++)
				tiles[xTile + yTile * cols].add(object);
	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<T> func) {
		get(xMin, yMin, zMin, xMax, yMax, zMax, new HashSet<>()).forEach(func);
	}

	@Override
	public Set<T> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, Set<T> target) {
		int firstX = getTileX(xMin);
		int firstY = getTileY(yMin);
		int lastX = getTileX(xMax);
		int lastY = getTileY(yMax);
		for (int x = firstX; x <= lastX; x++)
			for (int y = firstY; y <= lastY; y++)
				for (T object : tiles[x + y * cols])
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
		this.xMin = in.readInt();
		this.yMin = in.readInt();
		this.xMax = in.readInt();
		this.yMax = in.readInt();
		this.cols = in.readInt();
		this.rows = in.readInt();
		init();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(xMin);
		out.writeInt(yMin);
		out.writeInt(xMax);
		out.writeInt(yMax);
		out.writeInt(cols);
		out.writeInt(rows);
	}

	public int getTileX(double x) {
		double xClamp = FastMath.clamp(0.0, 1.0, (x - xMin) / (xMax - xMin));
		return (int) FastMath.floor(xClamp * (cols - 1));
	}

	public int getTileY(double y) {
		double yClamp = FastMath.clamp(0.0, 1.0, (y - yMin) / (yMax - yMin));
		return (int) FastMath.floor(yClamp * (rows - 1));
	}

	public int getTilePos(double x, double y) {
		return getTileX(x) + getTileY(y) * cols;
	}
}
