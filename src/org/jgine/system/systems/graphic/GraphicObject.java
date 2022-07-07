package org.jgine.system.systems.graphic;

import org.jgine.core.entity.Transform;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.system.SystemObject;

public class GraphicObject implements SystemObject, Cloneable {

	protected Transform transform;
	protected Model model;

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
	}

	@Override
	public GraphicObject clone() {
		try {
			return (GraphicObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Transform getTransform() {
		return transform;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}
}
