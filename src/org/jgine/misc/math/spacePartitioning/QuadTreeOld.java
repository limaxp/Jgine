package org.jgine.misc.math.spacePartitioning;

import java.util.ArrayList;
import java.util.Collection;

import org.jgine.misc.math.vector.Vector2f;

public class QuadTreeOld {

	private static final int MAX_OBJECTS = 5;
	private static final int MAX_LEVELS = 5;

	private int level;
	public ArrayList<Object> objects;
	private Vector2f pos;
	private int width;
	private int height;
	private QuadTreeOld[] nodes;

	public QuadTreeOld(int worldWidth, int worldHeight) {
		this(0, Vector2f.NULL, worldWidth, worldHeight);
	}

	public QuadTreeOld(int pLevel, Vector2f pos, int width, int height) {
		level = pLevel;
		this.pos = pos;
		this.width = width;
		this.height = height;
		objects = new ArrayList<Object>();
		nodes = new QuadTreeOld[4];
	}

	public void add(Object gameObject) {
		if (nodes[0] != null) {
			int index = getIndex(gameObject);
			if (index != -1) {
				nodes[index].add(gameObject);
				return;
			}
		}

		objects.add(gameObject);

		if (objects.size() > QuadTreeOld.MAX_OBJECTS && level < QuadTreeOld.MAX_LEVELS) {
			if (nodes[0] == null)
				split();

			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i));
				if (index != -1) {
					nodes[index].add(objects.get(i));
					objects.remove(i);
				} else
					i++;
			}
		}
	}

	private void split() {
		int subWidth = (int) (width / 2);
		int subHeight = (int) (height / 2);
		nodes[0] = new QuadTreeOld(level + 1, new Vector2f(pos.x - subWidth / 2, pos.y - subHeight / 2), subWidth,
				subHeight);
		nodes[1] = new QuadTreeOld(level + 1, new Vector2f(pos.x + subWidth / 2, pos.y - subHeight / 2), subWidth,
				subHeight);
		nodes[2] = new QuadTreeOld(level + 1, new Vector2f(pos.x - subWidth / 2, pos.y + subHeight / 2), subWidth,
				subHeight);
		nodes[3] = new QuadTreeOld(level + 1, new Vector2f(pos.x + subWidth / 2, pos.y + subHeight / 2), subWidth,
				subHeight);
	}

	private int getIndex(Object gameObject) {
		// IMPORTANT! commented out becouse of unsolved copy error!

//		int index = -1;
//		float minX = gameObject.collider.center.x - gameObject.collider.getW() / 2;
//		float minY = gameObject.collider.center.y - gameObject.collider.getH() / 2;
//		boolean topQuadrant = (minY < pos.y && minY + gameObject.collider.getH() < pos.y);
//		boolean bottomQuadrant = (minY > pos.y);
//
//		if (minX < pos.x && minX + gameObject.collider.getW() < pos.x) {
//			if (topQuadrant)
//				index = 0;
//			else if (bottomQuadrant)
//				index = 2;
//		} else if (minX > pos.x) {
//			if (topQuadrant)
//				index = 1;
//			else if (bottomQuadrant)
//				index = 3;
//		}
//		return index;
		return 0;
	}

	public Collection<Object> retrieve(Object gameObject) {
		return retrieve(new ArrayList<Object>(), gameObject);
	}

	public Collection<Object> retrieve(ArrayList<Object> returnObjects, Object gameObject) {
		if (this.nodes[0] != null) {
			int index = getIndex(gameObject);
			if (index != -1)
				nodes[index].retrieve(returnObjects, gameObject);
		}

		for (int i = 0; i < objects.size(); i++)
			returnObjects.add(objects.get(i));
		return returnObjects;
	}

	public void clear() {
		objects.clear();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}
}
