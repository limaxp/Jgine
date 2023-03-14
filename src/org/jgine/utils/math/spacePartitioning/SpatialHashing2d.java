package org.jgine.utils.math.spacePartitioning;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.jgine.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.core.entity.Entity;
import org.jgine.utils.math.FastMath;

/**
 * Basic spatial hashing implementation for 2d. Spatial hashing divides space
 * into a grid of cells. Cell size might severely impact performance since cells
 * object count should be generally low.
 */
public class SpatialHashing2d implements SpacePartitioning {

	private int xMin;
	private int yMin;
	private int xMax;
	private int yMax;
	private int cols;
	private int rows;
	private List<Entity>[] tiles;

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
			tiles[i] = new UnorderedIdentityArrayList<Entity>();
	}

	@Override
	public void add(Entity object) {
		tiles[getTilePos(object.transform.getX(), object.transform.getY())].add(object);
	}

	@Override
	public void remove(Entity object) {
		tiles[getTilePos(object.transform.getX(), object.transform.getY())].remove(object);
	}

	@Override
	public void move(Entity object, double xOld, double yOld, double zOld, double xNew, double yNew, double zNew) {
		int oldPos = getTilePos(xOld, yOld);
		int newPos = getTilePos(xNew, yNew);
		if (oldPos != newPos) {
			tiles[oldPos].remove(object);
			tiles[newPos].add(object);
		}
	}

// TODO: collider hashing!
//	public void add(Transform object, Collider collider) {
//		float boundX = object.getBoundX();
//		float boundY = object.getBoundX();
//		int firstX = getTileX(object.getX() - boundX);
//		int firstY = getTileY(object.getY() - boundY);
//		int lastX = getTileX(object.getX() + boundX);
//		int lastY = getTileY(object.getY() + boundY);
//		for (int x = firstX; x <= lastX; x++) {
//			for (int y = firstY; y <= lastY; y++) {
//				tiles[x + y * cols].add(object);
//			}
//		}
//	}

	@Override
	public void forEach(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax,
			Consumer<Entity> func) {
		get(xMin, yMin, zMin, xMax, yMax, zMax).forEach(func);
	}

	@Override
	public Collection<Entity> get(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		Set<Entity> result = new HashSet<Entity>();
		int firstX = getTileX(xMin);
		int firstY = getTileY(yMin);
		int lastX = getTileX(xMax);
		int lastY = getTileY(yMax);
		for (int x = firstX; x <= lastX; x++) {
			for (int y = firstY; y <= lastY; y++) {
				for (Entity object : tiles[x + y * cols]) {
					if (object.transform.getX() >= xMin && object.transform.getY() >= yMin
							&& object.transform.getX() < xMax && object.transform.getY() < yMax)
						result.add(object);
				}
			}
		}
		return result;
	}

	@Override
	public Entity get(double x, double y, double z, Entity opt_default) {
		for (Entity object : tiles[getTilePos(x, y)]) {
			if (object.transform.getX() == x && object.transform.getY() == y)
				return object;
		}
		return opt_default;
	}

	@Override
	public void clear() {
		for (int i = 0; i < tiles.length; i++)
			tiles[i].clear();
	}

	@Override
	public SpacePartitioningType<?> getType() {
		return SpacePartitioningTypes.SPATIAL_HASHING_2D;
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
