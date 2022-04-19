package org.jgine.system.systems.transform;

import org.jgine.core.entity.Entity;
import org.jgine.core.manager.UpdateManager;
import org.jgine.misc.math.Transform3f;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.system.SystemObject;

public class Transform extends Transform3f implements SystemObject {

	private Entity entity;

	public Transform() {
		super(Vector3f.NULL, Vector3f.NULL, Vector3f.FULL);
	}

	public Transform(Vector3f pos) {
		super(pos, Vector3f.NULL, Vector3f.FULL);
	}

	public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		super(position, rotation, scale);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public void calculateMatrix() {
		if (!hasChanged)
			return;
		matrix = calculateMatrix(matrix, position, rotation, scale);
		if (entity.hasParent()) {
			Transform parentTransform = entity.getParent().getSystem(TransformSystem.class);
			if (parentTransform != null)
				matrix.mult(parentTransform.getMatrix());
		}
		for (Entity child : entity.getChilds()) {
			Transform childTransform = child.getSystem(TransformSystem.class);
			if (childTransform != null)
				childTransform.setHasChanged(); // TODO this might stall update for 1 tick!
		}
	}

	@Override
	public void setPosition(Vector3f position) {
		super.setPosition(position);
		UpdateManager.update(entity.scene, "physicPosition", entity, position);
	}

	void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
