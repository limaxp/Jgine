package org.jgine.system.systems.collision.collider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.systems.collision.Collider;
import org.jgine.system.systems.collision.ColliderType;
import org.jgine.system.systems.collision.ColliderTypes;
import org.jgine.system.systems.collision.Collision;

public class ColliderGroup extends Collider {

	public Collider bounds;
	public List<Collider> collider;

	public ColliderGroup() {
		this.collider = new UnorderedIdentityArrayList<Collider>();
	}

	public ColliderGroup(Collider bounds) {
		this.bounds = bounds;
		this.collider = new UnorderedIdentityArrayList<Collider>();
	}

	public ColliderGroup(Collider bounds, List<Collider> collider) {
		this.bounds = bounds;
		this.collider = collider;
	}

	public ColliderGroup(Collider bounds, Collider... collider) {
		this.bounds = bounds;
		this.collider = Arrays.asList(collider);
	}

	@Override
	public boolean containsPoint(Vector3f pos, Vector3f point) {
		if (!bounds.containsPoint(pos, point))
			return false;
		for (Collider collider : collider)
			if (collider.containsPoint(pos, point))
				return true;
		return false;
	}

	@Override
	public boolean checkCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		if (!bounds.checkCollision(pos, other, otherPos))
			return false;
		for (Collider collider : collider)
			if (collider.checkCollision(pos, other, otherPos))
				return true;
		return false;
	}

	@Nullable
	@Override
	public Collision resolveCollision(Vector3f pos, Collider other, Vector3f otherPos) {
		Collision collsion = null;
		if ((collsion = bounds.resolveCollision(pos, other, otherPos)) != null)
			return collsion;
		for (Collider collider : collider)
			if ((collsion = collider.resolveCollision(pos, other, otherPos)) != null)
				return collsion;
		return collsion;
	}

	@Override
	public ColliderGroup clone() {
		return new ColliderGroup((Collider) bounds.clone(), collider); // TODO deep copy collider!
	}

	@Override
	public void load(Map<String, Object> data) {
		// TODO implement load!
	}

	@Override
	public ColliderType<ColliderGroup> getType() {
		return ColliderTypes.GROUP;
	}

	@Override
	public void render(Vector3f pos) {
		for (Collider collider : collider)
			collider.render(pos);
	}
}
