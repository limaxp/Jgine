package org.jgine.system.systems.graphic;

import org.jgine.render.mesh.Model;
import org.jgine.system.SystemObject;

public class GraphicObject implements SystemObject, Cloneable {

	protected Model model;

	public GraphicObject() {
	}

	public GraphicObject(Model model) {
		this.model = model;
	}

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

	public void setModel(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}
}
